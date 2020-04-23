package org.treblereel.client;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import org.gwtproject.core.client.EntryPoint;
import org.treblereel.client.events.Address;
import org.treblereel.client.events.User;
import org.treblereel.client.named.NamedBeanConstructorInjectionPanel;
import org.treblereel.client.resources.TextResource;
import org.treblereel.gwt.crysknife.client.Application;
import org.treblereel.gwt.crysknife.client.ComponentScan;
import org.treblereel.gwt.crysknife.navigation.client.local.DefaultPage;
import org.treblereel.gwt.crysknife.navigation.client.local.Navigation;

@Application
@ComponentScan("org.treblereel.client")
public class App implements EntryPoint {

    @Inject
    private HTMLDivElement toast;

    @Inject
    private TextResource textResource;

    @Inject
    private NamedBeanConstructorInjectionPanel namedBeanConstructorInjectionPanel;

    @Inject
    private Main main;

    @Inject
    private Navigation navigation;

    @Override
    public void onModuleLoad() {
        new AppBootstrap(this).initialize();
    }

    @PostConstruct
    public void init() {
        DomGlobal.document.body.appendChild(main.element());
        initToast();
        navigation.goToWithRole(DefaultPage.class);
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
