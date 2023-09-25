/*
 * Copyright © 2021 Treblereel
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package io.crysknife.ui.templates.generator;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLAreaElement;
import elemental2.dom.HTMLAudioElement;
import elemental2.dom.HTMLBRElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLCanvasElement;
import elemental2.dom.HTMLDataListElement;
import elemental2.dom.HTMLDetailsElement;
import elemental2.dom.HTMLDialogElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLEmbedElement;
import elemental2.dom.HTMLFieldSetElement;
import elemental2.dom.HTMLFormElement;
import elemental2.dom.HTMLHRElement;
import elemental2.dom.HTMLHeadingElement;
import elemental2.dom.HTMLImageElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLLIElement;
import elemental2.dom.HTMLLabelElement;
import elemental2.dom.HTMLLegendElement;
import elemental2.dom.HTMLMapElement;
import elemental2.dom.HTMLMenuElement;
import elemental2.dom.HTMLMenuItemElement;
import elemental2.dom.HTMLMeterElement;
import elemental2.dom.HTMLOListElement;
import elemental2.dom.HTMLObjectElement;
import elemental2.dom.HTMLOptGroupElement;
import elemental2.dom.HTMLOptionElement;
import elemental2.dom.HTMLOutputElement;
import elemental2.dom.HTMLParagraphElement;
import elemental2.dom.HTMLParamElement;
import elemental2.dom.HTMLPreElement;
import elemental2.dom.HTMLProgressElement;
import elemental2.dom.HTMLQuoteElement;
import elemental2.dom.HTMLScriptElement;
import elemental2.dom.HTMLSelectElement;
import elemental2.dom.HTMLSourceElement;
import elemental2.dom.HTMLTableCaptionElement;
import elemental2.dom.HTMLTableCellElement;
import elemental2.dom.HTMLTableColElement;
import elemental2.dom.HTMLTableElement;
import elemental2.dom.HTMLTableRowElement;
import elemental2.dom.HTMLTextAreaElement;
import elemental2.dom.HTMLTrackElement;
import elemental2.dom.HTMLUListElement;
import elemental2.dom.HTMLVideoElement;

import static java.util.Arrays.asList;

public class Elemental2TagMapping {

  public static final SetMultimap<String, String> HTML_ELEMENTS = HashMultimap.create();

  static {
    HTML_ELEMENTS.put(HTMLAnchorElement.class.getName(), "a");
    HTML_ELEMENTS.put(HTMLAreaElement.class.getName(), "area");
    HTML_ELEMENTS.put(HTMLAudioElement.class.getName(), "audio");
    HTML_ELEMENTS.put(HTMLQuoteElement.class.getName(), "blockquote");
    HTML_ELEMENTS.put(HTMLBRElement.class.getName(), "br");
    HTML_ELEMENTS.put(HTMLButtonElement.class.getName(), "button");
    HTML_ELEMENTS.put(HTMLCanvasElement.class.getName(), "canvas");
    HTML_ELEMENTS.put(HTMLTableCaptionElement.class.getName(), "caption");
    HTML_ELEMENTS.put(HTMLTableColElement.class.getName(), "col");
    HTML_ELEMENTS.put(HTMLDataListElement.class.getName(), "datalist");
    HTML_ELEMENTS.put(HTMLDetailsElement.class.getName(), "details");
    HTML_ELEMENTS.put(HTMLDialogElement.class.getName(), "dialog");
    HTML_ELEMENTS.put(HTMLDivElement.class.getName(), "div");
    HTML_ELEMENTS.put(HTMLEmbedElement.class.getName(), "embed");
    HTML_ELEMENTS.put(HTMLFieldSetElement.class.getName(), "fieldset");
    HTML_ELEMENTS.put(HTMLFormElement.class.getName(), "form");
    HTML_ELEMENTS.putAll(HTMLHeadingElement.class.getName(),
        asList("h1", "h2", "h3", "h4", "h5", "h6"));
    HTML_ELEMENTS.put(HTMLHRElement.class.getName(), "hr");
    HTML_ELEMENTS.put(HTMLImageElement.class.getName(), "img");
    HTML_ELEMENTS.put(HTMLInputElement.class.getName(), "input");
    HTML_ELEMENTS.put(HTMLLabelElement.class.getName(), "label");
    HTML_ELEMENTS.put(HTMLLegendElement.class.getName(), "legend");
    HTML_ELEMENTS.put(HTMLLIElement.class.getName(), "li");
    HTML_ELEMENTS.put(HTMLMapElement.class.getName(), "map");
    HTML_ELEMENTS.put(HTMLMenuElement.class.getName(), "menu");
    HTML_ELEMENTS.put(HTMLMenuItemElement.class.getName(), "menuitem");
    HTML_ELEMENTS.put(HTMLMeterElement.class.getName(), "meter");
    HTML_ELEMENTS.put(HTMLObjectElement.class.getName(), "object");
    HTML_ELEMENTS.put(HTMLOListElement.class.getName(), "ol");
    HTML_ELEMENTS.put(HTMLOptGroupElement.class.getName(), "optgroup");
    HTML_ELEMENTS.put(HTMLOptionElement.class.getName(), "option");
    HTML_ELEMENTS.put(HTMLOutputElement.class.getName(), "output");
    HTML_ELEMENTS.put(HTMLParagraphElement.class.getName(), "p");
    HTML_ELEMENTS.put(HTMLParamElement.class.getName(), "param");
    HTML_ELEMENTS.put(HTMLPreElement.class.getName(), "pre");
    HTML_ELEMENTS.put(HTMLProgressElement.class.getName(), "progress");
    HTML_ELEMENTS.put(HTMLQuoteElement.class.getName(), "q");
    HTML_ELEMENTS.put(HTMLScriptElement.class.getName(), "script");
    HTML_ELEMENTS.put(HTMLSelectElement.class.getName(), "select");
    HTML_ELEMENTS.put(HTMLSourceElement.class.getName(), "source");
    HTML_ELEMENTS.put(HTMLTableElement.class.getName(), "table");
    HTML_ELEMENTS.put(HTMLTableCellElement.class.getName(), "td");
    HTML_ELEMENTS.put(HTMLTableCellElement.class.getName(), "th");
    HTML_ELEMENTS.put(HTMLTextAreaElement.class.getName(), "textarea");
    HTML_ELEMENTS.put(HTMLTableRowElement.class.getName(), "tr");
    HTML_ELEMENTS.put(HTMLTrackElement.class.getName(), "track");
    HTML_ELEMENTS.put(HTMLUListElement.class.getName(), "ul");
    HTML_ELEMENTS.put(HTMLVideoElement.class.getName(), "video");

  }
}
