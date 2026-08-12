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

import jakarta.inject.Singleton;

import elemental2.dom.HTMLDivElement;
import io.crysknife.client.IsElement;
import io.crysknife.ui.navigation.client.annotation.Page;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.Templated;
import org.patternfly.component.alert.Alert;

import static org.patternfly.component.Severity.custom;
import static org.patternfly.component.Severity.danger;
import static org.patternfly.component.Severity.info;
import static org.patternfly.component.Severity.success;
import static org.patternfly.component.Severity.warning;
import static org.patternfly.component.alert.Alert.alert;

@Page(path = "AlertDemo")
@Singleton
@Templated("AlertDemo.html")
public class AlertDemo implements IsElement<HTMLDivElement> {

  @DataField
  Alert customAlert = alert(custom, "Custom alert title");

  @DataField
  Alert infoAlert = alert(info, "Info alert title")
      .addDescription("This is an info alert description.");

  @DataField
  Alert successAlert = alert(success, "Success alert title")
      .addDescription("This is a success alert description.");

  @DataField
  Alert warningAlert = alert(warning, "Warning alert title")
      .addDescription("This is a warning alert description.");

  @DataField
  Alert dangerAlert = alert(danger, "Danger alert title")
      .addDescription("This is a danger alert description.");
}
