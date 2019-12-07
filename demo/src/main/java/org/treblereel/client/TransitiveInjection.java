package org.treblereel.client;

import javax.inject.Inject;
import javax.inject.Singleton;

import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.MouseEvent;
import org.jboss.gwt.elemento.core.IsElement;
import org.treblereel.client.inject.Injector;
import org.treblereel.gwt.crysknife.annotation.DataField;
import org.treblereel.gwt.crysknife.annotation.EventHandler;
import org.treblereel.gwt.crysknife.annotation.ForEvent;
import org.treblereel.gwt.crysknife.annotation.Templated;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/22/19
 */
@Singleton
@Templated("transitiveinjection.html")
public class TransitiveInjection implements IsElement<HTMLDivElement> {

    @Inject
    @DataField
    protected HTMLDivElement form;

    @Inject
    @DataField
    protected HTMLInputElement textBox;

    @Inject
    @DataField
    protected HTMLButtonElement checkBtn;

    @Inject
    protected Injector injector;

    private void setText(String text) {
        textBox.value = text;
    }

    @Override
    public HTMLDivElement getElement() {
        return form;
    }

    @EventHandler("checkBtn")
    protected void onClick(@ForEvent("click") final MouseEvent event) {
        setText(injector.callBeanTwo());
    }
}

