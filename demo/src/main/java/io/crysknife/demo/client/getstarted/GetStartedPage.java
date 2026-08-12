/*
 * Copyright © 2025 Treblereel
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
package io.crysknife.demo.client.getstarted;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.MouseEvent;
import elemental2.dom.URL;
import io.crysknife.client.IsElement;
import io.crysknife.ui.navigation.client.annotation.Page;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;

@Singleton
@Page(path = "GetStarted")
@Templated("getstartedpage.html")
public class GetStartedPage implements IsElement<HTMLDivElement> {

  private final StarterBundleImpl bundle = StarterBundleImpl.INSTANCE;

  @Inject
  @DataField
  HTMLDivElement root;

  @Inject
  @DataField
  HTMLButtonElement downloadSingle;

  @Inject
  @DataField
  HTMLButtonElement downloadMulti;

  @Override
  public HTMLDivElement getElement() {
    return root;
  }

  @EventHandler("downloadSingle")
  public void onDownloadSingle(@ForEvent("click") final MouseEvent e) {
    downloadFile(bundle.singleModuleStarter().asString(),
        "crysknife-single-module-starter.zip");
  }

  @EventHandler("downloadMulti")
  public void onDownloadMulti(@ForEvent("click") final MouseEvent e) {
    downloadFile(bundle.multiModuleStarter().asString(),
        "crysknife-multi-module-starter.zip");
  }

  private void downloadFile(String dataUri, String filename) {
    DomGlobal.fetch(dataUri)
        .then(response -> response.blob())
        .then(blob -> {
          String url = URL.createObjectURL(blob);
          HTMLAnchorElement a =
              (HTMLAnchorElement) DomGlobal.document.createElement("a");
          a.href = url;
          a.download = filename;
          DomGlobal.document.body.appendChild(a);
          a.click();
          a.remove();
          URL.revokeObjectURL(url);
          return null;
        });
  }
}
