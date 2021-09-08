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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import com.google.auto.common.MoreElements;
import com.google.auto.service.AutoService;
import io.crysknife.logger.PrintWriterTreeLogger;
import io.crysknife.nextstep.BeanProcessor;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import io.crysknife.annotation.Application;
import io.crysknife.annotation.Generator;
import io.crysknife.annotation.ComponentScan;
import io.crysknife.exception.GenerationException;
import io.crysknife.generator.IOCGenerator;
import io.crysknife.generator.context.GenerationContext;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.generator.graph.Graph;
import io.crysknife.generator.info.BeanInfoGenerator;
import io.crysknife.generator.scanner.ComponentInjectionResolverScanner;
import io.crysknife.generator.scanner.ComponentScanner;
import io.crysknife.generator.scanner.ProducersScan;
import io.crysknife.generator.scanner.QualifiersScan;

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

    Optional<TypeElement> maybeApplication = processApplicationAnnotation(iocContext);
    if (!maybeApplication.isPresent()) {
      return true;
    }
    this.application = maybeApplication.get();

    processComponentScanAnnotation();
    initAndRegisterGenerators();

    BeanProcessor beanProcessor =
        new BeanProcessor(iocContext, new PrintWriterTreeLogger()).process();

    beanProcessor.getBeans().keySet().forEach(key -> {
      System.out.println("key " + key);
    });


    // processQualifiersScan();
    // processComponentScan();
    // processInjectionScan();
    // processProducersScan();
    fireIOCGeneratorBefore();


    processGraph(beanProcessor);
    new FactoryGenerator(iocContext, beanProcessor).generate();
    new BeanInfoGenerator(iocContext, beanProcessor).generate();
    new BeanManagerGenerator(iocContext, beanProcessor).generate();
    fireIOCGeneratorAfter();

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

  private void processComponentScanAnnotation() {
    packages = new HashSet<>();
    context.getRoundEnvironment().getElementsAnnotatedWith(ComponentScan.class)
        .forEach(componentScan -> {
          String[] values = componentScan.getAnnotation(ComponentScan.class).value();
          for (String aPackage : values) {
            packages.add(aPackage);
          }
        });

    if (packages.isEmpty()) {
      packages.add(MoreElements.getPackage(application).getQualifiedName().toString());
    }
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

  private void processQualifiersScan() {
    new QualifiersScan(iocContext).process();
  }

  private void processComponentScan() {
    new ComponentScanner(iocContext, context).scan();
  }

  private void processInjectionScan() {
    new ComponentInjectionResolverScanner(iocContext).scan();
  }

  private void processProducersScan() {
    new ProducersScan(iocContext).scan();
  }

  private void fireIOCGeneratorBefore() {
    iocContext.getGenerators().forEach((meta, generator) -> generator.before());
  }

  private void processGraph(BeanProcessor beanProcessor) {
    new Graph(iocContext, beanProcessor.getBeans()).process(application);
  }

  private void fireIOCGeneratorAfter() {
    iocContext.getGenerators().forEach((meta, generator) -> generator.after());
  }
}
