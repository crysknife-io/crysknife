/*
 * Copyright (C) 2026 treblereel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.crysknife.samples.security;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

import elemental2.core.Global;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import elemental2.dom.XMLHttpRequest;
import io.crysknife.annotation.Application;
import io.crysknife.ui.navigation.client.Navigation;
import io.crysknife.ui.security.SecurityContext;
import io.crysknife.ui.security.UserImpl;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.treblereel.j2cl.processors.annotations.GWT3EntryPoint;

@Application
public class App {

  @Inject
  private Navigation navigation;

  @Inject
  private SecurityContext securityContext;

  @GWT3EntryPoint
  public void onModuleLoad() {
    new AppBootstrap(this).initialize();
  }

  @PostConstruct
  public void init() {
    HTMLElement container =
        (HTMLElement) DomGlobal.document.getElementById("navigation-container");
    fetchUserInfo();
    navigation.setNavigationContainer(container);
  }

  private void fetchUserInfo() {
    XMLHttpRequest xhr = new XMLHttpRequest();
    xhr.open("GET", "/api/userinfo");
    xhr.onload = e -> {
      if (xhr.status == 200) {
        JsPropertyMap<Object> json =
            Js.cast(Global.JSON.parse(xhr.responseText));
        Object anonymous = json.get("anonymous");
        if (anonymous != null && Js.isTruthy(anonymous)) {
          return;
        }
        String name = (String) json.get("name");
        String[] roles = toStringArray(json.get("roles"));
        securityContext.setUser(new UserImpl(name, roles));
        HTMLElement loginLink =
            (HTMLElement) DomGlobal.document.getElementById("login-link");
        HTMLElement userInfo =
            (HTMLElement) DomGlobal.document.getElementById("user-info");
        HTMLElement userName =
            (HTMLElement) DomGlobal.document.getElementById("user-name");
        if (loginLink != null) {
          loginLink.style.display = "none";
        }
        if (userInfo != null) {
          userInfo.style.display = "";
        }
        if (userName != null) {
          userName.textContent = name;
        }
      }
    };
    xhr.send();
  }

  private static String[] toStringArray(Object jsArray) {
    if (jsArray == null) {
      return new String[0];
    }
    elemental2.core.JsArray<String> arr = Js.cast(jsArray);
    String[] result = new String[arr.length];
    for (int i = 0; i < arr.length; i++) {
      result[i] = arr.getAt(i);
    }
    return result;
  }
}
