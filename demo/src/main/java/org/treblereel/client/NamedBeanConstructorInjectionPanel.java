package org.treblereel.client;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import elemental2.dom.CSSProperties;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLLabelElement;
import org.treblereel.client.inject.named.Vehicle;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/22/19
 */
@Singleton
public class NamedBeanConstructorInjectionPanel {

    @Inject
    HTMLDivElement form;

    @Inject
    HTMLDivElement formGroup;

    @Inject
    HTMLLabelElement formLabel;

    @Inject
    HTMLInputElement textBox;

    @Inject
    HTMLButtonElement helicopterBtn;

    @Inject
    HTMLButtonElement carBtn;

    Vehicle car;

    Vehicle helicopter;

    @Inject
    NamedBeanConstructorInjectionPanel(@Named("Helicopter") Vehicle helicopter, @Named("Car") Vehicle car) {
        this.car = car;
        this.helicopter = helicopter;
    }

    @PostConstruct
    public void init() {
        formGroup.className = "form-group";

        formLabel.textContent = "@Named beans, constructor injection";
        formLabel.setAttribute("setFor", "NamedBeanConstructorInjectionPanel");
        formLabel.className = "control-label";

        textBox.id = "NamedBeanConstructorInjectionPanel";
        textBox.disabled = true;
        textBox.className = "form-control";
        textBox.style.width = CSSProperties.WidthUnionType.of("300px");

        formGroup.appendChild(formLabel);
        formGroup.appendChild(textBox);

        form.appendChild(formGroup);

        initBtn();
    }

    private void initBtn() {
        carBtn.textContent = "Car";
        carBtn.className = "btn btn-default";
        carBtn.addEventListener("click", evt -> setText(car.whoAmI()));

        helicopterBtn.textContent = "Helicopter";
        helicopterBtn.className = "btn btn-default";
        helicopterBtn.addEventListener("click", evt -> setText(helicopter.whoAmI()));

        formGroup.appendChild(carBtn);
        formGroup.appendChild(helicopterBtn);
    }

    private void setText(String text) {
        textBox.value = text;
    }

    public HTMLElement asElement() {
        return form;
    }
}

