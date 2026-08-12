/*
 * Copyright © 2024 Treblereel
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
package io.crysknife.task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.Dependent;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InterceptorBinding;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;

import com.google.auto.common.MoreTypes;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.definition.InterceptorInfo;
import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.generator.api.IOCGenerator;
import io.crysknife.generator.api.WiringElementType;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.TreeLogger;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;

public class InterceptorBindingProcessorTask implements Task {

  private final IOCContext iocContext;
  private final TreeLogger logger;

  public InterceptorBindingProcessorTask(IOCContext iocContext, TreeLogger logger) {
    this.iocContext = iocContext;
    this.logger = logger;
  }

  @Override
  public void execute() {
    Elements elements = iocContext.getGenerationContext().getElements();

    Set<String> bindingAnnotations = findInterceptorBindingAnnotations(elements);
    if (bindingAnnotations.isEmpty()) {
      return;
    }

    Map<String, List<InterceptorInfo>> bindingToInterceptors =
        findInterceptorClasses(elements, bindingAnnotations);
    if (bindingToInterceptors.isEmpty()) {
      return;
    }

    resolveInterceptedMethods(elements, bindingAnnotations, bindingToInterceptors);
  }

  private Set<String> findInterceptorBindingAnnotations(Elements elements) {
    Set<String> bindings = new HashSet<>();

    ClassInfoList bindingClasses = iocContext.getGenerationContext().getScanResult()
        .getClassesWithAnnotation(InterceptorBinding.class.getCanonicalName());
    for (ClassInfo info : bindingClasses) {
      if (info.isAnnotation()) {
        bindings.add(info.getName());
      }
    }

    Set<Element> roundBindings = (Set<Element>) iocContext.getGenerationContext()
        .getRoundEnvironment().getElementsAnnotatedWith(
            elements.getTypeElement(InterceptorBinding.class.getCanonicalName()));
    for (Element elm : roundBindings) {
      if (elm.getKind() == ElementKind.ANNOTATION_TYPE) {
        bindings.add(((TypeElement) elm).getQualifiedName().toString());
      }
    }

    logger.log(TreeLogger.INFO, "interceptor bindings found: " + bindings.size());
    return bindings;
  }

  private Map<String, List<InterceptorInfo>> findInterceptorClasses(Elements elements,
      Set<String> bindingAnnotations) {
    Map<String, List<InterceptorInfo>> result = new HashMap<>();

    Set<TypeElement> interceptorTypes = new HashSet<>();

    ClassInfoList interceptorClasses = iocContext.getGenerationContext().getScanResult()
        .getClassesWithAnnotation(Interceptor.class.getCanonicalName());
    for (ClassInfo info : interceptorClasses) {
      TypeElement type = elements.getTypeElement(info.getName());
      if (type != null) {
        interceptorTypes.add(type);
      }
    }

    Set<Element> roundInterceptors = (Set<Element>) iocContext.getGenerationContext()
        .getRoundEnvironment().getElementsAnnotatedWith(
            elements.getTypeElement(Interceptor.class.getCanonicalName()));
    for (Element elm : roundInterceptors) {
      if (elm.getKind().isClass()) {
        interceptorTypes.add((TypeElement) elm);
      }
    }

    for (TypeElement interceptorType : interceptorTypes) {
      ExecutableElement aroundInvoke = findAroundInvokeMethod(interceptorType);
      if (aroundInvoke == null) {
        continue;
      }

      int priority = getInterceptorPriority(interceptorType);

      ensureInterceptorIsManaged(interceptorType);

      for (AnnotationMirror am : interceptorType.getAnnotationMirrors()) {
        String annoName = am.getAnnotationType().toString();
        if (bindingAnnotations.contains(annoName)) {
          TypeMirror erased = iocContext.getGenerationContext().getTypes()
              .erasure(interceptorType.asType());
          InterceptorInfo info = new InterceptorInfo(erased, aroundInvoke, priority);
          result.computeIfAbsent(annoName, k -> new ArrayList<>()).add(info);
        }
      }
    }

    for (List<InterceptorInfo> list : result.values()) {
      list.sort(Comparator.comparingInt(InterceptorInfo::getPriority));
    }

    logger.log(TreeLogger.INFO, "interceptor classes found: " + interceptorTypes.size());
    return result;
  }

  private ExecutableElement findAroundInvokeMethod(TypeElement type) {
    for (ExecutableElement method : ElementFilter.methodsIn(type.getEnclosedElements())) {
      if (method.getAnnotation(AroundInvoke.class) != null) {
        return method;
      }
    }
    return null;
  }

  private int getInterceptorPriority(TypeElement interceptorType) {
    Priority priority = interceptorType.getAnnotation(Priority.class);
    if (priority != null) {
      return priority.value();
    }
    return Interceptor.Priority.APPLICATION;
  }

  private void ensureInterceptorIsManaged(TypeElement interceptorType) {
    TypeMirror erased = iocContext.getGenerationContext().getTypes()
        .erasure(interceptorType.asType());
    BeanDefinition beanDefinition;
    try {
      beanDefinition = iocContext.getBeanDefinitionOrCreateAndReturn(erased);
    } catch (UnableToCompleteException e) {
      logger.log(TreeLogger.ERROR,
          "Failed to register interceptor bean: " + interceptorType.getQualifiedName());
      return;
    }
    if (!beanDefinition.getIocGenerator().isPresent()) {
      TypeElement objectType = iocContext.getGenerationContext().getElements()
          .getTypeElement(Object.class.getCanonicalName());
      Optional<IOCGenerator> generator = iocContext.getGenerator(
          Dependent.class.getCanonicalName(), objectType, WiringElementType.BEAN);
      generator.ifPresent(beanDefinition::setIocGenerator);
    }
    if (!iocContext.getOrderedBeans().contains(erased)) {
      iocContext.getOrderedBeans().add(erased);
    }
  }

  private void resolveInterceptedMethods(Elements elements, Set<String> bindingAnnotations,
      Map<String, List<InterceptorInfo>> bindingToInterceptors) {
    for (Map.Entry<TypeMirror, BeanDefinition> entry : iocContext.getBeans().entrySet()) {
      BeanDefinition bean = entry.getValue();
      TypeElement typeElement = MoreTypes.asTypeElement(entry.getKey());

      if (typeElement.getAnnotation(Interceptor.class) != null) {
        continue;
      }

      Set<String> classLevelBindings = getBindingAnnotations(typeElement, bindingAnnotations);

      List<ExecutableElement> methods =
          ElementFilter.methodsIn(typeElement.getEnclosedElements());
      for (ExecutableElement method : methods) {
        if (!isInterceptableMethod(method)) {
          continue;
        }

        Set<String> methodBindings = getBindingAnnotations(method, bindingAnnotations);
        methodBindings.addAll(classLevelBindings);

        if (methodBindings.isEmpty()) {
          continue;
        }

        List<InterceptorInfo> chain = new ArrayList<>();
        for (String binding : methodBindings) {
          List<InterceptorInfo> interceptors = bindingToInterceptors.get(binding);
          if (interceptors != null) {
            chain.addAll(interceptors);
          }
        }

        if (!chain.isEmpty()) {
          chain.sort(Comparator.comparingInt(InterceptorInfo::getPriority));
          bean.getInterceptedMethods().put(method, chain);
        }
      }
    }

    long count = iocContext.getBeans().values().stream()
        .mapToLong(b -> b.getInterceptedMethods().size()).sum();
    logger.log(TreeLogger.INFO, "intercepted methods found: " + count);
  }

  private Set<String> getBindingAnnotations(Element element, Set<String> bindingAnnotations) {
    return element.getAnnotationMirrors().stream()
        .map(am -> am.getAnnotationType().toString())
        .filter(bindingAnnotations::contains)
        .collect(Collectors.toSet());
  }

  private boolean isInterceptableMethod(ExecutableElement method) {
    Set<Modifier> modifiers = method.getModifiers();
    if (modifiers.contains(Modifier.PRIVATE) || modifiers.contains(Modifier.STATIC)
        || modifiers.contains(Modifier.FINAL)) {
      return false;
    }
    if (method.getAnnotation(PostConstruct.class) != null
        || method.getAnnotation(PreDestroy.class) != null) {
      return false;
    }
    return method.getKind() == ElementKind.METHOD;
  }
}
