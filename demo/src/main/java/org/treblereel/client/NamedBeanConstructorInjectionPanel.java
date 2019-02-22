package org.treblereel.client;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Form;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.TextBox;
import org.treblereel.client.inject.named.Vehicle;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/22/19
 */
@Singleton
public class NamedBeanConstructorInjectionPanel implements IsWidget {

    @Inject
    Form form;

    @Inject
    FormGroup formGroup;

    @Inject
    FormLabel formLabel;

    @Inject
    TextBox textBox;

    @Inject
    Button helicopterBtn;

    @Inject
    Button carBtn;

    Vehicle car;

    Vehicle helicopter;

    @Inject
    NamedBeanConstructorInjectionPanel(@Named("Helicopter") Vehicle helicopter, @Named("Car") Vehicle car) {
        this.car = car;
        this.helicopter = helicopter;
    }

    @PostConstruct
    public void init() {
        formLabel.setText("@Named beans, constructor injection");
        formLabel.setFor("formSuccess");

        textBox.setId("textBox");
        textBox.setWidth("250px");
        textBox.setEnabled(false);

        formGroup.add(formLabel);
        formGroup.add(textBox);

        form.add(formGroup);

        initBtn();
    }

    private void initBtn() {
        carBtn.setText("Car");
        carBtn.addClickHandler(event -> setText(car.whoAmI()));

        helicopterBtn.setText("Helicopter");
        helicopterBtn.addClickHandler(event -> setText(helicopter.whoAmI()));

        formGroup.add(carBtn);
        formGroup.add(helicopterBtn);

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

