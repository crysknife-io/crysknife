package org.treblereel.client;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import jsinterop.annotations.JsFunction;
import org.gwtproject.core.client.EntryPoint;
import org.gwtproject.resources.client.ResourceCallback;
import org.gwtproject.resources.client.ResourceException;
import org.jboss.elemento.Elements;
import org.treblereel.client.events.Address;
import org.treblereel.client.events.User;
import org.treblereel.client.resources.TextResource;
import org.treblereel.gwt.crysknife.client.Application;
import org.treblereel.gwt.crysknife.client.ComponentScan;

import static org.jboss.elemento.Elements.body;

@Application
@ComponentScan("org.treblereel.client")
public class App implements EntryPoint {

    @Inject
    private HTMLDivElement toast;

    @Inject
    private TextResource textResource;

    @Inject
    private UI ui;

    @Inject
    private NamedBeanConstructorInjectionPanel namedBeanConstructorInjectionPanel;

    @Inject
    private SingletonBeans singletonBeans;

    @Override
    public void onModuleLoad() {
        new AppBootstrap(this).initialize();
    }

    @PostConstruct
    public void init() {
        body().add(ui);
        DomGlobal.console.log(textResource.helloWorldRelative().getText());

        try {
            textResource.helloWorldExternal().getText(new ResourceCallback<org.gwtproject.resources.client.TextResource>() {
                @Override
                public void onError(ResourceException e) {
                    DomGlobal.alert("[Error] " + e.getMessage());
                }

                @Override
                public void onSuccess(org.gwtproject.resources.client.TextResource textResource) {
                    DomGlobal.console.log("external " + textResource.getText());
                }
            });
        } catch (ResourceException e) {
            DomGlobal.alert("[Error] " + e.getMessage());
        }

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
