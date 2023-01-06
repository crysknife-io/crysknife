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

package io.crysknife.util;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.exception.GenerationException;
import io.crysknife.generator.context.IOCContext;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsProperty;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 2/21/19
 */
public class Utils {

  private Utils() {

  }

  public static String getQualifiedFactoryName(TypeMirror bean) {
    return getQualifiedFactoryName(MoreTypes.asTypeElement(bean));
  }

  public static String getQualifiedFactoryName(TypeElement bean) {
    return getPackageName(bean) + "." + getFactoryClassName(bean);
  }

  public static String getPackageName(TypeElement singleton) {
    return MoreElements.getPackage(singleton).getQualifiedName().toString();
  }

  public static String getPackageName(TypeMirror type) {
    return getPackageName(MoreTypes.asTypeElement(type));
  }

  public static String getFactoryClassName(TypeElement bean) {
    return (bean.getEnclosingElement().getKind().equals(ElementKind.PACKAGE) ? ""
        : (bean.getEnclosingElement().getSimpleName() + "_")) + bean.getSimpleName().toString()
        + "_Factory";
  }

  public static String getSimpleClassName(TypeMirror bean) {
    return getSimpleClassName(MoreTypes.asTypeElement(bean));
  }

  public static String getSimpleClassName(TypeElement bean) {
    return (bean.getEnclosingElement().getKind().equals(ElementKind.PACKAGE) ? ""
        : (bean.getEnclosingElement().getSimpleName() + ".")) + bean.getSimpleName().toString();
  }

  public static String getJsFieldName(VariableElement field) {
    if (field.getAnnotation(JsProperty.class) != null) {
      return field.getSimpleName().toString();
    }
    StringBuffer sb = new StringBuffer();
    sb.append("f_");
    sb.append(field.getSimpleName().toString());
    sb.append("__");
    sb.append(((TypeElement) field.getEnclosingElement()).getQualifiedName().toString()
        .replaceAll("\\.", "_"));
    if (field.getModifiers().contains(Modifier.PRIVATE)) {
      sb.append("_");
    }

    return sb.toString();
  }

  private static String maybeErase(String className) {
    if (className.contains("<")) { // type name contains wildcards
      return className.substring(0, className.indexOf("<"));
    }
    return className;
  }

  public static Set<Element> getAnnotatedElements(Elements elements, TypeElement type,
      Class<? extends Annotation> annotation) {
    Set<Element> found = new HashSet<>();
    for (Element e : elements.getAllMembers(type)) {
      if (e.getAnnotation(annotation) != null) {
        found.add(e);
      }
    }
    return found;
  }

  public static String toVariableName(TypeElement injection) {
    return toVariableName(getQualifiedName(injection));
  }

  public static String toVariableName(String name) {
    return name.toLowerCase().replaceAll("\\.", "_");
  }

  public static String getQualifiedName(Element elm) {
    if (elm.getKind().equals(ElementKind.FIELD) || elm.getKind().equals(ElementKind.PARAMETER)) {
      VariableElement variableElement = MoreElements.asVariable(elm);
      DeclaredType declaredType = (DeclaredType) variableElement.asType();
      return declaredType.toString();
    } else if (elm.getKind().equals(ElementKind.CONSTRUCTOR)) {
      ExecutableElement executableElement = MoreElements.asExecutable(elm);
      return executableElement.getEnclosingElement().toString();
    } else if (elm.getKind().isClass() || elm.getKind().isInterface()) {
      TypeElement typeElement = MoreElements.asType(elm);
      return typeElement.getQualifiedName().toString();
    }
    throw new GenerationException("Unable to process bean " + elm.toString());
  }

  public static List<AnnotationMirror> getAllElementQualifierAnnotations(IOCContext context,
      Element element) {
    List<AnnotationMirror> result = new ArrayList<>();
    for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
      if (isAnnotationMirrorOfType(annotationMirror, javax.inject.Named.class.getCanonicalName())) {
        continue;
      }
      if (isAnnotationMirrorOfType(annotationMirror, Default.class.getCanonicalName())) {
        continue;
      }
      for (AnnotationMirror allAnnotationMirror : context.getGenerationContext().getElements()
          .getAllAnnotationMirrors(annotationMirror.getAnnotationType().asElement())) {
        if (isAnnotationMirrorOfType(allAnnotationMirror,
            javax.inject.Qualifier.class.getCanonicalName())) {
          result.add(annotationMirror);
        }
      }
    }
    return result;
  }

  /**
   * @param annotationMirror The annotation mirror
   * @param fqcn the fully qualified class name to check against
   * @return {@code true} if the provided annotation type is of the same type as the provided class,
   *         {@code false} otherwise.
   * @url {https://github.com/hibernate/hibernate-metamodelgen/blob/master/src/main/java/org/hibernate/jpamodelgen/util/TypeUtils.java}
   *      <p>
   *      Returns {@code true} if the provided annotation type is of the same type as the provided
   *      class, {@code false} otherwise. This method uses the string class names for comparison.
   *      See also <a href=
   *      "http://www.retep.org/2009/02/getting-class-values-from-annotations.html">getting-class-values-from-annotations</a>.
   */
  public static boolean isAnnotationMirrorOfType(AnnotationMirror annotationMirror, String fqcn) {
    assert annotationMirror != null;
    assert fqcn != null;
    String annotationClassName = annotationMirror.getAnnotationType().toString();

    return annotationClassName.equals(fqcn);
  }

  /**
   * see: typetools/checker-framework Return all methods declared in the given type or any
   * superclass/interface. Note that no constructors will be returned. TODO: should this use
   * javax.lang.model.util.Elements.getAllMembers(TypeElement) instead of our own getSuperTypes?
   */
  public static Collection<VariableElement> getAllFieldsIn(Elements elements, TypeElement type) {
    Map<String, VariableElement> fields = new LinkedHashMap<>();
    ElementFilter.fieldsIn(type.getEnclosedElements())
        .forEach(field -> fields.put(field.getSimpleName().toString(), field));

    List<TypeElement> alltypes = getSuperTypes(elements, type);
    for (TypeElement atype : alltypes) {
      ElementFilter.fieldsIn(atype.getEnclosedElements()).stream()
          .filter(field -> !fields.containsKey(field.getSimpleName().toString()))
          .forEach(field -> fields.put(field.getSimpleName().toString(), field));
    }
    return fields.values();
  }

  /**
   * see: typetools/checker-framework Determine all type elements for the classes and interfaces
   * referenced in the extends/implements clauses of the given type element. TODO: can we learn from
   * the implementation of com.sun.tools.javac.model.JavacElements.getAllMembers(TypeElement)?
   */
  public static List<TypeElement> getSuperTypes(Elements elements, TypeElement type) {

    List<TypeElement> superelems = new ArrayList<>();
    if (type == null) {
      return superelems;
    }

    // Set up a stack containing type, which is our starting point.
    Deque<TypeElement> stack = new ArrayDeque<>();
    stack.push(type);

    while (!stack.isEmpty()) {
      TypeElement current = stack.pop();

      // For each direct supertype of the current type element, if it
      // hasn't already been visited, push it onto the stack and
      // add it to our superelems set.
      TypeMirror supertypecls = current.getSuperclass();
      if (supertypecls.getKind() != TypeKind.NONE) {
        TypeElement supercls = (TypeElement) ((DeclaredType) supertypecls).asElement();
        if (!superelems.contains(supercls)) {
          stack.push(supercls);
          superelems.add(supercls);
        }
      }
      for (TypeMirror supertypeitf : current.getInterfaces()) {
        TypeElement superitf = (TypeElement) ((DeclaredType) supertypeitf).asElement();
        if (!superelems.contains(superitf)) {
          stack.push(superitf);
          superelems.add(superitf);
        }
      }
    }

    // Include java.lang.Object as implicit superclass for all classes and interfaces.
    TypeElement jlobject = elements.getTypeElement(Object.class.getCanonicalName());
    if (!superelems.contains(jlobject)) {
      superelems.add(jlobject);
    }

    return Collections.unmodifiableList(superelems);
  }

  public static Collection<ExecutableType> getAllTypedMethodsIn(Elements elements, Types types,
      TypeMirror type) {
    return getAllMethodsIn(elements, MoreTypes.asTypeElement(type)).stream()
        .map(e -> types.asMemberOf(MoreTypes.asDeclared(type), e)).map(e -> (ExecutableType) e)
        .collect(Collectors.toSet());
  }

  public static Collection<ExecutableElement> getAllMethodsIn(Elements elements, TypeElement type) {
    Set<ExecutableElement> methods = new LinkedHashSet<>();
    ElementFilter.methodsIn(type.getEnclosedElements()).forEach(method -> methods.add(method));

    List<TypeElement> alltypes = getSuperTypes(elements, type);
    for (TypeElement atype : alltypes) {
      ElementFilter.methodsIn(atype.getEnclosedElements()).stream()
          .filter(method -> !methods.contains(method)).forEach(method -> methods.add(method));
    }
    return methods;
  }

  /**
   * @url {https://github.com/hibernate/hibernate-metamodelgen/blob/master/src/main/java/org/hibernate/jpamodelgen/util/TypeUtils.java}
   */
  public static boolean containsAnnotation(Element element, String... annotations) {
    assert element != null;
    assert annotations != null;

    List<String> annotationClassNames = new ArrayList<>();
    Collections.addAll(annotationClassNames, annotations);

    List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();
    for (AnnotationMirror mirror : annotationMirrors) {
      if (annotationClassNames.contains(mirror.getAnnotationType().toString())) {
        return true;
      }
    }
    return false;
  }

  /**
   * @url {https://github.com/hibernate/hibernate-metamodelgen/blob/master/src/main/java/org/hibernate/jpamodelgen/util/TypeUtils.java}
   */
  public static Object getAnnotationValue(AnnotationMirror annotationMirror,
      String parameterValue) {
    assert annotationMirror != null;
    assert parameterValue != null;

    Object returnValue = null;
    for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror
        .getElementValues().entrySet()) {
      if (parameterValue.equals(entry.getKey().getSimpleName().toString())) {
        returnValue = entry.getValue().getValue();
        break;
      }
    }
    return returnValue;
  }

  public static boolean isDependent(BeanDefinition beanDefinition) {
    String annotation = beanDefinition.getScope().annotationType().getCanonicalName();
    String dependent = Dependent.class.getCanonicalName();
    return annotation.equals(dependent);
  }

}
