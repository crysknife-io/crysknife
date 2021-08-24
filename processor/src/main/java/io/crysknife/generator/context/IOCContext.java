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

package io.crysknife.generator.context;

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import io.crysknife.exception.GenerationException;
import io.crysknife.generator.IOCGenerator;
import io.crysknife.generator.WiringElementType;
import io.crysknife.generator.definition.BeanDefinition;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/2/19
 */
public class IOCContext {

  private final SetMultimap<IOCGeneratorMeta, IOCGenerator> generators = HashMultimap.create();

  private final Map<TypeElement, BeanDefinition> beans = new HashMap<>();

  private final Map<TypeElement, Map<String, BeanDefinition>> qualifiers = new HashMap<>();

  private final GenerationContext generationContext;

  private final List<TypeElement> orderedBeans = new LinkedList<>();

  private final List<String> blacklist = new ArrayList<>();

  // TODO Temporary solution before ill find out why in j2cl-m-p apt get*AnnotatedWith isn't able to
  // process
  // annotations in external deps
  private final ScanResult scanResult = new ClassGraph().enableAllInfo().scan();

  private final Map<String, Set<TypeElement>> classesByAnnotation = new HashMap<>();

  private final Map<String, Set<ExecutableElement>> methodsByAnnotation = new HashMap<>();

  private final Map<String, Set<VariableElement>> fieldsByAnnotation = new HashMap<>();

  private final Map<String, Set<VariableElement>> parametersByAnnotation = new HashMap<>();

  public IOCContext(GenerationContext generationContext) {
    this.generationContext = generationContext;
  }

  public void register(final Class annotation, final WiringElementType wiringElementType,
      final IOCGenerator generator) {
    register(annotation, Object.class, wiringElementType, generator);
  }

  public void register(final Class annotation, Class exactType,
      final WiringElementType wiringElementType, final IOCGenerator generator) {
    TypeElement type =
        getGenerationContext().getElements().getTypeElement(exactType.getCanonicalName());
    generators.put(new IOCGeneratorMeta(annotation.getCanonicalName(), type, wiringElementType),
        generator);
    if (!exactType.equals(Object.class)) {
      BeanDefinition beanDefinition = getBeanDefinitionOrCreateAndReturn(type);
      beanDefinition.setGenerator(generator);
      getBeans().put(type, beanDefinition);
    }
  }

  public SetMultimap<IOCGeneratorMeta, IOCGenerator> getGenerators() {
    return generators;
  }

  public Map<TypeElement, BeanDefinition> getBeans() {
    return beans;
  }

  public BeanDefinition getBean(TypeElement bean) {
    if (beans.containsKey(bean)) {
      return beans.get(bean);
    }
    throw new GenerationException(bean.toString());
  }

  public GenerationContext getGenerationContext() {
    return generationContext;
  }

  public Map<TypeElement, Map<String, BeanDefinition>> getQualifiers() {
    return qualifiers;
  }

  public List<TypeElement> getOrderedBeans() {
    return orderedBeans;
  }

  public List<String> getBlacklist() {
    return blacklist;
  }

  public BeanDefinition getBeanDefinitionOrCreateAndReturn(TypeElement typeElement) {
    BeanDefinition beanDefinition;
    if (beans.containsKey(typeElement)) {
      return getBeans().get(typeElement);
    } else {
      beanDefinition = BeanDefinition.of(typeElement, this);
      beans.put(typeElement, beanDefinition);
      beanDefinition.processInjections(this);
    }
    return beanDefinition;
  }

  // TODO j2cl-m-p workaround
  public Set<TypeElement> getTypeElementsByAnnotation(String annotation) {
    if (classesByAnnotation.containsKey(annotation)) {
      return classesByAnnotation.get(annotation);
    }

    Elements elements = getGenerationContext().getElements();
    Set<TypeElement> results =
        getElementsByAnnotation(annotation).stream().filter(elm -> (elm instanceof TypeElement))
            .map(element -> ((TypeElement) element)).collect(Collectors.toSet());

    ClassInfoList routeClassInfoList = scanResult.getClassesWithAnnotation(annotation);
    for (ClassInfo routeClassInfo : routeClassInfoList) {
      TypeElement type = elements.getTypeElement(routeClassInfo.getName());
      if (type != null) {
        results.add(type);
      }
    }
    classesByAnnotation.put(annotation, results);
    return results;
  }

  private Set<Element> getElementsByAnnotation(String annotation) {
    Elements elements = getGenerationContext().getElements();
    return (Set<Element>) getGenerationContext().getRoundEnvironment()
        .getElementsAnnotatedWith(elements.getTypeElement(annotation));
  }

  public Set<ExecutableElement> getMethodsByAnnotation(String annotation) {
    if (methodsByAnnotation.containsKey(annotation)) {
      return methodsByAnnotation.get(annotation);
    }

    Elements elements = getGenerationContext().getElements();
    Set<ExecutableElement> results = getGenerationContext().getRoundEnvironment()
        .getElementsAnnotatedWith(elements.getTypeElement(annotation)).stream()
        .filter(elm -> (elm instanceof ExecutableElement))
        .map(element -> ((ExecutableElement) element)).collect(Collectors.toSet());

    ClassInfoList routeClassInfoList = scanResult.getClassesWithMethodAnnotation(annotation);
    for (ClassInfo routeClassInfo : routeClassInfoList) {
      if (!routeClassInfo.getDeclaredMethodInfo().asMap().isEmpty()) {
        TypeElement type = elements.getTypeElement(routeClassInfo.getName());
        type.getEnclosedElements().stream().filter(elm -> (elm instanceof ExecutableElement))
            .filter(elm -> ((ExecutableElement) elm).getAnnotationMirrors().stream()
                .map(a -> a.getAnnotationType().toString()).filter(a -> a.equals(annotation))
                .count() > 0)
            .map(method -> ((ExecutableElement) method)).forEach(results::add);
      }
    }
    methodsByAnnotation.put(annotation, results);
    return results;
  }

  public Set<VariableElement> getParametersByAnnotation(String annotation) {
    if (parametersByAnnotation.containsKey(annotation)) {
      return parametersByAnnotation.get(annotation);
    }

    Elements elements = getGenerationContext().getElements();
    Set<VariableElement> results = getGenerationContext().getRoundEnvironment()
        .getElementsAnnotatedWith(elements.getTypeElement(annotation)).stream()
        .filter(elm -> elm.getKind().equals(ElementKind.PARAMETER))
        .map(element -> ((VariableElement) element)).collect(Collectors.toSet());

    ClassInfoList routeClassInfoList =
        scanResult.getClassesWithMethodParameterAnnotation(annotation);
    for (ClassInfo routeClassInfo : routeClassInfoList) {
      if (!routeClassInfo.getDeclaredMethodInfo().asMap().isEmpty()) {
        TypeElement type = elements.getTypeElement(routeClassInfo.getName());
        for (Element elm : type.getEnclosedElements()) {
          if ((elm instanceof ExecutableElement)) {
            ExecutableElement method = ((ExecutableElement) elm);
            method.getParameters().forEach(param -> param.getAnnotationMirrors().forEach(ano -> {
              if (ano.getAnnotationType().toString().equals(annotation)) {
                results.add(param);
              }
            }));
          }
        }
      }
    }
    parametersByAnnotation.put(annotation, results);
    return results;
  }

  public Set<VariableElement> getFieldsByAnnotation(String annotation) {
    if (fieldsByAnnotation.containsKey(annotation)) {
      return fieldsByAnnotation.get(annotation);
    }

    Elements elements = getGenerationContext().getElements();
    Set<VariableElement> results = getGenerationContext().getRoundEnvironment()
        .getElementsAnnotatedWith(elements.getTypeElement(annotation)).stream()
        .filter(elm -> (elm instanceof VariableElement)).map(element -> ((VariableElement) element))
        .collect(Collectors.toSet());

    ClassInfoList routeClassInfoList = scanResult.getClassesWithMethodAnnotation(annotation);
    for (ClassInfo routeClassInfo : routeClassInfoList) {
      if (!routeClassInfo.getDeclaredFieldInfo().asMap().isEmpty()) {
        TypeElement type = elements.getTypeElement(routeClassInfo.getName());
        if (type != null) {
          type.getEnclosedElements().stream().filter(elm -> (elm instanceof VariableElement))
              .filter(elm -> elm.getAnnotationMirrors().stream()
                  .map(a -> a.getAnnotationType().toString()).filter(a -> a.equals(annotation))
                  .count() > 0)
              .map(method -> ((VariableElement) method)).forEach(results::add);
        }
      }
    }
    fieldsByAnnotation.put(annotation, results);
    return results;
  }

  public Set<TypeElement> getSubClassesOf(TypeElement type) {
    Types types = generationContext.getTypes();
    TypeMirror erased = types.erasure(type.asType());
    return beans.keySet().stream().filter(t -> types.isSubtype(types.erasure(t.asType()), erased)
        && !types.isSameType(types.erasure(t.asType()), erased)).collect(Collectors.toSet());
  }

  public static class IOCGeneratorMeta {

    public final String annotation;
    public final TypeElement exactType;
    public final WiringElementType wiringElementType;

    public IOCGeneratorMeta(String annotation, TypeElement exactType,
        WiringElementType wiringElementType) {
      this.annotation = annotation;
      this.wiringElementType = wiringElementType;
      this.exactType = exactType;
    }

    @Override
    public String toString() {
      return "IOCGeneratorMeta{" + "annotation='" + annotation + '\'' + ", exactType=" + exactType
          + ", wiringElementType=" + wiringElementType + '}';
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof IOCGeneratorMeta)) {
        return false;
      }
      IOCGeneratorMeta that = (IOCGeneratorMeta) o;
      return Objects.equals(annotation, that.annotation)
          && Objects.equals(exactType, that.exactType)
          && wiringElementType == that.wiringElementType;
    }

    @Override
    public int hashCode() {
      return Objects.hash(annotation, exactType, wiringElementType);
    }
  }
}
