//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jboss.gwt.elemento.processor;

import javax.lang.model.element.Element;

public class ProcessingException extends RuntimeException {
    private final Element element;

    public ProcessingException(String msg) {
        this((Element)null, msg);
    }

    public ProcessingException(Element element, String msg) {
        super(msg);
        this.element = element;
    }

    public Element getElement() {
        return this.element;
    }
}
