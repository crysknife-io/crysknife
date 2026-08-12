/*
 * Copyright © 2025 Treblereel
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.crysknife.ui.databinding.generator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

import com.google.auto.common.MoreTypes;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.definition.VariableDefinition;
import io.crysknife.exception.GenerationException;
import io.crysknife.generator.api.ClassMetaInfo;
import io.crysknife.generator.api.Generator;
import io.crysknife.generator.api.IOCGenerator;
import io.crysknife.generator.api.WiringElementType;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.TreeLogger;
import io.crysknife.ui.databinding.annotation.Bindable;
import io.crysknife.ui.databinding.annotation.Bound;

@Generator(priority = 100)
public class BoundDecorator extends IOCGenerator<VariableDefinition> {

  private final Set<String> unbindGenerated = new HashSet<>();

  public BoundDecorator(TreeLogger treeLogger, IOCContext iocContext) {
    super(treeLogger, iocContext);
  }

  @Override
  public void register() {
    iocContext.register(Bound.class, WiringElementType.FIELD_DECORATOR, this);
  }

  @Override
  public void generate(ClassMetaInfo classMetaInfo, VariableDefinition field) {
    VariableElement element = field.getVariableElement();
    String fieldName = element.getSimpleName().toString();

    Bound bound = element.getAnnotation(Bound.class);
    String property = bound.property().isEmpty() ? fieldName : bound.property();

    BeanDefinition parentBean = field.getEnclosingBeanDefinition();
    VariableDefinition binderField = findBinderField(parentBean);
    String binderFieldName = binderField.getVariableElement().getSimpleName().toString();

    TypeMirror erasedFieldType = types.erasure(element.asType());
    boolean isListComponent = erasedFieldType.toString()
        .equals("io.crysknife.ui.databinding.api.ListComponent");

    TypeElement modelType = extractModelType(binderField, parentBean);
    if (modelType != null && !isListComponent) {
      validateProperty(modelType, property, fieldName, parentBean);
    }

    if (isListComponent) {
      classMetaInfo.addToDoInitInstance(() -> String.format(
          "instance.%s.bindList(instance.%s, \"%s\");",
          binderFieldName, fieldName, property));
    } else {
      classMetaInfo.addToDoInitInstance(() -> String.format(
          "instance.%s.bind(instance.%s, \"%s\");",
          binderFieldName, fieldName, property));
    }

    String beanKey = parentBean.getType().toString();
    if (unbindGenerated.add(beanKey)) {
      classMetaInfo.addToOnDestroy(() -> String.format(
          "instance.%s.unbind();", binderFieldName));
    }
  }

  private VariableDefinition findBinderField(BeanDefinition bean) {
    return bean.getFields().stream()
        .filter(f -> {
          TypeMirror erasedType = types.erasure(f.getVariableElement().asType());
          return erasedType.toString().equals(
              "io.crysknife.ui.databinding.api.DataBinder");
        })
        .findFirst()
        .orElseThrow(() -> new GenerationException(
            "@Bound field requires @Inject DataBinder<?> field in "
                + bean.getQualifiedName()));
  }

  private TypeElement extractModelType(VariableDefinition binderField,
      BeanDefinition parentBean) {
    TypeMirror binderType = binderField.getVariableElement().asType();
    if (binderType instanceof DeclaredType) {
      List<? extends TypeMirror> typeArgs = ((DeclaredType) binderType).getTypeArguments();
      if (!typeArgs.isEmpty()) {
        TypeMirror modelMirror = typeArgs.get(0);
        if (modelMirror.getKind() == TypeKind.DECLARED) {
          TypeElement modelElement = MoreTypes.asTypeElement(modelMirror);
          if (modelElement.getAnnotation(Bindable.class) == null) {
            throw new GenerationException(
                "DataBinder model type " + modelElement.getQualifiedName()
                    + " must be annotated with @Bindable (in "
                    + parentBean.getQualifiedName() + ")");
          }
          return modelElement;
        }
      }
    }
    return null;
  }

  private void validateProperty(TypeElement modelType, String property,
      String boundFieldName, BeanDefinition parentBean) {
    int dot = property.indexOf('.');
    if (dot >= 0) {
      String head = property.substring(0, dot);
      String tail = property.substring(dot + 1);

      TypeElement nestedType = findPropertyType(modelType, head);
      if (nestedType == null) {
        throw new GenerationException(
            "@Bound field '" + boundFieldName + "' references property '" + head
                + "' which does not exist in " + modelType.getQualifiedName()
                + " (in " + parentBean.getQualifiedName() + ")");
      }
      if (nestedType.getAnnotation(Bindable.class) == null) {
        throw new GenerationException(
            "@Bound field '" + boundFieldName + "' uses dot-notation '"
                + property + "' but " + nestedType.getQualifiedName()
                + " is not @Bindable (in " + parentBean.getQualifiedName() + ")");
      }
      validateProperty(nestedType, tail, boundFieldName, parentBean);
    } else {
      if (!hasProperty(modelType, property)) {
        throw new GenerationException(
            "@Bound field '" + boundFieldName + "' references property '"
                + property + "' which does not exist in "
                + modelType.getQualifiedName()
                + " (in " + parentBean.getQualifiedName() + ")");
      }
    }
  }

  private boolean hasProperty(TypeElement type, String propertyName) {
    String capitalizedName =
        propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
    String setterName = "set" + capitalizedName;

    for (ExecutableElement method : getAllMethods(type)) {
      String name = method.getSimpleName().toString();
      if (name.equals(setterName) && method.getParameters().size() == 1
          && method.getReturnType().getKind() == TypeKind.VOID) {
        return true;
      }
    }
    return false;
  }

  private TypeElement findPropertyType(TypeElement type, String propertyName) {
    String capitalizedName =
        propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
    String getterName = "get" + capitalizedName;
    String boolGetterName = "is" + capitalizedName;

    for (ExecutableElement method : getAllMethods(type)) {
      String name = method.getSimpleName().toString();
      if ((name.equals(getterName) || name.equals(boolGetterName))
          && method.getParameters().isEmpty()
          && method.getReturnType().getKind() != TypeKind.VOID) {
        TypeMirror returnType = method.getReturnType();
        if (returnType.getKind() == TypeKind.DECLARED) {
          return MoreTypes.asTypeElement(types.erasure(returnType));
        }
        return null;
      }
    }
    return null;
  }

  private List<ExecutableElement> getAllMethods(TypeElement typeElement) {
    return ElementFilter.methodsIn(elements.getAllMembers(typeElement))
        .stream()
        .filter(m -> !m.getModifiers().contains(Modifier.STATIC))
        .filter(m -> !m.getModifiers().contains(Modifier.PRIVATE))
        .filter(m -> !m.getEnclosingElement().toString().equals("java.lang.Object"))
        .collect(java.util.stream.Collectors.toList());
  }
}
