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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.generator.IOCGenerator;
import io.crysknife.generator.WiringElementType;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.definition.BeanDefinitionFactory;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/2/19
 */
public class IOCContext {

  private final SetMultimap<IOCGeneratorMeta, IOCGenerator> generators = HashMultimap.create();

  private final Map<TypeMirror, BeanDefinition> beans = new HashMap<>();

  private final GenerationContext generationContext;

  private final List<TypeMirror> orderedBeans = new LinkedList<>();

  private final List<String> buildIn = new ArrayList<>();

  private final Map<String, Set<TypeElement>> classesByAnnotation = new HashMap<>();

  private final Map<String, Set<ExecutableElement>> methodsByAnnotation = new HashMap<>();

  private final Map<String, Set<VariableElement>> fieldsByAnnotation = new HashMap<>();

  private final Map<String, Set<VariableElement>> parametersByAnnotation = new HashMap<>();

  private final BeanDefinitionFactory beanDefinitionFactory;

  public IOCContext(GenerationContext generationContext) {
    this.generationContext = generationContext;
    this.beanDefinitionFactory = new BeanDefinitionFactory(this, null);
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
      BeanDefinition beanDefinition = null;
      try {
        beanDefinition = getBeanDefinitionOrCreateAndReturn(type.asType());
      } catch (UnableToCompleteException e) {
        e.printStackTrace();
      }
      beanDefinition.setIocGenerator(generator);
      getBeans().put(type.asType(), beanDefinition);
      buildIn.add(exactType.getCanonicalName());
    }
  }

  public GenerationContext getGenerationContext() {
    return generationContext;
  }


  public BeanDefinition getBeanDefinitionOrCreateAndReturn(TypeMirror typeElement)
      throws UnableToCompleteException {
    TypeMirror candidate = generationContext.getTypes().erasure(typeElement);
    BeanDefinition beanDefinition = null;
    if (beans.containsKey(candidate)) {
      return beans.get(candidate);
    } else {
      beanDefinition = beanDefinitionFactory.of(candidate);
      beans.put(candidate, beanDefinition);
      // beanDefinition.processInjections(this);
    }

    return beanDefinition;
  }

  public Map<TypeMirror, BeanDefinition> getBeans() {
    return beans;
  }

  public SetMultimap<IOCGeneratorMeta, IOCGenerator> getGenerators() {
    return generators;
  }

  public List<TypeMirror> getOrderedBeans() {
    return orderedBeans;
  }

  public List<String> getBuildIn() {
    return buildIn;
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

    ClassInfoList routeClassInfoList =
        generationContext.getScanResult().getClassesWithAnnotation(annotation);
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

    ClassInfoList routeClassInfoList =
        generationContext.getScanResult().getClassesWithMethodAnnotation(annotation);
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
        generationContext.getScanResult().getClassesWithMethodParameterAnnotation(annotation);
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

  public TypeMirror getTypeMirror(Class clazz) {
    return getTypeMirror(clazz.getCanonicalName());
  }

  public TypeMirror getTypeMirror(String clazz) {
    return generationContext.getElements().getTypeElement(clazz).asType();
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

    ClassInfoList routeClassInfoList =
        generationContext.getScanResult().getClassesWithMethodAnnotation(annotation);
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

  public Optional<IOCGenerator> getGenerator(String annotation, TypeElement type,
      WiringElementType wiringElementType) {
    IOCContext.IOCGeneratorMeta meta =
        new IOCContext.IOCGeneratorMeta(annotation, type, wiringElementType);
    Iterable<IOCGenerator> generators = getGenerators().get(meta);
    if (generators.iterator().hasNext()) {
      return Optional.of(generators.iterator().next());
    }
    return Optional.empty();
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
    public int hashCode() {
      return Objects.hash(annotation, exactType, wiringElementType);
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
    public String toString() {
      return "IOCGeneratorMeta{" + "annotation='" + annotation + '\'' + ", exactType=" + exactType
          + ", wiringElementType=" + wiringElementType + '}';
    }
  }
}
