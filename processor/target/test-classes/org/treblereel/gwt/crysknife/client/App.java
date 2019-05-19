package org.treblereel.gwt.crysknife.client;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.treblereel.gwt.crysknife.client.injection.qualifiers.QualifierConstructorInjection;
import org.treblereel.gwt.crysknife.client.injection.qualifiers.QualifierFieldInjection;
import org.treblereel.gwt.crysknife.client.injection.singleton.SingletonBean;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/14/19
 */
@Application
@ComponentScan("org.treblereel.client")
public class App {

    //@Inject
    SingletonBean singletonBean;

    //@Inject
    QualifierConstructorInjection qualifierConstructorInjection;

    ///@Inject
    QualifierFieldInjection qualifierFieldInjection;

    //@Inject
    BeanManager beanManager;

    @PostConstruct
    public void init() {

    }
}
