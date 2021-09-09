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

package io.crysknife.nextstep;

import com.google.auto.common.MoreTypes;
import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.generator.IOCGenerator;
import io.crysknife.generator.WiringElementType;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.PrintWriterTreeLogger;
import io.crysknife.nextstep.definition.BeanDefinition;
import io.crysknife.nextstep.definition.BeanDefinitionFactory;
import io.crysknife.nextstep.definition.ProducesBeanDefinition;
import io.crysknife.nextstep.validation.ProducesValidator;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import java.util.Optional;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 9/8/21
 */
public class ProducesProcessor {

  private final IOCContext iocContext;
  private final BeanProcessor beanProcessor;
  private final ProducesValidator validator;
  private final PrintWriterTreeLogger logger;
  private final BeanDefinitionFactory beanDefinitionFactory;
  private final TypeMirror objectTypeMirror;



  ProducesProcessor(IOCContext iocContext, BeanProcessor beanProcessor,
      PrintWriterTreeLogger logger) {
    this.iocContext = iocContext;
    this.beanProcessor = beanProcessor;
    this.logger = logger;
    this.validator = new ProducesValidator(iocContext);
    this.beanDefinitionFactory = new BeanDefinitionFactory(iocContext, logger);
    this.objectTypeMirror = iocContext.getGenerationContext().getElements()
        .getTypeElement(Object.class.getCanonicalName()).asType();
  }

  void process(Element produce) throws UnableToCompleteException {

    System.out.println("ProducesProcessor:process -> " + produce);
    validator.validate(produce);
    ExecutableElement method = (ExecutableElement) produce;
    TypeMirror parent =
        iocContext.getGenerationContext().getTypes().erasure(method.getEnclosingElement().asType());

    if (!beanProcessor.getBeans().containsKey(parent)) {
      BeanDefinition bean = beanDefinitionFactory.of(parent);
      beanProcessor.getBeans().put(parent, bean);

      Optional<IOCGenerator> generator =
          beanProcessor.getGenerator(Singleton.class.getCanonicalName(),
              MoreTypes.asTypeElement(objectTypeMirror), WiringElementType.BEAN);
      generator.ifPresent(bean::setIocGenerator);
      beanProcessor.getBeans().put(parent, bean);
    }

    Optional<IOCGenerator> generator = beanProcessor.getGenerator(Produces.class.getCanonicalName(),
        MoreTypes.asTypeElement(objectTypeMirror), WiringElementType.METHOD_DECORATOR);

    if (generator.isPresent()) {
      ProducesBeanDefinition beanDefinition = beanDefinitionFactory.of(method);
      beanDefinition.setIocGenerator(generator.get());
      TypeMirror beanTypeMirror =
          iocContext.getGenerationContext().getTypes().erasure(method.getReturnType());
      beanProcessor.getBeans().put(beanTypeMirror, beanDefinition);
    }

  }


}
