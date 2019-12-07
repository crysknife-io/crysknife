/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.gwt.elemento.template;

import java.util.Collection;

import elemental2.core.JsArray;
import elemental2.dom.Attr;
import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import elemental2.dom.NamedNodeMap;
import elemental2.dom.Node;
import elemental2.dom.NodeFilter;
import elemental2.dom.TreeWalker;
import jsinterop.base.Js;
import org.gwtproject.safehtml.shared.SafeHtml;
import org.gwtproject.safehtml.shared.SafeHtmlUtils;
import org.gwtproject.user.client.ui.RootPanel;
import org.gwtproject.user.client.ui.Widget;
import org.jboss.gwt.elemento.core.IsElement;

/**
 * Static helper methods used from code generated by {@code @Templated} annotation processors. You should not need to
 * call any of these methods manually.
 */
@SuppressWarnings("UnnecessaryLocalVariable")
public final class TemplateUtil {

    private static SelectorFunction DATA_ELEMENT = (context, identifier) ->
            context.querySelector("[data-field=" + identifier + "]");

    private TemplateUtil() {
    }

    // ------------------------------------------------------ HTMLElement methods
    public static <E> E resolveElement(HTMLElement context, String identifier) {
        Element element = DATA_ELEMENT.select(context, identifier);
        return Js.cast(element);
    }

    public static <E extends HTMLElement> E resolveElementAs(HTMLElement context, String identifier) {
        Element element = DATA_ELEMENT.select(context, identifier);
        E htmlElement = Js.cast(element);
        return htmlElement;
    }

    // ------------------------------------------------------ IsElement / (Is)Widget methods

    public static void replaceElement(HTMLElement context, String identifier, HTMLElement newElement) {
        if (newElement == null) {
            throw new NullPointerException("New element must not be null in TemplateUtils.replaceElement()");
        }
        HTMLElement oldElement = resolveElement(context, identifier);
        if (oldElement != null && oldElement.parentNode != null) {
            oldElement.parentNode.replaceChild(newElement, oldElement);
            if (oldElement.hasAttributes()) {
                for (String attributeName : ((JsArray<String>)Js.uncheckedCast(oldElement.getAttributeNames())).asList()) {
                    newElement.setAttribute(attributeName, oldElement.getAttribute(attributeName));
                }
            }

            if(newElement.innerHTML.isEmpty() &&  !oldElement.innerHTML.isEmpty()){
                newElement.innerHTML = oldElement.innerHTML;
            }
        }
    }

    // ------------------------------------------------------ custom elements

    public static void replaceIsElement(HTMLElement context, String identifier, IsElement newElement) {
        replaceElement(context, identifier, newElement.getElement());
    }

    public static <E> E resolveCustomElement(HTMLElement context, String identifier) {
        Element element = DATA_ELEMENT.select(context, identifier);
        E customElement = Js.cast(element);
        return customElement;
    }

    // ------------------------------------------------------ expressions

    public static <E> void replaceCustomElement(HTMLElement context, String identifier, E customElement) {
        HTMLElement element = Js.cast(customElement);
        replaceElement(context, identifier, element);
    }

    public static void replaceExpression(HTMLElement context, String expression, Object value) {
        SafeHtml safeValue;
        if (value instanceof SafeHtml) {
            safeValue = (SafeHtml) value;
        } else {
            safeValue = SafeHtmlUtils.fromString(String.valueOf(value));
        }
        // Text nodes are automatically made safe by the browser, so we need to use value here instead of safeValue to avoid escaping the string twice.
        replaceNestedExpressionInText(context, expression, String.valueOf(value));
        replaceNestedExpressionInAttributes(context, expression, safeValue.asString());
        // The call above does not catch the attributes in 'context', we need to replace them explicitly.
        replaceExpressionInAttributes(context, expression, safeValue.asString());
    }

    private static void replaceNestedExpressionInText(HTMLElement context, String expression, String value) {
        // We would normally pass a NodeFilter object (containing an acceptNode
        // method) as the third argument to createTreeWalker. However,
        // Internet Explorer expects a function to be passed as the third
        // argument, not an object, and will in fact throw a JavaScriptError
        // on the first call to nextNode() if an object is provided instead of
        // a function.
        //
        // Therefore, we pass null as the third parameter here and handle the
        // filtering manually using an if statement in the while loop below.
        TreeWalker treeWalker = DomGlobal.document.createTreeWalker(context, NodeFilter.SHOW_TEXT, null, false);

        while (treeWalker.nextNode() != null) {
            if (treeWalker.getCurrentNode().nodeValue != null && treeWalker.getCurrentNode().nodeValue.contains(expression)) {
                treeWalker.getCurrentNode().nodeValue = treeWalker.getCurrentNode().nodeValue.replace(expression, value);
            }
        }
    }

    private static void replaceNestedExpressionInAttributes(HTMLElement context, String expression, String value) {
        TreeWalker treeWalker = DomGlobal.document.createTreeWalker(context, NodeFilter.SHOW_ELEMENT, null, false);
        while (treeWalker.nextNode() != null) {
            if (treeWalker.getCurrentNode() instanceof HTMLElement) {
                replaceExpressionInAttributes((HTMLElement) treeWalker.getCurrentNode(), expression, value);
            }
        }
    }

    private static void replaceExpressionInAttributes(HTMLElement context, String expression, String value) {
        NamedNodeMap<Attr> attributes = context.attributes;
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attribute = attributes.item(i);
            String currentValue = attribute.nodeValue;
            if (currentValue != null && currentValue.contains(expression)) {
                attribute.nodeValue = currentValue.replace(expression, value);
            }
        }
    }

    public static void initTemplated(final org.gwtproject.dom.client.Element wrapped, final Collection<Widget> dataFields) {
        // All template fragments are contained in a single element, during initialization.
        wrapped.removeFromParent();
        final TemplateWidget widget = new TemplateWidget(wrapped, dataFields);
        widget.onAttach();
        try {
            RootPanel.detachOnWindowClose(widget);
        } catch (Exception e) {

        }
    }

    @FunctionalInterface
    private interface SelectorFunction {

        Element select(HTMLElement context, String identifier);
    }
}
