package org.treblereel.client.qualifiers;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.MouseEvent;
import org.jboss.elemento.IsElement;
import org.treblereel.gwt.crysknife.templates.client.annotation.DataField;
import org.treblereel.gwt.crysknife.templates.client.annotation.EventHandler;
import org.treblereel.gwt.crysknife.templates.client.annotation.ForEvent;
import org.treblereel.gwt.crysknife.templates.client.annotation.Templated;
import org.treblereel.gwt.crysknife.navigation.client.local.Page;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/15/20
 */
@ApplicationScoped
@Page
@Templated(value = "qualifiers.html")
public class Qualifiers implements IsElement<HTMLDivElement> {

    @Inject
    @DataField
    HTMLDivElement root;

    @Inject
    @DataField
    private HTMLInputElement textBox;

    @Inject
    @DataField
    private HTMLButtonElement defaultBtn;

    @Inject
    @DataField
    private HTMLButtonElement qualifierOneBtn;

    @Inject
    @DataField
    private HTMLButtonElement qualifierTwoBtn;

    @Inject
    @QualifierOne
    private QualifierBean one;

    @Inject
    @QualifierTwo
    private QualifierBean two;

    @Inject
    @Default
    private QualifierBean three;

    @EventHandler("defaultBtn")
    protected void onClickCar(@ForEvent("click") final MouseEvent event) {
        setText(three.say());
    }

    private void setText(String text) {
        textBox.value = text;
    }

    @EventHandler("qualifierOneBtn")
    protected void onClickOne(@ForEvent("click") final MouseEvent event) {
        setText(one.say());
    }

    @EventHandler("qualifierTwoBtn")
    protected void onClickTwo(@ForEvent("click") final MouseEvent event) {
        setText(two.say());
    }

    @Override
    public HTMLDivElement element() {
        return root;
    }
}