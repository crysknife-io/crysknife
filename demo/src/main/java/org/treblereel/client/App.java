package org.treblereel.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import org.treblereel.client.inject.DependentBean;
import org.treblereel.client.inject.Injector;
import org.treblereel.gwt.crysknife.client.Application;
import org.treblereel.gwt.crysknife.client.ComponentScan;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Application
@ComponentScan("org.treblereel.client.inject")
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

    @Override
    public void onModuleLoad() {
        new AppBootstrap(this).initialize();
    }

    @PostConstruct
    public void init(){
        RootPanel.get().add(namedBeanFieldInjectionPanel);
        RootPanel.get().add(namedBeanConstructorInjectionPanel);
        RootPanel.get().add(namedBeanConstructorInjectionPanel);
        RootPanel.get().add(singletonBeans);
        RootPanel.get().add(dependentBeans);
        RootPanel.get().add(transitiveInjection);
    }
}
