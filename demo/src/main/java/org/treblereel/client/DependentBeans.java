package org.treblereel.client;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLInputElement;
import org.jboss.gwt.elemento.core.IsElement;
import org.treblereel.client.inject.DependentBean;
import org.treblereel.gwt.crysknife.annotation.DataField;
import org.treblereel.gwt.crysknife.annotation.Templated;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/22/19
 */
@Singleton
@Templated("dependentbeans.html")
public class DependentBeans implements IsElement<HTMLDivElement> {

    @Inject
    @DataField
    HTMLDivElement form;

    @DataField
    HTMLInputElement textBox;

    @DataField
    HTMLButtonElement checkBtn;

    @Inject
    DependentBean beanOne1Instance;

    @Inject
    DependentBean beanOne2Instance;

    @PostConstruct
    public void init() {
        initBtn();
    }

    private void initBtn() {
        checkBtn.addEventListener("click", event -> {

            StringBuffer sb = new StringBuffer();
            sb.append("beanOne1Instance random :");
            sb.append(beanOne1Instance.getRandom());
            sb.append(", beanOne2Instance random :");
            sb.append(beanOne2Instance.getRandom());
            sb.append(", ? equal " + (beanOne1Instance.getRandom() == beanOne2Instance.getRandom()));

            setText(sb.toString());
        });
    }

    private void setText(String text) {
        textBox.value = text;
        textBox.textContent = text;
    }

    @Override
    public HTMLDivElement element() {
        return form;
    }
}

