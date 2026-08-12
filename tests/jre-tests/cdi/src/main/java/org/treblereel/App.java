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

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

import io.crysknife.annotation.Application;
import io.crysknife.client.BeanManager;
import io.crysknife.ui.databinding.api.DataBinder;
import org.treblereel.databinding.UserModel;
import org.treblereel.decorator.Greeter;
import org.treblereel.decorator.LoggingGreeterDecorator;
import org.treblereel.decorator.SimpleGreeter;
import org.treblereel.decorator.chain.BasicFormatter;
import org.treblereel.decorator.chain.BracketFormatterDecorator;
import org.treblereel.decorator.chain.Formatter;
import org.treblereel.decorator.chain.StarFormatterDecorator;
import org.treblereel.decorator.exception.StrictValidator;
import org.treblereel.decorator.exception.TrimValidatorDecorator;
import org.treblereel.decorator.exception.Validator;
import org.treblereel.decorator.lifecycle.PostConstructWorkerDecorator;
import org.treblereel.decorator.lifecycle.SimpleWorker;
import org.treblereel.decorator.lifecycle.Worker;
import org.treblereel.decorator.multiface.LoggingPrinterDecorator;
import org.treblereel.decorator.multiface.MultiDevice;
import org.treblereel.decorator.multiface.Printer;
import org.treblereel.decorator.multiface.Scanner;
import org.treblereel.interceptor.InterceptedService;
import org.treblereel.interceptor.LoggingInterceptor;
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
import org.treblereel.produces.qualifier.QualifierBeanProducerHolder;
import org.treblereel.ui.translation.AppMessagesBeanHolder;
import org.treblereel.websocket.EchoEndpointHolder;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/21/20
 */
@Application
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
  private QualifierBeanProducerHolder qualifierBeanProducerTest;

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

  @Inject
  public Greeter greeter;

  @Inject
  public LoggingGreeterDecorator loggingGreeterDecorator;

  @Inject
  public SimpleGreeter simpleGreeter;

  @Inject
  public Formatter formatter;

  @Inject
  public BasicFormatter basicFormatter;

  @Inject
  public BracketFormatterDecorator bracketFormatterDecorator;

  @Inject
  public StarFormatterDecorator starFormatterDecorator;

  @Inject
  public Printer printer;

  @Inject
  public Scanner scanner;

  @Inject
  public MultiDevice multiDevice;

  @Inject
  public LoggingPrinterDecorator loggingPrinterDecorator;

  @Inject
  public Worker worker;

  @Inject
  public PostConstructWorkerDecorator postConstructWorkerDecorator;

  @Inject
  public SimpleWorker simpleWorker;

  @Inject
  public Validator validator;

  @Inject
  public TrimValidatorDecorator trimValidatorDecorator;

  @Inject
  public StrictValidator strictValidator;

  @Inject
  public InterceptedService interceptedService;

  @Inject
  public LoggingInterceptor loggingInterceptor;

  @Inject
  public DataBinder<UserModel> userBinder;

  @Inject
  public AppMessagesBeanHolder appMessagesBeanHolder;

  @Inject
  public EchoEndpointHolder echoEndpointHolder;

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

  public QualifierBeanProducerHolder getQualifierBeanProducerTest() {
    return qualifierBeanProducerTest;
  }

  public ManagedInstanceBean getManagedInstanceBean() {
    return managedInstanceBean;
  }

}
