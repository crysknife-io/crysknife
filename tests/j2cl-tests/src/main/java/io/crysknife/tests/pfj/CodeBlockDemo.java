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
import org.patternfly.component.codeblock.CodeBlock;

import static org.patternfly.component.codeblock.CodeBlock.codeBlock;
import static org.patternfly.component.codeblock.CodeBlockAction.codeBlockCopyToClipboardAction;

@Page(path = "CodeBlockDemo")
@Singleton
@Templated("CodeBlockDemo.html")
public class CodeBlockDemo implements IsElement<HTMLDivElement> {

  private static final String CODE = "apiVersion: helm.openshift.io/v1beta1/\n"
      + "kind: HelmChartRepository\n"
      + "metadata:\n"
      + "  name: azure-sample-repo\n"
      + "spec:\n"
      + "  connectionConfig:\n"
      + "    url: https://raw.githubusercontent.com/Azure-Samples/helm-charts/master/docs";

  @DataField
  CodeBlock basicCodeBlock = codeBlock()
      .addAction(codeBlockCopyToClipboardAction())
      .code(CODE);

  @DataField
  CodeBlock expandableCodeBlock = codeBlock()
      .addAction(codeBlockCopyToClipboardAction())
      .truncate()
      .code(CODE);
}
