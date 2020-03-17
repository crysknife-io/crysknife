package org.treblereel.client.databinding;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import jsinterop.base.Js;
import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.user.client.ui.TextBox;
import org.jboss.elemento.IsElement;
import org.treblereel.gwt.crysknife.annotation.DataField;
import org.treblereel.gwt.crysknife.annotation.EventHandler;
import org.treblereel.gwt.crysknife.annotation.Templated;
import org.treblereel.gwt.crysknife.databinding.client.api.DataBinder;
import org.treblereel.gwt.crysknife.databinding.client.api.StateSync;
import org.treblereel.gwt.crysknife.databinding.client.api.handler.property.PropertyChangeHandler;
import org.treblereel.gwt.crysknife.navigation.client.local.Page;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 11/18/19
 */
@Singleton
@Page
@Templated(value = "databinding.html")
public class Databinding implements IsElement<HTMLDivElement> {

    @DataField HTMLDivElement root;

    @Inject
    protected DataBinder<Customer> dataBinder;
    @DataField
    @Inject
    protected TextBox nameBox;
    @DataField
    @Inject
    protected MyTextBox cityBox;
    @Inject
    @DataField
    protected HTMLInputElement streetBox;
    @Inject
    @DataField
    protected HTMLInputElement ageBox;
    @Inject
    @DataField
    protected HTMLInputElement resultBox;
    @Inject
    @DataField
    protected HTMLButtonElement modelBtn;
    @Inject
    @DataField
    protected HTMLButtonElement workingModelBtn;
    @Inject
    @DataField("pauseBtn")
    protected HTMLButtonElement pause;
    @Inject
    @DataField("resumeBtn")
    protected HTMLButtonElement resume;
    private Customer customer;

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

    @Override
    public HTMLDivElement element() {
        return root;
    }

    private void onPropertyChange(String state) {
        resultBox.value = state;
    }

    @EventHandler("cityBox")
    void onCityClick(final ClickEvent e) {
        DomGlobal.console.log("cityBox click");
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
    void onPause(final ClickEvent e) {
        dataBinder.pause();
    }

    @EventHandler("resumeBtn")
    void onResume(final ClickEvent e) {
        dataBinder.resume(StateSync.FROM_UI);
    }
}
