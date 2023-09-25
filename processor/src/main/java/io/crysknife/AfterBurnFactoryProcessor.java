/*
 * Copyright © 2023 Treblereel
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

package io.crysknife;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.ejb.Singleton;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import com.google.auto.common.MoreElements;
import com.google.auto.service.AutoService;
import io.crysknife.client.internal.step.AfterBurnFactoryStep;
import io.crysknife.exception.GenerationException;
import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.PrintWriterTreeLogger;
import io.crysknife.logger.TreeLogger;
import io.crysknife.task.BeanManagerGeneratorStepTask;
import io.crysknife.task.BeanProcessorTask;
import io.crysknife.task.FactoryGeneratorTask;
import io.crysknife.task.IOCProviderTask;
import io.crysknife.task.TaskGroup;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class AfterBurnFactoryProcessor extends AbstractProcessor {

  private static final TreeLogger logger = new PrintWriterTreeLogger();
  private final Set<Class<? extends Annotation>> annotations = Set.of(AfterBurnFactoryStep.class,
      ApplicationScoped.class, Dependent.class, Singleton.class, jakarta.inject.Singleton.class);
  private IOCContext iocContext;

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return annotations.stream().map(Class::getCanonicalName).collect(Collectors.toSet());
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    iocContext = ContextHolder.getInstance().getContext();

    if (annotations.isEmpty()) {
      return false;
    }
    if (iocContext == null) {
      return false;
    }

    try {
      new IOCProviderTask(iocContext, logger).execute();
    } catch (UnableToCompleteException e) {
      throw new GenerationException(e);
    }

    Set<TypeElement> types = new HashSet<>();
    for (TypeElement annotation : annotations) {
      roundEnv.getElementsAnnotatedWith(annotation).stream().filter(elm -> elm.getKind().isClass())
          .map(elm -> (TypeElement) elm)
          .filter(elm -> MoreElements.isAnnotationPresent(elm, annotation)).forEach(types::add);
    }
    if (types.isEmpty()) {
      return false;
    }

    BeanProcessorTask beanProcessorTask = new BeanProcessorTask(iocContext, logger);
    FactoryGeneratorTask factoryGeneratorTask = new FactoryGeneratorTask(iocContext, logger);

    Set<TypeElement> beans =
        types.stream().filter(type -> type.getAnnotation(AfterBurnFactoryStep.class) == null)
            .collect(Collectors.toSet());
    beanProcessorTask.execute(beans);

    beans.stream().map(bean -> iocContext.getGenerationContext().getTypes().erasure(bean.asType()))
        .map(mirror -> iocContext.getBean(mirror))
        .filter(bean -> !bean.isFactoryGenerationFinished()).forEach(beanDefinition -> {
          factoryGeneratorTask.generate(beanDefinition);
          iocContext.getOrderedBeans().add(beanDefinition.getType());
        });
    TaskGroup taskGroup =
        new TaskGroup(logger.branch(TreeLogger.DEBUG, "start after burn processing"));
    taskGroup.addTask(new BeanManagerGeneratorStepTask(iocContext, logger));
    taskGroup.execute();

    return true;
  }

}
