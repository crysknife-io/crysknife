package org.treblereel.client.events;

import java.util.Random;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import elemental2.dom.CSSProperties;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLLabelElement;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/31/19
 */
@Singleton
public class BeanWithCDIEvents {

    @Inject
    HTMLDivElement form;

    @Inject
    HTMLDivElement formGroup;

    @Inject
    HTMLLabelElement formLabel;

    @Inject
    HTMLInputElement textBox;

    @Inject
    Event<User> eventUser;

    @Inject
    Event<Address> eventAddress;

    @Inject
    HTMLButtonElement sendUserEvent, sendAddressEvent;

    @PostConstruct
    public void init() {
        formGroup.className = "form-group";

        formLabel.textContent = "Bean with Event<User> and Event<Address>";
        formLabel.setAttribute("setFor", "BeanWithCDIEvents");
        formLabel.className = "control-label";

        textBox.id = "BeanWithCDIEvents";
        textBox.disabled = true;
        textBox.className = "form-control";
        textBox.style.width = CSSProperties.WidthUnionType.of("300px");

        formGroup.appendChild(formLabel);
        formGroup.appendChild(textBox);

        form.appendChild(formGroup);

        initBtn();
    }

    private void initBtn() {
        sendUserEvent.textContent = "User event";
        sendUserEvent.className = "btn btn-default";
        sendUserEvent.addEventListener("click", evt -> {
            User user = new User();
            user.setId(new Random().nextInt());
            user.setName("IAMUSER");
            eventUser.fire(user);
        });

        sendAddressEvent.textContent = "Address event";
        sendAddressEvent.className = "btn btn-default";
        sendAddressEvent.addEventListener("click", evt -> {
            Address address = new Address();
            address.setId(new Random().nextInt());
            address.setName("Redhat");
            eventAddress.fire(address);
        });

        formGroup.appendChild(sendUserEvent);
        formGroup.appendChild(sendAddressEvent);
    }

    public void OnUserEvent(@Observes User user) {
        setText(user.toString());
    }

    public void OnAddressEvent(@Observes Address address) {
        setText(address.toString());
    }

    private void setText(String text) {
        textBox.value = text;
    }

    public HTMLElement asElement() {
        return form;
    }
}
