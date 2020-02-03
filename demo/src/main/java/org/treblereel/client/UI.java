package org.treblereel.client;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import elemental2.dom.HTMLDivElement;
import org.jboss.elemento.IsElement;
import org.treblereel.client.databinding.Databinding;
import org.treblereel.client.events.BeanWithCDIEvents;
import org.treblereel.client.mutationobserver.MutationObserverDemo;
import org.treblereel.gwt.crysknife.annotation.DataField;
import org.treblereel.gwt.crysknife.annotation.Templated;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 12/5/19
 */
@Singleton
@Templated(value = "ui.html")
public class UI implements IsElement<HTMLDivElement> {

    @Inject
    @DataField HTMLDivElement root;

    @Inject
    @DataField
    protected NamedBeanFieldInjectionPanel namedBeanFieldInjectionPanel;

    @Inject
    @DataField
    protected NamedBeanConstructorInjectionPanel namedBeanConstructorInjectionPanel;

    @Inject
    @DataField
    protected SingletonBeans singletonBeans;

    @Inject
    @DataField
    protected DependentBeans dependentBeans;

    @Inject
    @DataField
    protected TransitiveInjection transitiveInjection;

    @Inject
    @DataField
    protected BeanWithCDIEvents beanWithCDIEvents;

    @Inject
    @DataField
    protected Databinding databinding;

    @Inject
    @DataField
    protected MutationObserverDemo mutationObserverDemo;

    @PostConstruct
    public void init() {

    }

    @Override
    public HTMLDivElement element() {
        return root;
    }
}
