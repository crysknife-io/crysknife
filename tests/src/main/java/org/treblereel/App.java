package org.treblereel;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.treblereel.gwt.crysknife.client.Application;
import org.treblereel.injection.applicationscoped.SimpleBeanApplicationScoped;
import org.treblereel.injection.dependent.SimpleBeanDependent;
import org.treblereel.injection.dependent.SimpleDependentTest;
import org.treblereel.injection.named.NamedTestBean;
import org.treblereel.injection.qualifiers.QualifierConstructorInjection;
import org.treblereel.injection.qualifiers.QualifierFieldInjection;
import org.treblereel.injection.singleton.SimpleBeanSingleton;
import org.treblereel.injection.singleton.SimpleSingletonTest;
import org.treblereel.produces.SimpleBeanProducerTest;
import org.treblereel.produces.qualifier.QualifierBeanProducerTest;

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
  private QualifierBeanProducerTest qualifierBeanProducerTest;

  public void onModuleLoad() {
    new AppBootstrap(this).initialize();
  }

  @PostConstruct
  public void init() {
    this.testPostConstruct = "PostConstruct";
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
}
