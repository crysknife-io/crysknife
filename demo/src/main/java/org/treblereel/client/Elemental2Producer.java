package org.treblereel.client;

import javax.enterprise.inject.Produces;

import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLFormElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLLabelElement;

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

    @Produces
    public HTMLFormElement getFormElement() {
        return (HTMLFormElement) document.createElement("form");
    }

    @Produces
    public HTMLButtonElement getButton() {
        return (HTMLButtonElement) document.createElement("button");
    }

    @Produces
    public HTMLInputElement getInput() {
        return (HTMLInputElement) document.createElement("input");
    }

    @Produces
    public HTMLLabelElement getLabelElement() {
        return (HTMLLabelElement) document.createElement("label");
    }
}
