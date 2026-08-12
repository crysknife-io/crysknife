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
import org.patternfly.component.slider.Slider;

import static org.patternfly.component.slider.Slider.slider;
import static org.patternfly.component.slider.SliderStep.sliderStep;
import static org.patternfly.component.slider.SliderSteps.sliderSteps;

@Page(path = "SliderDemo")
@Singleton
@Templated("SliderDemo.html")
public class SliderDemo implements IsElement<HTMLDivElement> {

  @DataField
  Slider continuousSlider = slider().value(50);

  @DataField
  Slider discreteSlider = slider()
      .value(50)
      .customSteps(sliderSteps(
          sliderStep(0d),
          sliderStep(12.5, "1", true),
          sliderStep(25d, "2"),
          sliderStep(37.5, "3", true),
          sliderStep(50d, "4"),
          sliderStep(62.5, "5", true),
          sliderStep(75d, "6"),
          sliderStep(87.5, "7", true),
          sliderStep(100d, "8")));

  @DataField
  Slider rangeSlider = slider()
      .value(50)
      .range(0, 200, 50)
      .showTicks();

  @DataField
  Slider disabledSlider = slider().value(50).disabled();
}
