/*
 * Copyright © 2020 Treblereel
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package io.crysknife.demo.client.navigation;

import javax.inject.Inject;
import javax.inject.Singleton;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import org.gwtproject.event.dom.client.ClickEvent;
import io.crysknife.demo.client.about.About;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.Templated;
import io.crysknife.ui.navigation.client.local.Navigation;
import io.crysknife.ui.navigation.client.local.Page;
import io.crysknife.ui.navigation.client.local.PageHidden;
import io.crysknife.ui.navigation.client.local.PageHiding;
import io.crysknife.ui.navigation.client.local.PageShowing;
import io.crysknife.ui.navigation.client.local.PageShown;
import io.crysknife.ui.navigation.client.local.TransitionTo;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/1/20
 */
@Page(path = "navigation")
@Singleton
@Templated("navigation.html")
public class WidgetPanel implements io.crysknife.client.IsElement<HTMLDivElement> {

    @Inject
    private Navigation navigation;

    @Inject
    private TransitionTo<About> toAboutPage;

    @Inject
    @DataField
    private HTMLButtonElement button;

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