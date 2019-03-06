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
import org.treblereel.client.inject.named.Animal;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/22/19
 */
@Singleton
public class NamedBeanFieldInjectionPanel {

    @Inject
    HTMLDivElement form;

    @Inject
    HTMLDivElement formGroup;

    @Inject
    HTMLLabelElement formLabel;

    @Inject
    HTMLInputElement textBox;

    @Inject
    HTMLButtonElement birdBtn;

    @Inject
    HTMLButtonElement cowBtn;

    @Inject
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
        formGroup.className = "form-group";

        formLabel.textContent = "@Named beans, field injection";
        formLabel.setAttribute("setFor", "textBoxNamedBeanFieldInjectionPanel");
        formLabel.className = "control-label";

        textBox.id = "textBoxNamedBeanFieldInjectionPanel";
        textBox.disabled = true;
        textBox.className = "form-control";
        textBox.style.width = CSSProperties.WidthUnionType.of("300px");

        formGroup.appendChild(formLabel);
        formGroup.appendChild(textBox);

        form.appendChild(formGroup);

        initBtn();
    }

    private void initBtn() {
        birdBtn.textContent = "Bird";
        birdBtn.className = "btn btn-default";
        birdBtn.addEventListener("click", evt -> setText(bird.say()));

        cowBtn.textContent = "Cow";
        cowBtn.className = "btn btn-default";
        cowBtn.addEventListener("click", evt -> setText(cow.say()));

        dogBtn.textContent = "Bird";
        dogBtn.className = "btn btn-default";
        dogBtn.addEventListener("click", evt -> setText(dog.say()));

        formGroup.appendChild(birdBtn);
        formGroup.appendChild(cowBtn);
        formGroup.appendChild(dogBtn);
    }

    private void setText(String text) {
        textBox.value = text;
    }

    public HTMLElement asElement() {
        return form;
    }
}
