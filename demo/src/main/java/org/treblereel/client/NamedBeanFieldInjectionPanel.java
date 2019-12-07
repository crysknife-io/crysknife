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
    protected HTMLDivElement form;

    @Inject
    @DataField
    protected HTMLInputElement textBox;

    @Inject
    @DataField
    protected HTMLButtonElement birdBtn;

    @Inject
    @DataField
    protected HTMLButtonElement cowBtn;

    @Inject
    @DataField
    protected HTMLButtonElement dogBtn;

    @Inject
    @Named("dog")
    protected Animal dog;

    @Inject
    @Named("cow")
    protected Animal cow;

    @Inject
    @Named("bird")
    protected Animal bird;

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
