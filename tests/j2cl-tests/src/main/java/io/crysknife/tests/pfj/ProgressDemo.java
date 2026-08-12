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
import org.patternfly.component.progress.Progress;

import static org.patternfly.component.progress.MeasureLocation.inside;
import static org.patternfly.component.progress.MeasureLocation.none;
import static org.patternfly.component.progress.MeasureLocation.outside;
import static org.patternfly.component.progress.Progress.progress;
import static org.patternfly.style.Size.sm;
import static org.patternfly.style.Status.danger;
import static org.patternfly.style.Status.success;
import static org.patternfly.style.Status.warning;

@Page(path = "ProgressDemo")
@Singleton
@Templated("ProgressDemo.html")
public class ProgressDemo implements IsElement<HTMLDivElement> {

  @DataField
  Progress basicProgress = progress().title("Basic").value(33);

  @DataField
  Progress smallProgress = progress().size(sm).title("Small").value(50);

  @DataField
  Progress outsideProgress = progress().measureLocation(outside).title("Outside").value(66);

  @DataField
  Progress insideProgress = progress().measureLocation(inside).title("Inside").value(75);

  @DataField
  Progress successProgress = progress().status(success).title("Success").value(100);

  @DataField
  Progress warningProgress = progress().status(warning).title("Warning").value(90);

  @DataField
  Progress dangerProgress = progress().status(danger).title("Failure").value(33);

  @DataField
  Progress noMeasure = progress().measureLocation(none).title("Without measure").value(50);
}
