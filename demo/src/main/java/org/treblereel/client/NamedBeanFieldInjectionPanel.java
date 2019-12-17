package org.treblereel.client;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.MouseEvent;
import org.jboss.gwt.elemento.core.IsElement;
import org.treblereel.client.inject.named.Animal;
import org.treblereel.gwt.crysknife.annotation.DataField;
import org.treblereel.gwt.crysknife.annotation.EventHandler;
import org.treblereel.gwt.crysknife.annotation.ForEvent;
import org.treblereel.gwt.crysknife.annotation.Templated;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/22/19
 */
@Dependent
@Templated("namedbeanfieldinjectionpanel.html")
public class NamedBeanFieldInjectionPanel implements IsElement<HTMLDivElement> {

    @Inject
    @DataField
    private HTMLDivElement form;

    @Inject
    @DataField
    private HTMLInputElement textBox;

    @Inject
    @DataField
    private HTMLButtonElement birdBtn;

    @Inject
    @DataField
    private HTMLButtonElement cowBtn;

    @Inject
    @DataField
    private HTMLButtonElement dogBtn;

    @Inject
    @Named("dog")
    private Animal dog;

    @Inject
    @Named("cow")
    private Animal cow;

    @Inject
    @Named("bird")
    private Animal bird;

    private void setText(String text) {
        textBox.value = text;
    }

    @Override
    public HTMLDivElement getElement() {
        return form;
    }

    @EventHandler("birdBtn")
    protected void onClickBird(@ForEvent("click") final MouseEvent event) {
        setText(bird.say());
    }

    @EventHandler("cowBtn")
    protected void onClickCow(@ForEvent("click") final MouseEvent event) {
        setText(cow.say());
    }

    @EventHandler("dogBtn")
    protected void onClickDog(@ForEvent("click") final MouseEvent event) {
        setText(dog.say());
    }
}
