package org.treblereel.gwt.crysknife.templates.client;

import elemental2.dom.Document;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLStyleElement;
import jsinterop.base.Js;

public class StyleInjector {

    private StyleInjector() {
    }

    public static FromString fromString(String styleBody) {
        return new FromString(styleBody);
    }

    private static HTMLStyleElement createElement(String contents) {
        HTMLStyleElement style = (HTMLStyleElement) DomGlobal.document.createElement("style");
        style.setAttribute("language", "text/css");
        style.innerHTML = contents;
        return style;
    }

    /**
     * Builder for directly injecting a script body into the DOM.
     */
    public static class FromString {

        private final String styleBody;

        /**
         * @param styleBody The script text to install into the document.
         */
        public FromString(String styleBody) {
            this.styleBody = styleBody;
        }

        public HTMLStyleElement inject() {
            Document doc = DomGlobal.document;
            HTMLStyleElement style = createElement(styleBody);
            doc.head.appendChild(style);
            return Js.cast(style);
        }
    }
}
