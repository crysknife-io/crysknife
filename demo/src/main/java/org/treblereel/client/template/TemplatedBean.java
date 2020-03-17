package org.treblereel.client.template;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import org.gwtproject.event.dom.client.ClickEvent;
import org.jboss.elemento.IsElement;
import org.treblereel.gwt.crysknife.annotation.DataField;
import org.treblereel.gwt.crysknife.annotation.EventHandler;
import org.treblereel.gwt.crysknife.annotation.Templated;
import org.treblereel.gwt.crysknife.navigation.client.local.Page;

@Singleton
@Page
@Templated("templated.html")
public class TemplatedBean implements IsElement<HTMLDivElement> {

    @Inject
    @DataField
    private HTMLDivElement root;

    @Inject
    @DataField
    private HTMLButtonElement button;

    @Inject
    @DataField
    private HTMLButtonElement button1;

    @PostConstruct
    public void init() {
        button1.textContent = " PressMe";
    }

    @EventHandler("button")
    public void onClick(final ClickEvent e) {

    }

    public HTMLDivElement element() {
        return root;
    }
}
