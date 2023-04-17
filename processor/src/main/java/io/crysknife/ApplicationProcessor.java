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
import io.crysknife.generator.api.Generator;
import io.crysknife.exception.GenerationException;
import io.crysknife.task.BeanManagerGeneratorTask;
import io.crysknife.task.FactoryGeneratorTask;
import io.crysknife.generator.api.IOCGenerator;
import io.crysknife.generator.context.GenerationContext;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.task.BeanInfoGenerator;
import io.crysknife.logger.PrintWriterTreeLogger;
import io.crysknife.logger.TreeLogger;
import io.crysknife.task.*;
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

    final TreeLogger logger = new PrintWriterTreeLogger();
    final long start = System.currentTimeMillis();

    context = new GenerationContext(roundEnvironment, processingEnv,
        logger.branch(TreeLogger.DEBUG, "start classpath scan ..."));

    final long finished = (System.currentTimeMillis() - start);

    logger.log(TreeLogger.INFO, "classpath processed in " + finished / 1000 + "s");
    if (finished > 1000) {
      logger.log(TreeLogger.INFO,
          "ClassPath scan is slow, reduce the number of jars in the classpath/dependencies.");
    }


    iocContext = new IOCContext(context);
    Optional<TypeElement> maybeApplication = processApplicationAnnotation(iocContext);
    if (!maybeApplication.isPresent()) {
      return true;
    }
    this.application = maybeApplication.get();

    initAndRegisterGenerators(iocContext, logger);

    TaskGroup taskGroup = new TaskGroup(logger.branch(TreeLogger.DEBUG, "start processing"));
    // taskGroup.addTask(new InitAndRegisterGeneratorsTask(iocContext, logger));
    taskGroup.addTask(new FireBeforeTask(iocContext, logger));
    taskGroup.addTask(new IOCProviderTask(iocContext, logger));
    taskGroup.addTask(new BeanProcessorTask(iocContext, logger));
    taskGroup.addTask(new ProcessSubClassesTask(iocContext, logger));
    taskGroup.addTask(new ProcessGraphTask(iocContext, logger, application));
    taskGroup.addTask(new CheckCyclesTask(iocContext, logger));
    taskGroup.addTask(new MethodParamDecoratorTask(iocContext, logger));
    taskGroup.addTask(new FactoryGeneratorTask(iocContext, logger));
    taskGroup.addTask(new BeanInfoGenerator(iocContext, logger));
    taskGroup.addTask(new BeanManagerGeneratorTask(iocContext, logger));
    taskGroup.addTask(new FireAfterTask(iocContext, logger));
    taskGroup.execute();


    logger.log(TreeLogger.INFO,
        "Crysknife generation finished in " + (System.currentTimeMillis() - start) + " ms");

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

  private void initAndRegisterGenerators(IOCContext iocContext, TreeLogger logger) {
    ScanResult scanResult = iocContext.getGenerationContext().getScanResult();
    ClassInfoList routeClassInfoList =
        scanResult.getClassesWithAnnotation(Generator.class.getCanonicalName());
    for (ClassInfo routeClassInfo : routeClassInfoList) {
      try {
        Constructor c = Class.forName(routeClassInfo.getName()).getConstructor(TreeLogger.class,
            IOCContext.class);
        ((IOCGenerator) c.newInstance(
            logger.branch(TreeLogger.INFO, "register generator: " + routeClassInfo.getName()),
            this.iocContext)).register();
      } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
          | NoSuchMethodException | InvocationTargetException e) {
        throw new GenerationException(e);
      }
    }
  }
}
