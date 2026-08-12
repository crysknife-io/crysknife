/*
 * Copyright © 2023 Treblereel
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

package io.crysknife.tests.pfj;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

import elemental2.dom.HTMLElement;
import io.crysknife.annotation.Application;
import io.crysknife.ui.navigation.client.Navigation;
import org.patternfly.component.page.PageMain;
import org.treblereel.j2cl.processors.annotations.GWT3EntryPoint;

import static org.jboss.elemento.Elements.body;
import static org.patternfly.component.navigation.Navigation.navigation;
import static org.patternfly.component.navigation.NavigationItem.navigationItem;
import static org.patternfly.component.navigation.NavigationType.Vertical.flat;
import static org.patternfly.component.page.Masthead.masthead;
import static org.patternfly.component.page.MastheadBrand.mastheadBrand;
import static org.patternfly.component.page.MastheadContent.mastheadContent;
import static org.patternfly.component.page.MastheadMain.mastheadMain;
import static org.patternfly.component.page.MastheadToggle.mastheadToggle;
import static org.patternfly.component.page.Page.page;
import static org.patternfly.component.page.PageMain.pageMain;
import static org.patternfly.component.page.PageSidebar.pageSidebar;
import static org.patternfly.component.page.PageSidebarBody.pageSidebarBody;
import static org.patternfly.component.title.Title.title;
import static org.patternfly.style.Size.xl;

@Application
public class App {

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

    body().add(page()
        .addMasthead(masthead()
            .addMain(mastheadMain()
                .addToggle(mastheadToggle().toggleSidebar())
                .addBrand(mastheadBrand()))
            .addContent(mastheadContent()
                .add(title(1, xl).text("Crysknife + PatternFly Java"))))
        .addSidebar(pageSidebar()
            .addBody(pageSidebarBody()
                .addNavigation(navigation(flat)
                    .addItem(navigationItem("accordion", "Accordion", "#AccordionDemo"))
                    .addItem(navigationItem("action-list", "Action list", "#ActionListDemo"))
                    .addItem(navigationItem("alert", "Alert", "#AlertDemo"))
                    .addItem(navigationItem("avatar", "Avatar", "#AvatarDemo"))
                    .addItem(navigationItem("back-to-top", "Back to top", "#BackToTopDemo"))
                    .addItem(navigationItem("badge", "Badge", "#BadgeDemo"))
                    .addItem(navigationItem("banner", "Banner", "#BannerDemo"))
                    .addItem(navigationItem("brand", "Brand", "#BrandDemo"))
                    .addItem(navigationItem("breadcrumb", "Breadcrumb", "#BreadcrumbDemo"))
                    .addItem(navigationItem("button", "Button", "#"))
                    .addItem(navigationItem("card", "Card", "#CardDemo"))
                    .addItem(navigationItem("checkbox", "Checkbox", "#CheckboxDemo"))
                    .addItem(navigationItem("code-block", "Code block", "#CodeBlockDemo"))
                    .addItem(navigationItem("content", "Content", "#ContentDemo"))
                    .addItem(navigationItem("dashboard", "Dashboard", "#DashboardDemo"))
                    .addItem(navigationItem("data-list", "Data list", "#DataListDemo"))
                    .addItem(navigationItem("description-list", "Description list", "#DescriptionListDemo"))
                    .addItem(navigationItem("divider", "Divider", "#DividerDemo"))
                    .addItem(navigationItem("drawer", "Drawer", "#DrawerDemo"))
                    .addItem(navigationItem("dropdown", "Dropdown", "#DropdownDemo"))
                    .addItem(navigationItem("empty-state", "Empty state", "#EmptyStateDemo"))
                    .addItem(navigationItem("expandable-section", "Expandable section", "#ExpandableSectionDemo"))
                    .addItem(navigationItem("form", "Form", "#FormDemo"))
                    .addItem(navigationItem("helper-text", "Helper text", "#HelperTextDemo"))
                    .addItem(navigationItem("hint", "Hint", "#HintDemo"))
                    .addItem(navigationItem("icon", "Icon", "#IconDemo"))
                    .addItem(navigationItem("input-group", "Input group", "#InputGroupDemo"))
                    .addItem(navigationItem("jump-links", "Jump links", "#JumpLinksDemo"))
                    .addItem(navigationItem("label", "Label", "#LabelDemo"))
                    .addItem(navigationItem("list", "List", "#ListDemo"))
                    .addItem(navigationItem("modal", "Modal", "#ModalDemo"))
                    .addItem(navigationItem("notification-badge", "Notification badge", "#NotificationBadgeDemo"))
                    .addItem(navigationItem("panel", "Panel", "#PanelDemo"))
                    .addItem(navigationItem("popover", "Popover", "#PopoverDemo"))
                    .addItem(navigationItem("progress", "Progress", "#ProgressDemo"))
                    .addItem(navigationItem("progress-stepper", "Progress stepper", "#ProgressStepperDemo"))
                    .addItem(navigationItem("radio", "Radio", "#RadioDemo"))
                    .addItem(navigationItem("simple-list", "Simple list", "#SimpleListDemo"))
                    .addItem(navigationItem("single-select", "Single select", "#SingleSelectDemo"))
                    .addItem(navigationItem("skeleton", "Skeleton", "#SkeletonDemo"))
                    .addItem(navigationItem("slider", "Slider", "#SliderDemo"))
                    .addItem(navigationItem("spinner", "Spinner", "#SpinnerDemo"))
                    .addItem(navigationItem("switch", "Switch", "#SwitchDemo"))
                    .addItem(navigationItem("table", "Table", "#TableDemo"))
                    .addItem(navigationItem("tabs", "Tabs", "#TabsDemo"))
                    .addItem(navigationItem("text-area", "Text area", "#TextAreaDemo"))
                    .addItem(navigationItem("text-input", "Text input", "#TextInputDemo"))
                    .addItem(navigationItem("text-input-group", "Text input group", "#TextInputGroupDemo"))
                    .addItem(navigationItem("timestamp", "Timestamp", "#TimestampDemo"))
                    .addItem(navigationItem("title", "Title", "#TitleDemo"))
                    .addItem(navigationItem("toggle-group", "Toggle group", "#ToggleGroupDemo"))
                    .addItem(navigationItem("toolbar", "Toolbar", "#ToolbarDemo"))
                    .addItem(navigationItem("tooltip", "Tooltip", "#TooltipDemo"))
                    .addItem(navigationItem("tree-view", "Tree view", "#TreeViewDemo"))
                    .addItem(navigationItem("truncate", "Truncate", "#TruncateDemo"))
                    .addItem(navigationItem("translation", "Translation", "#TranslationDemo")))))
        .addMain(main));
  }
}
