package org.treblereel.client;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import elemental2.dom.CSSProperties;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLFormElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLLabelElement;
import org.jboss.gwt.elemento.core.IsElement;
import org.treblereel.client.inject.named.Animal;
import org.treblereel.gwt.crysknife.annotation.DataField;
import org.treblereel.gwt.crysknife.annotation.Templated;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/22/19
 */
@Singleton
@Templated("namedbeanfieldinjectionpanel.html")
public class NamedBeanFieldInjectionPanel implements IsElement<HTMLDivElement> {

    @Inject
    @DataField
    HTMLDivElement form;

    @DataField
    HTMLInputElement textBox;

    @DataField
    HTMLButtonElement birdBtn;

    @DataField
    HTMLButtonElement cowBtn;

    @DataField
    HTMLButtonElement dogBtn;

    @Inject
    @Named("dog")
    Animal dog;

    @Inject
    @Named("cow")
    Animal cow;

    @Inject
    @Named("bird")
    Animal bird;

    @PostConstruct
    public void init() {
        initBtn();
    }

    private void initBtn() {
        birdBtn.addEventListener("click", evt -> setText(bird.say()));

        cowBtn.addEventListener("click", evt -> setText(cow.say()));

        dogBtn.addEventListener("click", evt -> setText(dog.say()));

    }

    private void setText(String text) {
        textBox.value = text;
    }

    @Override
    public HTMLDivElement element() {
        return form;
    }
}
