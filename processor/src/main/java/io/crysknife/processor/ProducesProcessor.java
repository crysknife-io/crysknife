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

package io.crysknife.processor;

import com.google.auto.common.MoreTypes;
import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.generator.api.IOCGenerator;
import io.crysknife.generator.api.WiringElementType;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.TreeLogger;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.definition.BeanDefinitionFactory;
import io.crysknife.definition.ProducesBeanDefinition;
import io.crysknife.validation.ProducesValidator;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import java.util.Optional;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 9/8/21
 */
public class ProducesProcessor {

  private final IOCContext iocContext;
  private final ProducesValidator validator;
  private final TreeLogger logger;
  private final BeanDefinitionFactory beanDefinitionFactory;
  private final TypeMirror objectTypeMirror;



  public ProducesProcessor(IOCContext iocContext, TreeLogger logger) {
    this.iocContext = iocContext;
    this.logger = logger;
    this.validator = new ProducesValidator(iocContext);
    this.beanDefinitionFactory = new BeanDefinitionFactory(iocContext, logger);
    this.objectTypeMirror = iocContext.getGenerationContext().getElements()
        .getTypeElement(Object.class.getCanonicalName()).asType();
  }

  public void process(Element produce) throws UnableToCompleteException {

    validator.validate(produce);
    ExecutableElement method = (ExecutableElement) produce;
    TypeMirror parent =
        iocContext.getGenerationContext().getTypes().erasure(method.getEnclosingElement().asType());

    if (!iocContext.getBeans().containsKey(parent)) {
      BeanDefinition bean = beanDefinitionFactory.of(parent);
      iocContext.getBeans().put(parent, bean);

      Optional<IOCGenerator> generator = iocContext.getGenerator(Singleton.class.getCanonicalName(),
          MoreTypes.asTypeElement(objectTypeMirror), WiringElementType.BEAN);
      generator.ifPresent(bean::setIocGenerator);
      iocContext.getBeans().put(parent, bean);
    }

    Optional<IOCGenerator> generator = iocContext.getGenerator(Produces.class.getCanonicalName(),
        MoreTypes.asTypeElement(objectTypeMirror), WiringElementType.METHOD_DECORATOR);

    if (generator.isPresent()) {
      ProducesBeanDefinition beanDefinition = beanDefinitionFactory.of(method);
      generator.ifPresent(beanDefinition::setIocGenerator);
      TypeMirror beanTypeMirror =
          iocContext.getGenerationContext().getTypes().erasure(method.getReturnType());
      // TODO this must be refactored
      if (iocContext.getBeans().containsKey(beanTypeMirror)
          && iocContext.getBeans().get(beanTypeMirror) instanceof ProducesBeanDefinition) {
        ((ProducesBeanDefinition) iocContext.getBeans().get(beanTypeMirror))
            .addSubtype(beanDefinition);
      } else {
        iocContext.getBeans().put(beanTypeMirror, beanDefinition);
      }
    }

  }


}
