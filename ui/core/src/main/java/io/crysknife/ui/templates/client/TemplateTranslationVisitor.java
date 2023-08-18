/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
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

package io.crysknife.ui.templates.client;

import elemental2.dom.Element;

import java.util.function.Function;

/**
 * Visits the DOM and translates i18n text.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class TemplateTranslationVisitor extends TemplateVisitor {

  private final Function<String, String> translationService;

  public TemplateTranslationVisitor(String i18nPrefix,
      Function<String, String> translationService) {
    super(i18nPrefix);
    this.translationService = translationService;
  }

  /**
   * Translate the text in this element if there is i18n text.
   * 
   * @param i18nKeyPrefix The template prefix for the i18n key
   * @param element The element to be translated
   */
  @Override
  protected void visitElement(String i18nKeyPrefix, Element element) {
    String translationKey = i18nKeyPrefix + getOrGenerateTranslationKey(element);
    String translationValue = getI18nValue(translationKey);
    if (translationValue != null)
      element.innerHTML = translationValue;
  }

  /**
   * Translate the text value in an attribute of this element if there is i18n text.
   * 
   * @param i18nKeyPrefix The template prefix for the i18n key
   * @param element The element containing the attribute to be translated
   * @param attributeName The name of the attribute to be translated
   */
  @Override
  protected void visitAttribute(String i18nKeyPrefix, Element element, String attributeName) {
    String translationKey = i18nKeyPrefix + getElementKey(element);
    translationKey += "-" + attributeName;
    String translationValue = getI18nValue(translationKey);
    if (translationValue != null)
      element.setAttribute(attributeName, translationValue);
  }

  private String getI18nValue(String translationKey) {
    return translationService.apply(translationKey);
  }
}
