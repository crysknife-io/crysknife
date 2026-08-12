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
import org.patternfly.component.skeleton.Skeleton;

import static org.patternfly.component.skeleton.Shape.circle;
import static org.patternfly.component.skeleton.Shape.square;
import static org.patternfly.component.skeleton.Skeleton.skeleton;
import static org.patternfly.style.Size.lg;
import static org.patternfly.style.Size.sm;
import static org.patternfly.style.Size.xl;

@Page(path = "SkeletonDemo")
@Singleton
@Templated("SkeletonDemo.html")
public class SkeletonDemo implements IsElement<HTMLDivElement> {

  @DataField
  Skeleton defaultSkeleton = skeleton().screenReaderText("Loading contents");

  @DataField
  Skeleton width25 = skeleton().width("25%").screenReaderText("Loading 25%");

  @DataField
  Skeleton width50 = skeleton().width("50%").screenReaderText("Loading 50%");

  @DataField
  Skeleton width75 = skeleton().width("75%").screenReaderText("Loading 75%");

  @DataField
  Skeleton textSm = skeleton().fontSize(sm).screenReaderText("Loading small text");

  @DataField
  Skeleton textLg = skeleton().fontSize(lg).screenReaderText("Loading large text");

  @DataField
  Skeleton textXl = skeleton().fontSize(xl).screenReaderText("Loading xl text");

  @DataField
  Skeleton circleSkeleton = skeleton().shape(circle).width("60px").screenReaderText("Loading circle");

  @DataField
  Skeleton squareSkeleton = skeleton().shape(square).width("60px").screenReaderText("Loading square");
}
