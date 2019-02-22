package org.treblereel.client;

import elemental2.dom.HTMLDivElement;

import javax.enterprise.inject.Produces;

import static elemental2.dom.DomGlobal.document;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/21/19
 */
public class Elemental2Producer {


    @Produces
    public HTMLDivElement getDiv() {
        return (HTMLDivElement) document.createElement("div");
    }
}
