/*
 * Copyright © 2020 Treblereel
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

package org.jboss.gwt.elemento.processor.context;

import java.net.URL;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 6/2/19
 */
public class StyleSheet {

  private String style;
  private URL file;

  public StyleSheet(String style, URL file) {

    this.style = style;
    this.file = file;
  }

  public String getStyle() {
    return style;
  }

  public URL getFile() {
    return file;
  }

  public boolean isLess() {
    return style.endsWith(".less");
  }

  @Override
  public String toString() {
    return "StyleSheet{" + "style='" + style + '\'' + ", file=" + file + ", isLess=" + isLess()
        + '}';
  }
}
