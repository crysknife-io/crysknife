package org.treblereel.gwt.crysknife.client;

import javax.inject.Inject;

import org.treblereel.gwt.crysknife.client.injection.applicationscoped.ApplicationScopedConstructorInjection;
import org.treblereel.gwt.crysknife.client.injection.named.NamedConstructorInjection;
import org.treblereel.gwt.crysknife.client.injection.named.NamedFieldInjection;
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

    @Inject
    SingletonBean singletonBean;

    @Inject
    QualifierConstructorInjection qualifierConstructorInjection;

    @Inject
    QualifierFieldInjection qualifierFieldInjection;

    @Inject
    BeanManager beanManager;

    @Inject
    ApplicationScopedConstructorInjection applicationScopedConstructorInjection;

    @Inject
    NamedConstructorInjection namedConstructorInjection;

    @Inject
    NamedFieldInjection namedFieldInjection;
}
