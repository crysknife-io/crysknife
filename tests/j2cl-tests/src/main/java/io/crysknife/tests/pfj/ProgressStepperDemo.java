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
import org.patternfly.component.progressstepper.ProgressStepper;

import static org.patternfly.component.progressstepper.ProgressStep.progressStep;
import static org.patternfly.component.progressstepper.ProgressStepVariant.danger;
import static org.patternfly.component.progressstepper.ProgressStepVariant.info;
import static org.patternfly.component.progressstepper.ProgressStepVariant.pending;
import static org.patternfly.component.progressstepper.ProgressStepVariant.success;
import static org.patternfly.component.progressstepper.ProgressStepVariant.warning;
import static org.patternfly.component.progressstepper.ProgressStepper.progressStepper;

@Page(path = "ProgressStepperDemo")
@Singleton
@Templated("ProgressStepperDemo.html")
public class ProgressStepperDemo implements IsElement<HTMLDivElement> {

  @DataField
  ProgressStepper basicStepper = progressStepper()
      .ariaLabel("Basic progress stepper")
      .add(progressStep("ps-1", "First step").variant(success))
      .add(progressStep("ps-2", "Second step").variant(info))
      .add(progressStep("ps-3", "Third step").variant(pending))
      .first().next();

  @DataField
  ProgressStepper descriptionStepper = progressStepper()
      .ariaLabel("Progress stepper with descriptions")
      .add(progressStep("psd-1", "First step").variant(success)
          .description("This is the first step"))
      .add(progressStep("psd-2", "Second step").variant(info)
          .description("This is the second step"))
      .add(progressStep("psd-3", "Third step").variant(pending)
          .description("This is the third step"))
      .first().next();

  @DataField
  ProgressStepper issueStepper = progressStepper()
      .ariaLabel("Progress stepper with an issue")
      .add(progressStep("psi-1", "First step").variant(success))
      .add(progressStep("psi-2", "Second step").variant(success))
      .add(progressStep("psi-3", "Third step").variant(warning))
      .add(progressStep("psi-4", "Fourth step").variant(info))
      .add(progressStep("psi-5", "Fifth step").variant(pending))
      .last().previous();

  @DataField
  ProgressStepper failureStepper = progressStepper()
      .ariaLabel("Progress stepper with a failure")
      .add(progressStep("psf-1", "First step").variant(success))
      .add(progressStep("psf-2", "Second step").variant(success))
      .add(progressStep("psf-3", "Third step").variant(success))
      .add(progressStep("psf-4", "Fourth step").variant(danger))
      .add(progressStep("psf-5", "Fifth step").variant(pending))
      .last().previous();
}
