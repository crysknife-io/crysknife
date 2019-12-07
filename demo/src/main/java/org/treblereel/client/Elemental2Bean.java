package org.treblereel.client;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLHeadingElement;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/22/19
 */
@Singleton
public class Elemental2Bean {

    @Inject
    protected HTMLButtonElement buttonElement;

    @Inject
    @Named("h5")
    protected HTMLHeadingElement headingElement;

    @PostConstruct
    public void init() {
        buttonElement.textContent = "@Produces test -> HTMLButtonElement";
        buttonElement.className = "btn btn-default";
        buttonElement.addEventListener("click", evt -> DomGlobal.alert("HTMLButtonElement pressed"));

        DomGlobal.document.body.appendChild(buttonElement);
    }
}
