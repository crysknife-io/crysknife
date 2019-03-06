package org.treblereel.client;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.core.client.EntryPoint;
import elemental2.dom.DomGlobal;
import org.treblereel.client.inject.DependentBean;
import org.treblereel.client.inject.Injector;
import org.treblereel.client.inject.iface.IBean;
import org.treblereel.gwt.crysknife.client.Application;
import org.treblereel.gwt.crysknife.client.ComponentScan;

@Application
@ComponentScan("org.treblereel.client")
public class App implements EntryPoint {

    @Inject
    Injector injector;

    @Inject
    NamedBeanFieldInjectionPanel namedBeanFieldInjectionPanel;

    @Inject
    NamedBeanConstructorInjectionPanel namedBeanConstructorInjectionPanel;

    @Inject
    DependentBean dependentBean;

    @Inject
    Elemental2Bean elemental2Bean;

    @Inject
    SingletonBeans singletonBeans;

    @Inject
    DependentBeans dependentBeans;

    @Inject
    TransitiveInjection transitiveInjection;

    @Inject
    IBean iBean;

    @Override
    public void onModuleLoad() {
        new AppBootstrap(this).initialize();
    }

    @PostConstruct
    public void init() {
        DomGlobal.document.body.appendChild(dependentBeans.asElement());
        DomGlobal.document.body.appendChild(singletonBeans.asElement());
        DomGlobal.document.body.appendChild(namedBeanFieldInjectionPanel.asElement());
        DomGlobal.document.body.appendChild(namedBeanConstructorInjectionPanel.asElement());
        DomGlobal.document.body.appendChild(transitiveInjection.asElement());
    }
}
