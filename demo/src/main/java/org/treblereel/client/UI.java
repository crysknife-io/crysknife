package org.treblereel.client;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import org.jboss.elemento.IsElement;
import org.treblereel.client.databinding.Databinding;
import org.treblereel.client.dependent.DependentBeans;
import org.treblereel.client.events.BeanWithCDIEvents;
import org.treblereel.client.mutationobserver.MutationObserverDemo;
import org.treblereel.client.named.NamedBeanConstructorInjectionPanel;
import org.treblereel.client.named.NamedBeanFieldInjectionPanel;
import org.treblereel.client.singletonbeans.SingletonBeans;
import org.treblereel.gwt.crysknife.templates.client.annotation.DataField;
import org.treblereel.gwt.crysknife.templates.client.annotation.Templated;
import org.treblereel.gwt.crysknife.navigation.client.local.PageHidden;
import org.treblereel.gwt.crysknife.navigation.client.local.PageHiding;
import org.treblereel.gwt.crysknife.navigation.client.local.PageShowing;
import org.treblereel.gwt.crysknife.navigation.client.local.PageShown;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 12/5/19
 */
@Singleton
//@Page(role = DefaultPage.class)
@Templated(value = "ui.html")
public class UI implements IsElement<HTMLDivElement> {

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
    @Inject
    @DataField
    HTMLDivElement root;

    @PostConstruct
    public void init() {

    }

    @Override
    public HTMLDivElement element() {
        return root;
    }

    @PageShown
    public void onPageShown() {
        DomGlobal.console.log(this.getClass().getCanonicalName() + " PageShown");
    }

    @PageShowing
    public void onPageShowing() {
        DomGlobal.console.log(this.getClass().getCanonicalName() + " PageShowing");
    }

    @PageHidden
    public void onPageHidden() {
        DomGlobal.console.log(this.getClass().getCanonicalName() + " PageHidden");
    }

    @PageHiding
    public void onPageHiding() {
        DomGlobal.console.log(this.getClass().getCanonicalName() + " PageHiding");
    }
}
