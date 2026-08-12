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

import java.util.Date;

import jakarta.inject.Singleton;

import elemental2.dom.HTMLDivElement;
import io.crysknife.client.IsElement;
import io.crysknife.ui.navigation.client.annotation.Page;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.Templated;
import org.patternfly.component.timestamp.Timestamp;
import org.patternfly.component.timestamp.TimestampFormat;

import static org.patternfly.component.timestamp.Timestamp.timestamp;

@Page(path = "TimestampDemo")
@Singleton
@Templated("TimestampDemo.html")
public class TimestampDemo implements IsElement<HTMLDivElement> {

  @DataField
  Timestamp defaultTimestamp = timestamp();

  @DataField
  Timestamp fullDate = timestamp().dateFormat(TimestampFormat.full);

  @DataField
  Timestamp longDate = timestamp().dateFormat(TimestampFormat._long);

  @DataField
  Timestamp mediumDate = timestamp().dateFormat(TimestampFormat.medium);

  @DataField
  Timestamp shortDate = timestamp().dateFormat(TimestampFormat._short);

  @DataField
  Timestamp dateAndTime = timestamp()
      .dateFormat(TimestampFormat._long)
      .timeFormat(TimestampFormat._short);

  @DataField
  Timestamp utcTimestamp = timestamp()
      .dateFormat(TimestampFormat.medium)
      .timeFormat(TimestampFormat._short)
      .utc(true);
}
