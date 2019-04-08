package org.treblereel.client;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import elemental2.dom.CSSProperties;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLLabelElement;
import org.jboss.gwt.elemento.core.IsElement;
import org.treblereel.client.inject.Injector;
import org.treblereel.gwt.crysknife.annotation.DataField;
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
    HTMLDivElement form;

    @DataField
    HTMLInputElement textBox;

    @DataField
    HTMLButtonElement checkBtn;

    @Inject
    Injector injector;

    @PostConstruct
    public void init() {
        initBtn();
    }

    private void initBtn() {
        checkBtn.addEventListener("click", evt -> {
            setText(injector.callBeanTwo());

        });
    }

    private void setText(String text) {
        textBox.value = text;
    }

    @Override
    public HTMLDivElement element() {
        return form;
    }
}

