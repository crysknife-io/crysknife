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

package org.treblereel.injection.qualifiers;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 8/23/21
 */
@Singleton
public class DefaultImpls {

  @Inject
  public BeanOne beanOne;

  @Inject
  public BeanTwo beanTwo;

  @Inject
  public Inner inner;

  public interface BeanOne {

    String say();
  }

  @Singleton
  @Default
  public static class BeanOneImpl implements BeanOne {

    private String answer;

    @PostConstruct
    public void init() {
      this.answer = "PostConstruct_BeanOneImpl";
    }

    @Override
    public String say() {
      return answer;
    }
  }

  @Dependent
  public static class Inner {

    @Inject
    public BeanOne beanOne;

    @Inject
    public BeanTwo beanTwo;

  }

  public interface BeanTwo {

    String say();
  }

  public static abstract class BeanTwoAbstract implements BeanTwo {

  }

  @Singleton
  @Default
  public static class BeanTwoImpl extends BeanTwoAbstract {

    @Inject
    Resolver resolver;

    @Override
    public String say() {
      return resolver.resolve();
    }
  }

  @Singleton
  public static class Resolver {

    String resolve() {
      return "Resolver_BeanTwoImpl";
    }
  }

}
