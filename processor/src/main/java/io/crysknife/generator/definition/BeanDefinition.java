/*
 * Copyright © 2020 Treblereel
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

package io.crysknife.generator.definition;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.auto.common.MoreElements;
import io.crysknife.exception.GenerationException;
import io.crysknife.generator.BeanIOCGenerator;
import io.crysknife.generator.IOCGenerator;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.generator.point.ConstructorPoint;
import io.crysknife.generator.point.FieldPoint;
import io.crysknife.util.GenerationUtils;
import io.crysknife.util.Utils;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Qualifier;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 2/20/19
 */
public class BeanDefinition extends Definition {

  protected List<FieldPoint> fieldInjectionPoints = new LinkedList<>();
  protected ConstructorPoint constructorInjectionPoint;
  protected String className;
  protected String classFactoryName;
  protected String packageName;
  protected String qualifiedName;
  protected TypeElement element;
  protected Set<DeclaredType> types = new HashSet<>();
  private BeanDefinition defaultImplementation;

  protected BeanDefinition(TypeElement element) {
    this.element = element;
    this.className = element.getSimpleName().toString();
    this.classFactoryName = Utils.getFactoryClassName(element);
    this.packageName = Utils.getPackageName(element);
    this.qualifiedName = Utils.getQualifiedName(element);
  }

  public static BeanDefinition of(TypeElement element, IOCContext context) {
    if (context.getBeans().containsKey(element)) {
      return context.getBeans().get(element);
    }
    return new BeanDefinitionBuilder(element, context).build();
  }

  public void addExecutableDefinition(IOCGenerator generator, ExecutableDefinition definition) {
    if (!executableDefinitions.containsKey(generator)) {
      executableDefinitions.put(generator, new LinkedList<>());
    }
    executableDefinitions.get(generator).add(0, definition);
  }

  public void setGenerator(IOCGenerator iocGenerator) {
    if (iocGenerator == null) {
      throw new GenerationException("Unable to set generator for " + this.toString());
    } else {
      this.generator = Optional.of(iocGenerator);
    }
  }

  public boolean hasGenerator() {
    return generator != null;
  }

  public void generateDecorators(ClassBuilder builder) {
    super.generateDecorators(builder);

    executableDefinitions.forEach((gen, defs) -> defs.forEach(def -> {
      gen.generateBeanFactory(builder, def);
    }));
  }

  @Override
  public String toString() {
    return "BeanDefinition {" + " generator = [ "
        + (generator.isPresent() ? generator.get().getClass().getCanonicalName() : "") + " ]"
        + " ] , element= [" + element + " ] , dependsOn= [ "
        + dependsOn.stream().map(m -> Utils.getQualifiedName(m.element))
            .collect(Collectors.joining(", "))
        + " ] , executables= [ " + executableDefinitions.values().stream().map(Object::toString)
            .collect(Collectors.joining(", "))
        + " ]}";
  }

  public Expression generateBeanCall(IOCContext context, ClassBuilder builder,
      FieldPoint fieldPoint) {
    if (generator.isPresent()) {
      IOCGenerator iocGenerator = generator.get();
      return ((BeanIOCGenerator) iocGenerator).generateBeanCall(builder, fieldPoint);
    } else {
      if (maybeProcessableAsCommonBean(fieldPoint.getType())) {
        // we ll use direct object construction, lets ignore factory creation
        context.getBlacklist().add(fieldPoint.getType().getQualifiedName().toString());
        context.getGenerationContext().getProcessingEnvironment().getMessager().printMessage(
            Diagnostic.Kind.WARNING,
            String.format(
                "Unable to determine bean type for %s, it will be processed as common bean ",
                fieldPoint.getType().getQualifiedName().toString()));
        return new GenerationUtils(context).wrapCallInstanceImpl(builder,
            new ObjectCreationExpr().setType(new ClassOrInterfaceType()
                .setName(fieldPoint.getType().getQualifiedName().toString())));
      }

      BeanDefinition candidate = maybeHasOnlyOneImplementation(context, fieldPoint);


      if (candidate != null && !context.getGenerationContext().getTypes()
          .isSameType(candidate.getType().asType(), fieldPoint.getType().asType())) {
        defaultImplementation = candidate;
        return candidate.generateBeanCall(context, builder, fieldPoint);
      }

      throw new GenerationException("Unable to find generator for " + fieldPoint.getType() + " at "
          + fieldPoint.getEnclosingElement() + "." + fieldPoint.getField());
    }
  }

  private boolean maybeProcessableAsCommonBean(TypeElement candidate) {
    if (candidate.getKind().isClass() && !candidate.getModifiers().contains(Modifier.ABSTRACT)
        && candidate.getModifiers().contains(Modifier.PUBLIC)) {
      long count = candidate.getEnclosedElements().stream()
          .filter(elm -> elm.getKind().equals(ElementKind.CONSTRUCTOR))
          .filter(elm -> MoreElements.asExecutable(elm).getParameters().isEmpty())
          .filter(elm -> elm.getModifiers().contains(Modifier.PUBLIC)).count();
      if (count == 1) {
        return true;
      }
    }
    return false;
  }

  public void processInjections(IOCContext context) {
    Elements elements = context.getGenerationContext().getElements();
    elements.getAllMembers(element).stream().filter(elm -> elm.getAnnotation(Inject.class) != null)
        .forEach(mem -> {
          if (mem.getAnnotation(Inject.class) != null
              && (mem.getKind().equals(ElementKind.CONSTRUCTOR)
                  || mem.getKind().equals(ElementKind.FIELD))) {
            if (mem.getModifiers().contains(Modifier.STATIC)) {
              throw new GenerationException(String
                  .format("Field [%s] in [%s] must not be STATIC \n", mem, getQualifiedName()));
            }
            if (mem.getKind().equals(ElementKind.CONSTRUCTOR)) {
              ExecutableElement elms = MoreElements.asExecutable(mem);
              constructorInjectionPoint =
                  new ConstructorPoint(element.getQualifiedName().toString(), element);

              for (int i = 0; i < elms.getParameters().size(); i++) {
                FieldPoint field = parseField(elms.getParameters().get(i), context);
                constructorInjectionPoint.addArgument(field);
              }
            } else if (mem.getKind().equals(ElementKind.FIELD)) {
              FieldPoint fiend = parseField(mem, context);
              fieldInjectionPoints.add(fiend);
            }
          }
        });
  }

  public String getQualifiedName() {
    return qualifiedName;
  }

  // TODO refactoring needed here
  private FieldPoint parseField(Element type, IOCContext context) {

    FieldPoint field = FieldPoint.of(MoreElements.asVariable(type));
    BeanDefinition bean = null;

    if (field.isNamed()) {
      context.getTypeElementsByAnnotation(Named.class.getCanonicalName()).stream()
          .filter(element -> element.getKind().isClass())
          .filter(named -> named.getAnnotation(Named.class) != null)
          .filter(named -> named.getAnnotation(Named.class).value().equals(field.getNamed()))
          .findAny()
          .ifPresent(elm -> getDependsOn().add(context.getBeanDefinitionOrCreateAndReturn(elm)));
      bean = context.getBeanDefinitionOrCreateAndReturn(field.getType());
    } else if (context.getQualifiers().containsKey(field.getType())) {
      // TODO what if type has several qualifiers ???
      for (AnnotationMirror mirror : context.getGenerationContext().getElements()
          .getAllAnnotationMirrors(type)) {
        DeclaredType annotationType = mirror.getAnnotationType();
        Qualifier qualifier = annotationType.asElement().getAnnotation(Qualifier.class);
        // exclude all annotations, except Qualifiers
        if (qualifier != null) {
          bean = context.getQualifiers().get(field.getType())
              .get(mirror.getAnnotationType().toString());
          break;
        }
      }
    }

    if (bean == null) {
      bean = context.getBeanDefinitionOrCreateAndReturn(field.getType());
      BeanDefinition candidate = maybeHasOnlyOneImplementation(context, field);
      if (candidate != null) {
        bean.defaultImplementation = candidate;
      }
    }
    dependsOn.add(bean);
    return field;
  }

  private BeanDefinition maybeHasOnlyOneImplementation(IOCContext context, FieldPoint field) {
    if (!field.getType().getKind().isInterface()) {
      return null;
    }

    if (context.getBlacklist().contains(field.getType().getQualifiedName().toString())) {
      return null;
    }

    BeanDefinition bean = null;
    Set<TypeElement> subs = context.getSubClassesOf(field.getType());
    if (subs.size() == 1) {
      TypeElement candidate = subs.iterator().next();

      if (candidate.getKind().isClass() && hasGenerator()) {
        bean = context.getBeanDefinitionOrCreateAndReturn(candidate);
        defaultImplementation = bean;
      }
    } else {
      Set<TypeElement> result = subs.stream()
          .filter(e -> e.getAnnotation(Specializes.class) != null).collect(Collectors.toSet());
      if (result.size() == 1) {
        bean = context.getBeanDefinitionOrCreateAndReturn(result.iterator().next());
      }
    }
    return bean;
  }

  public Set<BeanDefinition> getDependsOn() {
    return dependsOn;
  }

  public TypeElement getType() {
    return element;
  }

  @Override
  public int hashCode() {
    return Objects.hash(Utils.getQualifiedName(element));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BeanDefinition that = (BeanDefinition) o;
    return Objects.equals(Utils.getQualifiedName(element), Utils.getQualifiedName(that.element));
  }

  public String getClassName() {
    return className;
  }

  public String getPackageName() {
    return packageName;
  }

  public Map<IOCGenerator, LinkedList<ExecutableDefinition>> getExecutableDefinitions() {
    return executableDefinitions;
  }

  public String getClassFactoryName() {
    return classFactoryName;
  }

  public List<FieldPoint> getFieldInjectionPoints() {
    return fieldInjectionPoints;
  }

  public Set<DeclaredType> getDeclaredTypes() {
    return types;
  }

  public ConstructorPoint getConstructorInjectionPoint() {
    return constructorInjectionPoint;
  }

  public void setConstructorInjectionPoint(ConstructorPoint constructorInjectionPoint) {
    this.constructorInjectionPoint = constructorInjectionPoint;
  }

  public BeanDefinition getDefaultImplementation() {
    return defaultImplementation;
  }

  public void setDefaultImplementation(BeanDefinition defaultImplementation) {
    this.defaultImplementation = defaultImplementation;
  }

  private static class BeanDefinitionBuilder {

    private BeanDefinition beanDefinition;

    private IOCContext context;

    BeanDefinitionBuilder(TypeElement element, IOCContext context) {
      this.context = context;
      this.beanDefinition = new BeanDefinition(element);
    }

    public BeanDefinition build() {
      return beanDefinition;
    }
  }
}
