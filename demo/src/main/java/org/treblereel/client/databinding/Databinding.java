package org.treblereel.client.databinding;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLLabelElement;
import org.gwtproject.event.dom.client.ClickEvent;
import org.jboss.gwt.elemento.core.IsElement;
import org.treblereel.gwt.crysknife.annotation.DataField;
import org.treblereel.gwt.crysknife.annotation.EventHandler;
import org.treblereel.gwt.crysknife.annotation.Templated;
import org.treblereel.gwt.crysknife.databinding.client.api.DataBinder;
import org.treblereel.gwt.crysknife.databinding.client.api.StateSync;
import org.treblereel.gwt.crysknife.databinding.client.api.handler.property.PropertyChangeHandler;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 11/18/19
 */
@Singleton
@Templated(value = "databinding.html")
public class Databinding implements IsElement<HTMLDivElement> {

    @Inject
    DataBinder<Customer> dataBinder;

    Customer customer;

    @Inject
    @DataField
    HTMLInputElement nameBox;

    @Inject
    @DataField
    HTMLInputElement cityBox;

    @Inject
    @DataField
    HTMLInputElement streetBox;

    @Inject
    @DataField
    HTMLInputElement ageBox;

    @Inject
    @DataField
    HTMLInputElement resultBox;

    @Inject
    @DataField
    HTMLButtonElement modelBtn;

    @Inject
    @DataField
    HTMLButtonElement workingModelBtn;

    @Inject
    @DataField("pauseBtn")
    HTMLButtonElement pause;

    @Inject
    @DataField("resumeBtn")
    HTMLButtonElement resume;

    @PostConstruct
    public void init() {

        customer = dataBinder
                .bind(nameBox, "name")
                .bind(cityBox, "address.city")
                .bind(streetBox, "address.street.name")
                .bind(ageBox, "age")
                .getModel();

        dataBinder.addPropertyChangeHandler((PropertyChangeHandler<Customer>) event -> {
            DomGlobal.console.log("new value " + event.toString());
            onPropertyChange(event.toString());
        });
    }

    private void onPropertyChange(String state) {
        resultBox.value = state;
    }

    @EventHandler("modelBtn")
    void getModel(final ClickEvent e) {
        onPropertyChange(dataBinder.getModel().toString());
    }

    @EventHandler("workingModelBtn")
    void getWorkingModel(final ClickEvent e) {
        onPropertyChange(dataBinder.getWorkingModel().toString());
    }

    @EventHandler("pauseBtn")
    void pause(final ClickEvent e) {
        dataBinder.pause();
    }

    @EventHandler("resumeBtn")
    void resume(final ClickEvent e) {
        dataBinder.resume(StateSync.FROM_UI);
    }
}
