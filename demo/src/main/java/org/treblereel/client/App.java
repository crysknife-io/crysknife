package org.treblereel.client;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.EntryPoint;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import org.treblereel.client.events.Address;
import org.treblereel.client.events.BeanWithCDIEvents;
import org.treblereel.client.events.User;
import org.treblereel.client.inject.DependentBean;
import org.treblereel.client.inject.Injector;
import org.treblereel.client.inject.iface.IBean;
import org.treblereel.client.template.TemplatedBean;
import org.treblereel.gwt.crysknife.client.Application;
import org.treblereel.gwt.crysknife.client.BeanManager;
import org.treblereel.gwt.crysknife.client.ComponentScan;

@Application
@ComponentScan("org.treblereel.client")
public class App implements EntryPoint {

    @Inject
    Injector injector;

    @Inject
    BeanManager beanManager;

    @Inject
    NamedBeanFieldInjectionPanel namedBeanFieldInjectionPanel;

    @Inject
    NamedBeanConstructorInjectionPanel namedBeanConstructorInjectionPanel;

    @Inject
    DependentBean dependentBean;

    //@Inject
    Elemental2Bean elemental2Bean;

    @Inject
    SingletonBeans singletonBeans;

    @Inject
    DependentBeans dependentBeans;

    @Inject
    TransitiveInjection transitiveInjection;

    @Inject
    IBean iBean;

    @Inject
    BeanWithCDIEvents beanWithCDIEvents;

    @Inject
    Event<User> eventUser;

    @Inject
    HTMLDivElement toast;

    @Inject
    TemplatedBean templatedBean;

    @Override
    public void onModuleLoad() {
        new AppBootstrap(this).initialize();
    }

    @PostConstruct
    public void init() {
        DomGlobal.document.body.appendChild(dependentBeans.element());
        DomGlobal.document.body.appendChild(singletonBeans.element());
        DomGlobal.document.body.appendChild(namedBeanFieldInjectionPanel.element());
        DomGlobal.document.body.appendChild(namedBeanConstructorInjectionPanel.element());
        DomGlobal.document.body.appendChild(transitiveInjection.element());
        DomGlobal.document.body.appendChild(beanWithCDIEvents.element());

        initToast();
    }

    private void initToast() {
        toast.id = "snackbar";
        toast.textContent = "LuckyMe";

        DomGlobal.document.body.appendChild(toast);
    }

    void onUserEvent(@Observes User user) {
        toast.className = "show";
        toast.textContent = "App : onEvent " + user.toString();

        DomGlobal.setTimeout(p0 -> toast.className = toast.className.replace("show", ""), 3000);
    }

    void onAddressEvent(@Observes Address address) {
        toast.className = "show";
        toast.textContent = "App : onEvent " + address.toString();

        DomGlobal.setTimeout(p0 -> toast.className = toast.className.replace("show", ""), 3000);
    }
}
