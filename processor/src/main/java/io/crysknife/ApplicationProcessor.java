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

package io.crysknife;

import com.google.auto.service.AutoService;
import io.crysknife.annotation.Application;
import io.crysknife.annotation.Generator;
import io.crysknife.exception.GenerationException;
import io.crysknife.generator.BeanManagerGenerator;
import io.crysknife.generator.FactoryGenerator;
import io.crysknife.generator.IOCGenerator;
import io.crysknife.generator.context.GenerationContext;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.generator.info.BeanInfoGenerator;
import io.crysknife.logger.PrintWriterTreeLogger;
import io.crysknife.logger.TreeLogger;
import io.crysknife.task.BeanProcessorTask;
import io.crysknife.task.CheckCyclesTask;
import io.crysknife.task.FireAfterTask;
import io.crysknife.task.FireBeforeTask;
import io.crysknife.task.ProcessComponentScanAnnotationTask;
import io.crysknife.task.ProcessGraphTask;
import io.crysknife.task.ProcessSubClassesTask;
import io.crysknife.task.TaskGroup;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.Set;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"io.crysknife.annotation.Application"})
public class ApplicationProcessor extends AbstractProcessor {

  private IOCContext iocContext;
  private Set<String> packages;
  private GenerationContext context;
  private TypeElement application;

  @Override
  public boolean process(Set<? extends TypeElement> annotations,
      RoundEnvironment roundEnvironment) {
    if (annotations.isEmpty()) {
      return false;
    }

    context = new GenerationContext(roundEnvironment, processingEnv);
    iocContext = new IOCContext(context);

    TreeLogger logger = new PrintWriterTreeLogger();

    Optional<TypeElement> maybeApplication = processApplicationAnnotation(iocContext);
    if (!maybeApplication.isPresent()) {
      return true;
    }
    this.application = maybeApplication.get();

    initAndRegisterGenerators();

    TaskGroup taskGroup = new TaskGroup(logger.branch(TreeLogger.DEBUG, "start processing"));
    // taskGroup.addTask(new InitAndRegisterGeneratorsTask(iocContext, logger));
    taskGroup.addTask(new FireBeforeTask(iocContext, logger));

    taskGroup.addTask(new ProcessComponentScanAnnotationTask(iocContext, logger, application));
    taskGroup.addTask(new BeanProcessorTask(iocContext, logger));
    taskGroup.addTask(new ProcessSubClassesTask(iocContext, logger));
    // taskGroup.addTask(new FireBeforeTask(iocContext, logger));
    taskGroup.addTask(new ProcessGraphTask(iocContext, logger, application));
    taskGroup.addTask(new CheckCyclesTask(iocContext, logger));

    taskGroup.addTask(new FactoryGenerator(iocContext));
    taskGroup.addTask(new BeanInfoGenerator(iocContext));
    taskGroup.addTask(new BeanManagerGenerator(iocContext));
    taskGroup.addTask(new FireAfterTask(iocContext, logger));
    taskGroup.execute();

    return false;
  }

  private Optional<TypeElement> processApplicationAnnotation(IOCContext iocContext) {
    Set<TypeElement> applications = (Set<TypeElement>) iocContext.getGenerationContext()
        .getRoundEnvironment().getElementsAnnotatedWith(Application.class);

    if (applications.size() == 0) {
      context.getProcessingEnvironment().getMessager().printMessage(Diagnostic.Kind.WARNING,
          "No class annotated with @Application detected\"");
      return Optional.empty();
    }

    if (applications.size() > 1) {
      context.getProcessingEnvironment().getMessager().printMessage(Diagnostic.Kind.ERROR,
          "There is must be only one class annotated with @Application\"");
      throw new GenerationException();
    }
    return applications.stream().findFirst();
  }

  private void initAndRegisterGenerators() {
    try (ScanResult scanResult = new ClassGraph().enableAllInfo().scan()) {
      ClassInfoList routeClassInfoList =
          scanResult.getClassesWithAnnotation(Generator.class.getCanonicalName());
      for (ClassInfo routeClassInfo : routeClassInfoList) {
        try {
          Constructor c = Class.forName(routeClassInfo.getName()).getConstructor(IOCContext.class);
          ((IOCGenerator) c.newInstance(iocContext)).register();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
            | NoSuchMethodException | InvocationTargetException e) {
          throw new GenerationException(e);
        }
      }
    }
  }
}
