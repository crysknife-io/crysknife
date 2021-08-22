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

package org.treblereel.injection.inheritance;

import io.crysknife.client.BeanManager;
import io.crysknife.client.BeanManagerImpl;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import javax.enterprise.inject.Instance;
import java.lang.reflect.Field;
import java.util.function.Supplier;

// @Aspect()
public class BeanChildInfo2 {

  private BeanManager beanManager = BeanManagerImpl.get();

  // @Before("get(* org.treblereel.injection.inheritance.Parent.target)")
  public void Parent_target(JoinPoint jp) {
    try {
      onInvoke(jp, "target", beanManager.lookupBean(Target.class));
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new Error(e);
    }
  }

  private void onInvoke(JoinPoint joinPoint, String fieldName, Instance instance)
      throws NoSuchFieldException, IllegalAccessException {
    Field field = joinPoint.getTarget().getClass().getField(fieldName);
    if (field.get(joinPoint.getTarget()) == null) {
      field.setAccessible(true);
      field.set(joinPoint.getTarget(), instance.get());
    }
  }

  // @Before("get(* org.treblereel.injection.inheritance.BeanChild.*)")
  public void doSomething(JoinPoint jp) {
    System.out.println(" ProceedingJoinPoint " + jp);
  }
}
