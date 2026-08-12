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
package io.crysknife.tests.navigation;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.MouseEvent;
import io.crysknife.client.IsElement;
import io.crysknife.ui.navigation.client.TransitionTo;
import io.crysknife.ui.navigation.client.annotation.Page;
import io.crysknife.ui.navigation.client.annotation.PageShown;
import io.crysknife.ui.navigation.client.annotation.PageState;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;

@Singleton
@Page(path = "PageC")
@Templated("PageC.html")
public class PageC implements IsElement<HTMLDivElement> {

  @Inject
  @DataField
  HTMLDivElement root;

  @Inject
  @DataField
  HTMLDivElement stateDisplay;

  @Inject
  @DataField
  HTMLButtonElement backHome;

  @Inject
  TransitionTo<HomePage> toHome;

  @PageState
  int count;

  @PageState
  long bigNum;

  @PageState
  boolean active;

  @PageState
  double ratio;

  @PageState
  float score;

  @PageState
  short level;

  @PageState
  byte code;

  @PageState(defaultValue = "42")
  int countWithDefault;

  @PageState(defaultValue = "true")
  boolean flagWithDefault;

  @PageState
  Integer boxedInt;

  @PageState
  Long boxedLong;

  @PageState
  Boolean boxedBool;

  @PageState
  Double boxedDouble;

  @PageState(defaultValue = "99")
  Integer boxedIntWithDefault;

  @PageState(defaultValue = "1000000")
  long longWithDefault;

  @PageState(defaultValue = "3.14")
  double doubleWithDefault;

  @PageState(defaultValue = "1.5")
  float floatWithDefault;

  @PageState(defaultValue = "7")
  short shortWithDefault;

  @PageState(defaultValue = "3")
  byte byteWithDefault;

  @PageState(defaultValue = "500")
  Long boxedLongWithDefault;

  @PageState(defaultValue = "false")
  Boolean boxedBoolWithDefault;

  @PageState(defaultValue = "2.72")
  Double boxedDoubleWithDefault;

  @PageState
  List<String> tags;

  @PageState
  List<Integer> ids;

  @PageState
  List<Long> longList;

  @PageState
  List<Boolean> flags;

  @Override
  public HTMLDivElement getElement() {
    return root;
  }

  @EventHandler("backHome")
  public void onBackHome(@ForEvent("click") MouseEvent e) {
    toHome.go();
  }

  @PageShown
  public void onPageShown() {
    NavigationTestLogger.log("[PageC] @PageShown");
    StringBuilder sb = new StringBuilder();
    sb.append("count=").append(count);
    sb.append("|bigNum=").append(bigNum);
    sb.append("|active=").append(active);
    sb.append("|ratio=").append(ratio);
    sb.append("|score=").append(score);
    sb.append("|level=").append(level);
    sb.append("|code=").append(code);
    sb.append("|countWithDefault=").append(countWithDefault);
    sb.append("|flagWithDefault=").append(flagWithDefault);
    sb.append("|boxedInt=").append(boxedInt);
    sb.append("|boxedLong=").append(boxedLong);
    sb.append("|boxedBool=").append(boxedBool);
    sb.append("|boxedDouble=").append(boxedDouble);
    sb.append("|boxedIntWithDefault=").append(boxedIntWithDefault);
    sb.append("|longWithDefault=").append(longWithDefault);
    sb.append("|doubleWithDefault=").append(doubleWithDefault);
    sb.append("|floatWithDefault=").append(floatWithDefault);
    sb.append("|shortWithDefault=").append(shortWithDefault);
    sb.append("|byteWithDefault=").append(byteWithDefault);
    sb.append("|boxedLongWithDefault=").append(boxedLongWithDefault);
    sb.append("|boxedBoolWithDefault=").append(boxedBoolWithDefault);
    sb.append("|boxedDoubleWithDefault=").append(boxedDoubleWithDefault);
    sb.append("|tags=").append(tags);
    sb.append("|ids=").append(ids);
    sb.append("|longList=").append(longList);
    sb.append("|flags=").append(flags);
    String stateStr = sb.toString();
    NavigationTestLogger.log("[PageC] " + stateStr);
    stateDisplay.textContent = stateStr;
  }
}
