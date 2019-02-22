package org.treblereel.client;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLButtonElement;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/22/19
 */
@Singleton
public class Elemental2Bean {

    @Inject
    HTMLButtonElement buttonElement;

    @PostConstruct
    public void init() {
        buttonElement.textContent = "@Produces test -> HTMLButtonElement";
        buttonElement.className = "btn btn-default";
        buttonElement.addEventListener("click", evt -> DomGlobal.alert("HTMLButtonElement pressed"));

        DomGlobal.document.body.appendChild(buttonElement);
    }
}
