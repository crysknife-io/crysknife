package org.treblereel.client;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Form;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.TextBox;
import org.treblereel.client.inject.Injector;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/22/19
 */
@Singleton
public class TransitiveInjection implements IsWidget {

    @Inject
    Form form;

    @Inject
    FormGroup formGroup;

    @Inject
    FormLabel formLabel;

    @Inject
    TextBox textBox;

    @Inject
    Button checkBtn;

    @Inject
    Injector injector;

    @PostConstruct
    public void init() {
        formLabel.setText("Check, that injected bean also got injection and so on. Injector.callBeanTwo() -> BeanTwo.callBeanOne() -> Car.whoAmI()");
        formLabel.setFor("formSuccess");

        textBox.setId("textBox");
        textBox.setWidth("750px");
        textBox.setEnabled(false);

        formGroup.add(formLabel);
        formGroup.add(textBox);

        form.add(formGroup);

        initBtn();
    }

    private void initBtn() {
        checkBtn.setText("Check");
        checkBtn.addClickHandler(event -> {
            setText(injector.callBeanTwo());
        });
        formGroup.add(checkBtn);
    }

    private void setText(String text) {
        textBox.clear();
        textBox.setText(text);
    }

    @Override
    public Widget asWidget() {
        return form;
    }
}

