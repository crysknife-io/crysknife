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

package org.treblereel;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import io.crysknife.annotation.Application;
import io.crysknife.client.BeanManager;
import io.crysknife.ui.translation.api.annotations.Bundle;
import org.treblereel.injection.applicationscoped.SimpleBeanApplicationScoped;
import org.treblereel.injection.dependent.SimpleBeanDependent;
import org.treblereel.injection.dependent.SimpleDependentTest;
import org.treblereel.injection.inheritance.InheritanceBean;
import org.treblereel.injection.managedinstance.ManagedInstanceBean;
import org.treblereel.injection.named.NamedTestBean;
import org.treblereel.injection.qualifiers.QualifierConstructorInjection;
import org.treblereel.injection.qualifiers.QualifierFieldInjection;
import org.treblereel.injection.qualifiers.controls.NodeBuilderControl;
import org.treblereel.injection.qualifiers.specializes.SpecializesBeanHolder;
import org.treblereel.injection.singleton.SimpleBeanSingleton;
import org.treblereel.injection.singleton.SimpleSingletonTest;
import org.treblereel.postconstruct.PostConstructs;
import org.treblereel.produces.SimpleBeanProducerTest;
import org.treblereel.produces.qualifier.QualifierBeanProducerTest;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/21/20
 */
@Application
@Bundle("i18n/simple/i18n.properties")
public class App {

  public String testPostConstruct;
  @Inject
  public QualifierFieldInjection qualifierFieldInjection;
  @Inject
  public QualifierConstructorInjection qualifierConstructorInjection;
  @Inject
  public SimpleDependentTest simpleDependentTest;
  @Inject
  public SimpleSingletonTest simpleSingletonTest;
  @Inject
  private SimpleBeanApplicationScoped simpleBeanApplicationScoped;
  @Inject
  private SimpleBeanSingleton simpleBeanSingleton;
  @Inject
  private SimpleBeanDependent simpleBeanDependent;
  @Inject
  private NamedTestBean namedTestBean;

  @Inject
  private SimpleBeanProducerTest simpleBeanProducerTest;

  @Inject
  private QualifierBeanProducerTest qualifierBeanProducerTest;

  @Inject
  private ManagedInstanceBean managedInstanceBean;

  @Inject
  public BeanManager beanManager;

  @Inject
  protected PostConstructs postConstructs;

  @Inject
  public InheritanceBean inheritanceBean;

  @Inject
  public NodeBuilderControl nodeBuilderControl;

  @Inject
  public SpecializesBeanHolder specializesBeanHolder;

  public void onModuleLoad() {
    new AppBootstrap(this).initialize();
  }

  @PostConstruct
  public void init() {
    this.testPostConstruct = "PostConstructChild";
  }

  public String getTestPostConstruct() {
    return testPostConstruct;
  }

  public SimpleBeanApplicationScoped getSimpleBeanApplicationScoped() {
    return simpleBeanApplicationScoped;
  }

  public QualifierConstructorInjection getQualifierConstructorInjection() {
    return qualifierConstructorInjection;
  }

  public SimpleBeanSingleton getSimpleBeanSingleton() {
    return simpleBeanSingleton;
  }

  public SimpleBeanDependent getSimpleBeanDependent() {
    return simpleBeanDependent;
  }

  public QualifierFieldInjection getQualifierFieldInjection() {
    return qualifierFieldInjection;
  }

  public NamedTestBean getNamedTestBean() {
    return namedTestBean;
  }

  public SimpleBeanProducerTest getSimpleBeanProducerTest() {
    return simpleBeanProducerTest;
  }

  public void setSimpleBeanProducerTest(SimpleBeanProducerTest simpleBeanProducerTest) {
    this.simpleBeanProducerTest = simpleBeanProducerTest;
  }

  public QualifierBeanProducerTest getQualifierBeanProducerTest() {
    return qualifierBeanProducerTest;
  }

  public ManagedInstanceBean getManagedInstanceBean() {
    return managedInstanceBean;
  }

}
