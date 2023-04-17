/*
 * Copyright © 2021 Treblereel
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

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.definition.MethodDefinition;
import io.crysknife.definition.MethodDefinitionFactory;
import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.generator.api.IOCGenerator;
import io.crysknife.generator.api.WiringElementType;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.TreeLogger;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 24/11/21
 */
public class MethodParamDecoratorTask implements Task {

  private IOCContext iocContext;
  private TreeLogger logger;
  private final MethodDefinitionFactory methodDefinitionFactory;


  public MethodParamDecoratorTask(IOCContext context, TreeLogger logger) {
    this.iocContext = context;
    this.logger = logger;

    this.methodDefinitionFactory = new MethodDefinitionFactory(iocContext, logger);
  }

  @Override
  public void execute() throws UnableToCompleteException {
    iocContext.getGenerators().asMap().entrySet().stream()
        .filter(iocGeneratorMetaCollectionEntry -> iocGeneratorMetaCollectionEntry
            .getKey().wiringElementType.equals(WiringElementType.PARAMETER))
        .forEach(iocGeneratorMetaCollectionEntry -> {
          iocGeneratorMetaCollectionEntry.getValue().forEach(gen -> {

            Set<ExecutableElement> elements = iocContext
                .getParametersByAnnotation(iocGeneratorMetaCollectionEntry.getKey().annotation)
                .stream().filter(elm -> elm.getKind().equals(ElementKind.PARAMETER))
                .map(MoreElements::asVariable)
                .map(elm -> MoreElements.asExecutable(elm.getEnclosingElement()))
                .map(MoreElements::asExecutable).collect(Collectors.toSet());

            elements.stream().forEach(e -> {
              TypeMirror erased = iocContext.getGenerationContext().getTypes()
                  .erasure(e.getEnclosingElement().asType());
              BeanDefinition bean = iocContext.getBeans().get(erased);
              ExecutableType methodType = (ExecutableType) e.asType();
              bean.getMethods().stream()
                  .filter(mmethod -> MoreTypes.asExecutable(mmethod.getExecutableElement().asType())
                      .equals(methodType))
                  .findFirst().orElse(methodDefinitionFactory.of(bean, e)).getDecorators()
                  .addAll(iocGeneratorMetaCollectionEntry.getValue().stream()
                      .map(em -> (IOCGenerator<MethodDefinition>) em).collect(Collectors.toSet()));
            });
          });
        });

    iocContext.getBeans().values().forEach(bean -> bean.getSubclasses().forEach(sub -> {
      bean.getMethods().forEach(subMethod -> {
        if (!sub.getMethods().contains(subMethod)) {
          sub.getMethods().add(subMethod);
        }
      });
    }));
  }
}
