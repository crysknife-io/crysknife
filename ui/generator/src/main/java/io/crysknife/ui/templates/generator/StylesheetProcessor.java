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

package io.crysknife.ui.templates.generator;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import javax.lang.model.element.TypeElement;

import com.google.auto.common.MoreElements;
import com.google.common.base.Strings;
import com.inet.lib.less.Less;
import io.crysknife.exception.GenerationException;
import io.crysknife.generator.api.ClassMetaInfo;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.ui.common.client.injectors.StyleInjector;
import io.crysknife.ui.templates.client.annotation.Templated;
import io.crysknife.ui.templates.generator.dto.TemplateDefinition;
import org.jboss.gwt.elemento.processor.context.StyleSheet;
import org.jboss.gwt.elemento.processor.context.TemplateContext;

public class StylesheetProcessor {

    private final IOCContext iocContext;
    private final TemplatedGeneratorUtils templatedGeneratorUtils;

    StylesheetProcessor(IOCContext iocContext, TemplatedGeneratorUtils templatedGeneratorUtils) {
        this.iocContext = iocContext;
        this.templatedGeneratorUtils = templatedGeneratorUtils;
    }

    StyleSheet resolveStylesheet(TypeElement type, Templated templated) {
        if (Strings.emptyToNull(templated.stylesheet()) == null) {
            List<String> postfixes = Arrays.asList(".css", ".gss", ".less");
            for (String postfix : postfixes) {
                String beanName = type.getSimpleName().toString() + postfix;
                URL file = iocContext.getGenerationContext().getResourceOracle()
                        .findResource(MoreElements.getPackage(type), beanName);
                if (file != null) {
                    return new StyleSheet(type.getSimpleName() + postfix, file);
                }
            }
        } else {
            try {
                URL url = iocContext.getGenerationContext().getResourceOracle()
                        .findResource(MoreElements.getPackage(type), templated.stylesheet());
                if (url != null) {
                    return new StyleSheet(templated.stylesheet(), url);
                }
            } catch (IllegalArgumentException e1) {
                // fall through to error
            }
            throw new GenerationException(
                    String.format("Unable to find stylesheet defined at %s", type.getQualifiedName()));
        }

        return null;
    }

    void processStylesheet(ClassMetaInfo builder, TemplateContext templateContext,
                           TemplateDefinition templateDefinition) {
        if (templateContext.getStylesheet() == null) {
            return;
        }
        builder.addImport(StyleInjector.class);

        try {
            String content = new String(
                    templateContext.getStylesheet().getFile().openStream().readAllBytes(),
                    Charset.defaultCharset());

            if (templateContext.getStylesheet().isLess()) {
                content = Less.compile(null, content, false);
            }

            templateDefinition.setCss(templatedGeneratorUtils.escape(content));
        } catch (IOException e) {
            String kind = templateContext.getStylesheet().isLess() ? "Less" : "Css/Gss";
            throw new GenerationException(
                    "Unable to process " + kind + " " + templateContext.getStylesheet(), e);
        }
    }
}
