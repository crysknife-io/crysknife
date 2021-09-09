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

package io.crysknife.nextstep.definition;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Singleton;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 9/6/21
 */
public class ProducesBeanDefinition extends BeanDefinition {

  private ExecutableElement method;

  ProducesBeanDefinition(ExecutableElement method) {
    super(method.getReturnType());
    this.method = method;
  }

  public ExecutableElement getMethod() {
    return method;
  }

  public TypeElement getProducer() {
    return MoreElements.asType(method.getEnclosingElement());
  }

  public boolean isSingleton() {
    return method.getAnnotation(Singleton.class) != null
        || method.getAnnotation(ApplicationScoped.class) != null;
  }
}
