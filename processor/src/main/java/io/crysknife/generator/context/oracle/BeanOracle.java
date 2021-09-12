/*
 * Copyright © 2021 Treblereel
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

package io.crysknife.generator.context.oracle;

import com.google.auto.common.MoreTypes;
import io.crysknife.definition.InjectableVariableDefinition;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.definition.UnscopedBeanDefinition;
import io.crysknife.util.Utils;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Specializes;
import javax.enterprise.inject.Typed;
import javax.inject.Named;
import javax.inject.Qualifier;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 9/5/21
 */
public class BeanOracle {

  private final IOCContext context;

  public BeanOracle(IOCContext context) {
    this.context = context;
  }

  public BeanDefinition guess(InjectableVariableDefinition point) {
    Named named = point.getVariableElement().getAnnotation(Named.class);
    Set<AnnotationMirror> qualifiers = getAnnotationMirrors(point);
    boolean isInterfaceOrAbstractClass =
        isInterfaceOrAbstractClass(point.getVariableElement().asType());

    TypeMirror beanTypeMirror =
        context.getGenerationContext().getTypes().erasure(point.getVariableElement().asType());

    if (isInterfaceOrAbstractClass) {
      if (named != null) {
        Optional<BeanDefinition> candidate = processName(point, named);
        if (candidate.isPresent()) {
          return candidate.get();
        }
      }

      if (!qualifiers.isEmpty()) {
        Optional<BeanDefinition> candidate = processQualifiers(point, qualifiers);
        if (candidate.isPresent()) {
          return candidate.get();
        }
      }

      Optional<BeanDefinition> candidate =
          asInterfaceOrAbstractClass(point.getVariableElement().asType());
      if (candidate.isPresent()) {
        return candidate.get();
      }
    }

    // Case 2: simple injection case, known type
    if (context.getBeans().containsKey(beanTypeMirror)) {
      BeanDefinition simpleInjectionCaseCandidate = context.getBeans().get(beanTypeMirror);
      if (simpleInjectionCaseCandidate.getIocGenerator().isPresent()) {
        return simpleInjectionCaseCandidate;
      }
    }

    if (isUnscopedBean(beanTypeMirror)) {
      return new UnscopedBeanDefinition(beanTypeMirror, context);
    }

    return null;
  }

  private boolean isUnscopedBean(TypeMirror beanTypeMirror) {
    TypeElement type = MoreTypes.asTypeElement(beanTypeMirror);

    if (type.getKind().isClass() && !type.getModifiers().contains(Modifier.ABSTRACT)
        && type.getModifiers().contains(Modifier.PUBLIC)) {
      Set<ExecutableElement> constructors = ElementFilter.methodsIn(type.getEnclosedElements())
          .stream().filter(elm -> elm.getKind().equals(ElementKind.CONSTRUCTOR))
          .collect(Collectors.toSet());
      if (constructors.isEmpty()) {
        return true;
      }

      if (constructors.stream().filter(elm -> elm.getParameters().isEmpty())
          .filter(elm -> elm.getModifiers().contains(Modifier.PUBLIC)).count() == 1) {
        return true;
      }
    }

    return false;
  }

  public Optional<BeanDefinition> guessDefaultImpl(TypeMirror point) {
    return asInterfaceOrAbstractClass(point);
  }

  private Optional<BeanDefinition> asInterfaceOrAbstractClass(TypeMirror point) {
    Set<BeanDefinition> subclasses = getSubClasses(point);

    Set<BeanDefinition> types = subclasses.stream()
        .filter(bean -> !isInterfaceOrAbstractClass(bean.getType())).collect(Collectors.toSet());
    // Case TheOnlyOneImpl
    if (types.size() == 1) {
      return types.stream().findFirst();
    } else if (types.size() > 1) {
      // Case @Default
      Set<BeanDefinition> maybeDefault = types.stream()
          .filter(
              elm -> MoreTypes.asTypeElement(elm.getType()).getAnnotation(Default.class) != null)
          .collect(Collectors.toSet());
      if (maybeDefault.size() == 1) {
        return maybeDefault.stream().findFirst();
      }
      // Case @Specializes
      Set<BeanDefinition> maybeSpecializes = types.stream().filter(
          elm -> MoreTypes.asTypeElement(elm.getType()).getAnnotation(Specializes.class) != null)
          .collect(Collectors.toSet());
      if (maybeSpecializes.size() == 1) {
        return maybeSpecializes.stream().findFirst();
      }
      // Case @Typed
      Set<BeanDefinition> maybeTyped = types.stream()
          .filter(elm -> MoreTypes.asTypeElement(elm.getType()).getAnnotation(Typed.class) != null)
          .collect(Collectors.toSet());
      if (!maybeTyped.isEmpty()) {
        TypeMirror mirror = context.getGenerationContext().getTypes().erasure(point);
        for (BeanDefinition typed : maybeTyped) {
          Optional<List<TypeMirror>> annotations = getTypedAnnotationValues(typed.getType());
          if (annotations.isPresent()) {
            if (annotations.get().contains(mirror)) {
              return Optional.of(typed);
            }
          }
        }
      }
    }
    return Optional.empty();
  }

  private Optional<List<TypeMirror>> getTypedAnnotationValues(TypeMirror typeMirror) {
    try {
      MoreTypes.asTypeElement(typeMirror).getAnnotation(Typed.class).value();
    } catch (MirroredTypesException e) {
      return Optional.of((List<TypeMirror>) e.getTypeMirrors());
    }
    return Optional.empty();
  }

  private Optional<BeanDefinition> processQualifiers(InjectableVariableDefinition point,
      Set<AnnotationMirror> qualifiers) {
    Set<BeanDefinition> subclasses = getSubClasses(point.getVariableElement().asType());

    List<String> temp =
        qualifiers.stream().map(a -> a.getAnnotationType().toString()).collect(Collectors.toList());
    String[] annotations = temp.toArray(new String[temp.size()]);

    return subclasses.stream().filter(bean -> !isInterfaceOrAbstractClass(bean.getType()))
        .filter(q -> Utils.containsAnnotation(MoreTypes.asTypeElement(q.getType()), annotations))
        .findFirst();

  }

  private Optional<BeanDefinition> processName(InjectableVariableDefinition point, Named named) {
    Set<BeanDefinition> subclasses = getSubClasses(point.getVariableElement().asType());
    return subclasses.stream().filter(bean -> !isInterfaceOrAbstractClass(bean.getType()))
        .filter(elm -> (MoreTypes.asTypeElement(elm.getType()).getAnnotation(Named.class) != null
            && MoreTypes.asTypeElement(elm.getType()).getAnnotation(Named.class).value()
                .equals(named.value())))
        .findFirst();
  }

  private Set<BeanDefinition> getSubClasses(TypeMirror point) {
    TypeMirror beanTypeMirror = context.getGenerationContext().getTypes().erasure(point);
    BeanDefinition type = context.getBeans().get(beanTypeMirror);
    Set<BeanDefinition> subclasses = new HashSet<>(type.getSubclasses());
    getAllSubtypes(type, subclasses);
    type.getSubclasses().addAll(subclasses);
    return subclasses;
  }

  private Set<BeanDefinition> getAllSubtypes(BeanDefinition beanDefinition,
      Set<BeanDefinition> subclasses) {
    if (!beanDefinition.getSubclasses().isEmpty()) {
      for (BeanDefinition subclass : beanDefinition.getSubclasses()) {
        subclasses.add(subclass);
        getAllSubtypes(subclass, subclasses);
      }
    }
    return subclasses;
  }

  private boolean isInterfaceOrAbstractClass(TypeMirror type) {
    return MoreTypes.asTypeElement(type).getKind().isInterface()
        || MoreTypes.asTypeElement(type).getModifiers().contains(Modifier.ABSTRACT);
  }

  private Set<AnnotationMirror> getAnnotationMirrors(InjectableVariableDefinition point) {
    return point.getVariableElement().getAnnotationMirrors().stream()
        .filter(anno -> anno.getAnnotationType().asElement().getAnnotation(Qualifier.class) != null)
        .collect(Collectors.toSet());
  }
}
