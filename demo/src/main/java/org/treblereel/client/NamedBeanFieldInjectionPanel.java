package org.treblereel.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Form;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.TextBox;
import org.treblereel.client.inject.named.Animal;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/22/19
 */
@Singleton
public class NamedBeanFieldInjectionPanel implements IsWidget {

    @Inject
    Form form;

    @Inject
    FormGroup formGroup;

    @Inject
    FormLabel formLabel;

    @Inject
    TextBox textBox;

    @Inject
    Button birdBtn;

    @Inject
    Button cowBtn;

    @Inject
    Button dogBtn;

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
    public void init(){
        formLabel.setText("@Named beans, field injection");
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
        birdBtn.setText("Bird");
        birdBtn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setText(bird.say());
            }
        });

        cowBtn.setText("Cow");
        cowBtn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setText(cow.say());
            }
        });

        dogBtn.setText("Bird");
        dogBtn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setText(dog.say());
            }
        });

        formGroup.add(birdBtn);
        formGroup.add(cowBtn);
        formGroup.add(dogBtn);

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
