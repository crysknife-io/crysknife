package org.treblereel.client.navigation;

import javax.inject.Inject;
import javax.inject.Singleton;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import org.gwtproject.event.dom.client.ClickEvent;
import org.jboss.elemento.IsElement;
import org.treblereel.client.about.About;
import org.treblereel.gwt.crysknife.templates.client.annotation.DataField;
import org.treblereel.gwt.crysknife.templates.client.annotation.EventHandler;
import org.treblereel.gwt.crysknife.templates.client.annotation.Templated;
import org.treblereel.gwt.crysknife.navigation.client.local.Navigation;
import org.treblereel.gwt.crysknife.navigation.client.local.Page;
import org.treblereel.gwt.crysknife.navigation.client.local.PageHidden;
import org.treblereel.gwt.crysknife.navigation.client.local.PageHiding;
import org.treblereel.gwt.crysknife.navigation.client.local.PageShowing;
import org.treblereel.gwt.crysknife.navigation.client.local.PageShown;
import org.treblereel.gwt.crysknife.navigation.client.local.TransitionTo;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/1/20
 */
@Page(path = "navigation")
@Singleton
@Templated("navigation.html")
public class WidgetPanel implements IsElement<HTMLDivElement> {

    @Inject
    private Navigation navigation;

    @DataField
    private HTMLDivElement root;

    @Inject
    private TransitionTo<About> toAboutPage;

    @Inject
    @DataField
    private HTMLButtonElement button;

    @Override
    public HTMLDivElement element() {
        return root;
    }

    @EventHandler("button")
    public void onClick(final ClickEvent e) {
        toAboutPage.go();
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