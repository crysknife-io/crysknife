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

package io.crysknife.processor;

import java.util.Optional;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import com.google.auto.common.MoreElements;
import io.crysknife.generator.IOCGenerator;
import io.crysknife.generator.context.IOCContext;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/4/19
 */
public class TypeProcessorFactory {

  private TypeProcessorFactory() {

  }

  public static Optional<TypeProcessor> getTypeProcessor(IOCContext.IOCGeneratorMeta meta,
      IOCGenerator generator, Element element) {
    if (element.getKind().isField() || element.getKind().isClass()) {
      TypeMirror type = (element.getKind().isField() ? MoreElements.asVariable(element).asType()
          : element.asType());
      if (type.equals(meta.exactType.asType())) {
        return Optional.of(new ExactTypeProcessor(generator));
      }
    }

    switch (meta.wiringElementType) {
      case BEAN:
        return Optional.of(new DependentTypeProcessor(generator));
      case PRODUCER_ELEMENT:
        return Optional.of(new ProducerTypeProcessor(generator));
      case CLASS_DECORATOR:
        return Optional.of(new ClassDecoratorTypeProcessor(generator));
      case METHOD_DECORATOR:
        return Optional.of(new MethodDecoratorTypeProcessor(generator));
      case PARAMETER:
        return Optional.of(new ParameterTypeProcessor(generator));
      default:
        return Optional.empty();
    }
  }
}
