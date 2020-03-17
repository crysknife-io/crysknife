package org.treblereel.gwt.crysknife.navigation.client.local;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.jboss.elemento.IsElement;

/**
 * This class exists to make the default navigation panel injectable without
 * requiring a qualifier.
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class NavigationPanel implements IsElement {

    private final HTMLDivElement holder = (HTMLDivElement) DomGlobal.document.createElement("div");

    NavigationPanel() {

    }

    @Override
    public HTMLElement element() {
        return holder;
    }
}
