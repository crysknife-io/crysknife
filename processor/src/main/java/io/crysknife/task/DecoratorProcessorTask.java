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

import jakarta.annotation.Priority;
import jakarta.decorator.Decorator;
import jakarta.decorator.Delegate;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;

import com.google.auto.common.MoreTypes;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.definition.DecoratorInfo;
import io.crysknife.definition.InjectableVariableDefinition;
import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.generator.api.IOCGenerator;
import io.crysknife.generator.api.WiringElementType;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.TreeLogger;
import io.crysknife.util.TypeUtils;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;

public class DecoratorProcessorTask implements Task {

  private final IOCContext iocContext;
  private final TreeLogger logger;

  public DecoratorProcessorTask(IOCContext iocContext, TreeLogger logger) {
    this.iocContext = iocContext;
    this.logger = logger;
  }

  @Override
  public void execute() {
    Elements elements = iocContext.getGenerationContext().getElements();

    List<DecoratorInfo> allDecorators = findDecoratorClasses(elements);
    if (allDecorators.isEmpty()) {
      return;
    }

    Map<String, List<DecoratorInfo>> decoratorsByDelegateType = groupByDelegateType(allDecorators);
    assignDecoratorsToTargetBeans(elements, decoratorsByDelegateType);
  }

  private List<DecoratorInfo> findDecoratorClasses(Elements elements) {
    Set<TypeElement> decoratorTypes = new HashSet<>();

    ClassInfoList decoratorClasses = iocContext.getGenerationContext().getScanResult()
        .getClassesWithAnnotation(Decorator.class.getCanonicalName());
    for (ClassInfo info : decoratorClasses) {
      TypeElement type = elements.getTypeElement(info.getName());
      if (type != null) {
        decoratorTypes.add(type);
      }
    }

    Set<Element> roundDecorators = (Set<Element>) iocContext.getGenerationContext()
        .getRoundEnvironment().getElementsAnnotatedWith(
            elements.getTypeElement(Decorator.class.getCanonicalName()));
    for (Element elm : roundDecorators) {
      if (elm.getKind().isClass()) {
        decoratorTypes.add((TypeElement) elm);
      }
    }

    List<DecoratorInfo> result = new ArrayList<>();
    for (TypeElement decoratorType : decoratorTypes) {
      VariableElement delegateField = findDelegateField(decoratorType);
      if (delegateField == null) {
        logger.log(TreeLogger.WARN,
            "Decorator " + decoratorType.getQualifiedName()
                + " has no @Delegate @Inject field, skipping");
        continue;
      }

      TypeMirror delegateType = iocContext.getGenerationContext().getTypes()
          .erasure(delegateField.asType());

      TypeMirror decoratorErased = iocContext.getGenerationContext().getTypes()
          .erasure(decoratorType.asType());
      if (!iocContext.getGenerationContext().getTypes()
          .isAssignable(decoratorErased, delegateType)) {
        logger.log(TreeLogger.WARN,
            "Decorator " + decoratorType.getQualifiedName()
                + " does not implement delegate type " + delegateType + ", skipping");
        continue;
      }

      int priority = getDecoratorPriority(decoratorType);
      ensureDecoratorIsManaged(decoratorType);

      DecoratorInfo info = new DecoratorInfo(decoratorErased, delegateType, delegateField, priority);
      result.add(info);
    }

    result.sort(Comparator.comparingInt(DecoratorInfo::getPriority));
    logger.log(TreeLogger.INFO, "decorator classes found: " + result.size());
    return result;
  }

  private VariableElement findDelegateField(TypeElement type) {
    for (VariableElement field : ElementFilter.fieldsIn(type.getEnclosedElements())) {
      if (field.getAnnotation(Delegate.class) != null
          && field.getAnnotation(Inject.class) != null) {
        return field;
      }
    }
    return null;
  }

  private int getDecoratorPriority(TypeElement decoratorType) {
    Priority priority = decoratorType.getAnnotation(Priority.class);
    if (priority != null) {
      return priority.value();
    }
    return Integer.MAX_VALUE;
  }

  private void ensureDecoratorIsManaged(TypeElement decoratorType) {
    TypeMirror erased = iocContext.getGenerationContext().getTypes()
        .erasure(decoratorType.asType());
    BeanDefinition beanDefinition;
    try {
      beanDefinition = iocContext.getBeanDefinitionOrCreateAndReturn(erased);
    } catch (UnableToCompleteException e) {
      logger.log(TreeLogger.ERROR,
          "Failed to register decorator bean: " + decoratorType.getQualifiedName());
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

  private Map<String, List<DecoratorInfo>> groupByDelegateType(List<DecoratorInfo> decorators) {
    Map<String, List<DecoratorInfo>> result = new HashMap<>();
    for (DecoratorInfo info : decorators) {
      String delegateTypeName = info.getDelegateType().toString();
      result.computeIfAbsent(delegateTypeName, k -> new ArrayList<>()).add(info);
    }
    for (List<DecoratorInfo> list : result.values()) {
      list.sort(Comparator.comparingInt(DecoratorInfo::getPriority));
    }
    return result;
  }

  private void assignDecoratorsToTargetBeans(Elements elements,
      Map<String, List<DecoratorInfo>> decoratorsByDelegateType) {

    for (Map.Entry<TypeMirror, BeanDefinition> entry : iocContext.getBeans().entrySet()) {
      BeanDefinition bean = entry.getValue();
      TypeElement typeElement = MoreTypes.asTypeElement(entry.getKey());

      if (typeElement.getAnnotation(Decorator.class) != null) {
        continue;
      }

      List<TypeElement> superTypes =
          TypeUtils.getSuperTypes(elements, typeElement);

      for (TypeElement superType : superTypes) {
        String superTypeName = iocContext.getGenerationContext().getTypes()
            .erasure(superType.asType()).toString();
        List<DecoratorInfo> decorators = decoratorsByDelegateType.get(superTypeName);
        if (decorators != null && !decorators.isEmpty()) {
          bean.getDecoratorChain().addAll(decorators);

          TypeMirror decoratedInterface = iocContext.getGenerationContext().getTypes()
              .erasure(superType.asType());
          bean.getExcludedAssignableTypes().add(decoratedInterface);

          resolveDecoratorChain(bean, decorators, decoratedInterface);
        }
      }
    }

    long count = iocContext.getBeans().values().stream()
        .mapToLong(b -> b.getDecoratorChain().size()).sum();
    logger.log(TreeLogger.INFO, "decorator bindings resolved: " + count);
  }

  private void resolveDecoratorChain(BeanDefinition targetBean,
      List<DecoratorInfo> chain, TypeMirror decoratedInterface) {

    for (int i = 0; i < chain.size(); i++) {
      DecoratorInfo current = chain.get(i);

      if (i == 0) {
        current.setDelegateResolvesTo(targetBean.getType());
      } else {
        current.setDelegateResolvesTo(chain.get(i - 1).getDecoratorType());
      }

      BeanDefinition decoratorBean =
          iocContext.getBeans().get(current.getDecoratorType());
      if (decoratorBean != null) {
        setDelegateFieldOverrideType(decoratorBean, current);

        if (i == chain.size() - 1) {
          decoratorBean.getAdditionalAssignableTypes().add(decoratedInterface);
        } else {
          decoratorBean.getExcludedAssignableTypes().add(decoratedInterface);
        }
      }
    }
  }

  private void setDelegateFieldOverrideType(BeanDefinition decoratorBean, DecoratorInfo info) {
    for (InjectableVariableDefinition field : decoratorBean.getFields()) {
      if (field.getVariableElement().equals(info.getDelegateField())) {
        field.setOverrideType(info.getDelegateResolvesTo());
        return;
      }
    }
  }
}
