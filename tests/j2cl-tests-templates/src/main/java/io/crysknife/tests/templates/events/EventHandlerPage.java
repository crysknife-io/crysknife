/*
 * Copyright © 2026 Treblereel
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
package io.crysknife.tests.templates.events;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.MouseEvent;
import io.crysknife.client.IsElement;
import io.crysknife.tests.templates.TestLogger;
import io.crysknife.ui.navigation.client.annotation.Page;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;

@Singleton
@Page(path = "EventHandlerPage")
@Templated
public class EventHandlerPage implements IsElement<HTMLDivElement> {

  @Inject
  @DataField
  HTMLDivElement root;

  @Inject
  @DataField
  HTMLButtonElement singleBtn;

  @Inject
  @DataField
  HTMLButtonElement btnA;

  @Inject
  @DataField
  HTMLButtonElement btnB;

  @Inject
  @DataField
  HTMLButtonElement multiEventBtn;

  @Inject
  @DataField
  @Named("span")
  HTMLElement eventResult;

  @Override
  public HTMLDivElement getElement() {
    return root;
  }

  @EventHandler("singleBtn")
  public void onSingleClick(@ForEvent("click") MouseEvent e) {
    eventResult.textContent = "single-click";
    TestLogger.log("[EventHandlerPage] single-click");
  }

  @EventHandler({"btnA", "btnB"})
  public void onMultiTarget(@ForEvent("click") MouseEvent e) {
    String source = ((elemental2.dom.HTMLElement) e.currentTarget).id;
    eventResult.textContent = "multi-target:" + source;
    TestLogger.log("[EventHandlerPage] multi-target:" + source);
  }

  @EventHandler("multiEventBtn")
  public void onMultiEvent(@ForEvent({"mousedown", "mouseup"}) MouseEvent e) {
    eventResult.textContent = "multi-event:" + e.type;
    TestLogger.log("[EventHandlerPage] multi-event:" + e.type);
  }

  @EventHandler
  public void onRootClick(@ForEvent("click") MouseEvent e) {
    TestLogger.log("[EventHandlerPage] root-click");
  }
}
