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

package io.crysknife.generator.steps;

import com.google.auto.common.MoreTypes;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.generator.PostConstructGenerator;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.util.Utils;
import jakarta.annotation.PostConstruct;

import javax.lang.model.element.ExecutableElement;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class PostConstructAnnotation implements Step<BeanDefinition> {

  @Override
  public void execute(IOCContext iocContext, ClassBuilder classBuilder,
      BeanDefinition beanDefinition) {

    PostConstructGenerator postConstructGenerator = new PostConstructGenerator(null, iocContext);
    LinkedList<ExecutableElement> postConstructs = Utils
        .getAllMethodsIn(iocContext.getGenerationContext().getElements(),
            MoreTypes.asTypeElement(beanDefinition.getType()))
        .stream().filter(elm -> elm.getAnnotation(PostConstruct.class) != null)
        .collect(Collectors.toCollection(LinkedList::new));

    Iterator<ExecutableElement> elm = postConstructs.descendingIterator();
    while (elm.hasNext()) {
      postConstructGenerator.generate(beanDefinition.getType(),
          classBuilder.getInitInstanceMethod().getBody().get(), elm.next());
    }
  }

}
