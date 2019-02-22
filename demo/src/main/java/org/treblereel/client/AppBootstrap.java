package org.treblereel.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import elemental2.dom.HTMLDivElement;
import org.treblereel.client.inject.Injector;
import org.treblereel.client.inject.named.Animal;
import org.treblereel.gwt.crysknife.client.Application;
import org.treblereel.gwt.crysknife.client.ComponentScan;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import static elemental2.dom.DomGlobal.document;

@Application
@ComponentScan("org.treblereel.client.inject")
public class AppBootstrap implements EntryPoint {

    @Inject
    Injector injector;

    @Inject
    HTMLDivElement div;


    @Inject
    @Named("dog")
    Animal dog;

    @Inject
    @Named("cow")
    Animal cow;

    @Inject
    @Named("bird")
    Animal bird;

    @Inject
    DependentBean dependentBean;

    @Override
    public void onModuleLoad() {
        new AppBootstrapBootstrap(this).initialize();


    }

    @PostConstruct
    public void init(){
        GWT.log("onInit");
        injector.say();

        dependentBean.sayHello();

        div.id = "ololo";
        document.body.appendChild(div);
    }
}
