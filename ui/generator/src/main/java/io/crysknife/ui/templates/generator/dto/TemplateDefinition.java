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

package io.crysknife.ui.templates.generator.dto;

import java.util.ArrayList;
import java.util.List;

public class TemplateDefinition {

  private final List<Attribute> attributes = new ArrayList<>();
  private final List<Element> elements = new ArrayList<>();

  private final List<Event> events = new ArrayList<>();
  private String html;
  private String rootElementPropertyName;
  private boolean initRootElement;
  private String css;


  public List<Attribute> getAttributes() {
    return attributes;
  }

  public List<Element> getElements() {
    return elements;
  }

  public List<Event> getEvents() {
    return events;
  }

  public String getHtml() {
    return html;
  }

  public void setHtml(String html) {
    this.html = html;
  }

  public boolean isInitRootElement() {
    return initRootElement;
  }

  public void setInitRootElement(boolean initRootElement) {
    this.initRootElement = initRootElement;
  }

  public String getRootElementPropertyName() {
    return rootElementPropertyName;
  }

  public void setRootElementPropertyName(String rootElementPropertyName) {
    this.rootElementPropertyName = rootElementPropertyName;
  }

  public String getCss() {
    return css;
  }

  public void setCss(String css) {
    this.css = css;
  }
}
