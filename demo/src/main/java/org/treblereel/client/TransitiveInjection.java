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
import org.treblereel.client.inject.Injector;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/22/19
 */
@Singleton
public class TransitiveInjection {

    @Inject
    HTMLDivElement form;

    @Inject
    HTMLDivElement formGroup;

    @Inject
    HTMLLabelElement formLabel;

    @Inject
    HTMLInputElement textBox;

    @Inject
    HTMLButtonElement checkBtn;

    @Inject
    Injector injector;

    @PostConstruct
    public void init() {
        formGroup.className = "form-group";

        formLabel.textContent = "Check, that injected bean also got injection and so on. Injector.callBeanTwo() -> BeanTwo.callBeanOne() -> Car.whoAmI()";
        formLabel.setAttribute("setFor", "TransitiveInjection");
        formLabel.className = "control-label";

        textBox.id = "TransitiveInjection";
        textBox.disabled = true;
        textBox.className = "form-control";
        textBox.style.width = CSSProperties.WidthUnionType.of("300px");

        formGroup.appendChild(formLabel);
        formGroup.appendChild(textBox);

        form.appendChild(formGroup);

        initBtn();
    }

    private void initBtn() {
        checkBtn.textContent = "Check";
        checkBtn.className = "btn btn-default";
        checkBtn.addEventListener("click", evt -> {
            setText(injector.callBeanTwo());

        });
        formGroup.appendChild(checkBtn);
    }

    private void setText(String text) {
        textBox.value = text;
    }

    public HTMLElement asElement() {
        return form;
    }

}

