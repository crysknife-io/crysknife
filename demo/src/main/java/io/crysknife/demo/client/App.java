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

package io.crysknife.demo.client;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import io.crysknife.annotation.Application;
import io.crysknife.demo.client.events.Address;
import io.crysknife.demo.client.events.User;
import io.crysknife.ui.navigation.client.Navigation;
import org.patternfly.component.page.PageMain;
import org.patternfly.component.page.PageSidebar;
import org.treblereel.j2cl.processors.annotations.GWT3EntryPoint;

import static org.jboss.elemento.Elements.body;
import static org.patternfly.component.navigation.ExpandableNavigationGroup.expandableNavigationGroup;
import static org.patternfly.component.navigation.Navigation.navigation;
import static org.patternfly.component.navigation.NavigationItem.navigationItem;
import static org.patternfly.component.navigation.NavigationType.Vertical.expandable;
import static org.patternfly.component.page.Masthead.masthead;
import static org.patternfly.component.page.MastheadBrand.mastheadBrand;
import static org.patternfly.component.page.MastheadContent.mastheadContent;
import static org.patternfly.component.page.MastheadLogo.mastheadLogo;
import static org.patternfly.component.page.MastheadMain.mastheadMain;
import static org.patternfly.component.page.MastheadToggle.mastheadToggle;
import static org.patternfly.component.page.Page.page;
import static org.patternfly.component.page.PageMain.pageMain;
import static org.patternfly.component.page.PageSidebar.pageSidebar;
import static org.patternfly.component.page.PageSidebarBody.pageSidebarBody;
import static org.patternfly.component.title.Title.title;
import static org.patternfly.style.Size.lg;

@Application(packages = {"io.crysknife"})
public class App {

    @Inject
    private HTMLDivElement toast;

    @Inject
    private Navigation crysNavigation;

    @GWT3EntryPoint
    public void onModuleLoad() {
        new AppBootstrap(this).initialize();
    }

    @PostConstruct
    public void init() {
        PageMain main = pageMain("main-content");
        crysNavigation.setNavigationContainer(main.element());

        PageSidebar sidebar = pageSidebar()
            .addBody(pageSidebarBody()
                .addNavigation(navigation(expandable)
                    .addItem(navigationItem("home", "Home", "#HomePage"))
                    .addItem(navigationItem("getstarted", "Get Started", "#GetStarted"))
                    .addGroup(expandableNavigationGroup("cdi", "CDI")
                        .addItem(navigationItem("dependent", "@Dependent", "#dependent"))
                        .addItem(navigationItem("singleton", "@Singleton", "#SingletonBeans"))
                        .addItem(navigationItem("named", "@Named", "#Named"))
                        .addItem(navigationItem("qualifier", "@Qualifier", "#Qualifiers"))
                        .addItem(navigationItem("events", "CDI Events", "#BeanWithCDIEvents")))
                    .addGroup(expandableNavigationGroup("advanced", "Advanced CDI")
                        .addItem(navigationItem("interceptor", "@Interceptor", "#InterceptorDemo"))
                        .addItem(navigationItem("decorator", "@Decorator", "#DecoratorDemo"))
                        .addItem(navigationItem("produces", "@Produces", "#ProducesDemo"))
                        .addItem(navigationItem("alternative", "@Alternative", "#AlternativeDemo"))
                        .addItem(navigationItem("specializes", "@Specializes", "#SpecializesDemo"))
                        .addItem(navigationItem("typed", "@Typed", "#TypedDemo"))
                        .addItem(navigationItem("provider", "Provider<T>", "#ProviderDemo"))
                        .addItem(navigationItem("managedinstance", "ManagedInstance<T>", "#ManagedInstanceDemo"))
                        .addItem(navigationItem("startup", "@Startup", "#StartupDemo")))
                    .addGroup(expandableNavigationGroup("ui", "UI")
                        .addItem(navigationItem("templated", "@Templated", "#TemplatedBean"))
                        .addItem(navigationItem("databinder", "DataBinder", "#DataBinderDemo"))
                        .addItem(navigationItem("navigation", "Navigation", "#navigation"))
                        .addItem(navigationItem("mutation", "MutationObserver", "#MutationObserverDemo"))
                        .addItem(navigationItem("rest", "REST Client", "#RestDemo"))
                        .addItem(navigationItem("websocket", "WebSocket", "#WebSocketDemo")))));
        sidebar.collapse(false);

        body().add(page()
            .addMasthead(masthead()
                .addMain(mastheadMain()
                    .addToggle(mastheadToggle().toggleSidebar())
                    .addBrand(mastheadBrand()
                        .addLogo(mastheadLogo("#HomePage")
                            .add(title(1, lg).text("Crysknife")))))
                .addContent(mastheadContent()))
            .addSidebar(sidebar)
            .addMain(main));

        initToast();
    }

    private void initToast() {
        toast.id = "snackbar";
        toast.textContent = "LuckyMe";
        DomGlobal.document.body.appendChild(toast);
    }

    private void onUserEvent(@Observes User user) {
        toast.className = "show";
        toast.textContent = "App : onEvent " + user.toString();
        DomGlobal.setTimeout(p0 -> toast.className = toast.className.replace("show", ""), 3000);
    }

    private void onAddressEvent(@Observes Address address) {
        toast.className = "show";
        toast.textContent = "App : onEvent " + address.toString();
        DomGlobal.setTimeout(p0 -> toast.className = toast.className.replace("show", ""), 3000);
    }
}
