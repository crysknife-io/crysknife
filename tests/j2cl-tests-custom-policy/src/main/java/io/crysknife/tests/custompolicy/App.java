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

package io.crysknife.tests.custompolicy;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import io.crysknife.annotation.Application;
import io.crysknife.ui.common.client.SafeHtmlUtils;
import org.treblereel.j2cl.processors.annotations.GWT3EntryPoint;

@Application
public class App {

  @GWT3EntryPoint
  public void onModuleLoad() {
    new AppBootstrap(this).initialize();

    HTMLDivElement div = (HTMLDivElement) DomGlobal.document.createElement("div");
    div.id = "trusted-types-result";
    SafeHtmlUtils.setInnerHTML(div, "<span>policy-ok</span>");
    DomGlobal.document.body.appendChild(div);
  }
}
