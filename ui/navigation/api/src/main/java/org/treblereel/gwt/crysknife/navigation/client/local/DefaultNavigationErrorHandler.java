/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.treblereel.gwt.crysknife.navigation.client.local;

import elemental2.dom.DomGlobal;
import org.treblereel.gwt.crysknife.navigation.client.local.api.PageNavigationErrorHandler;

/**
 * Implements default error handling behavior for page navigation.
 *
 * @author Divya Dadlani <ddadlani@redhat.com>
 */
public class DefaultNavigationErrorHandler implements PageNavigationErrorHandler {

  private Navigation navigation;

  public DefaultNavigationErrorHandler(Navigation nav) {
    this.navigation = nav;
  }

  @Override
  public void handleInvalidPageNameError(Exception exception, String pageName) {
    if (pageName.equals("")) {
      throw new Error("Failed to initialize Default Page", exception);
    } else {
      DomGlobal.console.warn("Got invalid page name \"" + pageName + "\". Redirecting to default page.", exception);
      navigation.goTo("");
    }
  }

  @Override
  public void handleError(Exception exception, Class<? extends UniquePageRole> pageRole) {
    if (pageRole.equals(DefaultPage.class)) {
      throw new Error("Failed to initialize Default Page", exception);
    } else {
      DomGlobal.console.warn("Got invalid page role \"" + pageRole + "\". Redirecting to default page.", exception);
      navigation.goTo("");
    }
  }

  @Override
  public void handleInvalidURLError(Exception exception, String urlPath) {
    if (urlPath.equals("")) {
      throw new Error("Failed to initialize Default Page", exception);
    } else {
      DomGlobal.console.warn("Got invalid URL \"" + urlPath + "\". Redirecting to default page.", exception);
      navigation.goTo("");
    }
  }
}
