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

package io.crysknife.ui.templates.generator;

import java.io.IOException;
import java.io.PrintWriter;

import javax.tools.JavaFileObject;

import io.crysknife.exception.GenerationException;
import io.crysknife.generator.context.IOCContext;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 6/25/20
 */
public class TemplateWidgetGenerator {

  private final String newLine = System.lineSeparator();
  private IOCContext iocContext;
  private StringBuilder clazz;

  private static final String dom2Package = "com.google.gwt.dom";
  private static final String widget2Package = "com.google.gwt.user";
  private static final String dom3Package = "org.gwtproject.dom";
  private static final String widget3Package = "org.gwtproject.user";

  TemplateWidgetGenerator(IOCContext iocContext) {
    this.iocContext = iocContext;
  }

  TemplateWidgetGenerator build(boolean isGWT2) {
    this.clazz = new StringBuilder();
    clazz
        .append("/*\n" + " * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.\n" + " *\n"
            + " * Licensed under the Apache License, Version 2.0 (the \"License\");\n"
            + " * you may not use this file except in compliance with the License.\n"
            + " * You may obtain a copy of the License at\n" + " *\n"
            + " *       http://www.apache.org/licenses/LICENSE-2.0\n" + " *\n"
            + " * Unless required by applicable law or agreed to in writing, software\n"
            + " * distributed under the License is distributed on an \"AS IS\" BASIS,\n"
            + " * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n"
            + " * See the License for the specific language governing permissions and\n"
            + " * limitations under the License.\n" + " */\n" + "\n"
            + "package io.crysknife.templates.client;\n" + "\n" + "import java.util.Collection;\n"
            + "import java.util.Iterator;\n" + "\n")
        .append(newLine)

        .append("import ").append(isGWT2 ? dom2Package : dom3Package).append(".client.Element;\n")
        .append("import ").append(isGWT2 ? widget2Package : widget3Package)
        .append(".client.ui.Composite;\n").append("import ")
        .append(isGWT2 ? widget2Package : widget3Package).append(".client.ui.Panel;\n")
        .append("import ").append(isGWT2 ? widget2Package : widget3Package)
        .append(".client.ui.RootPanel;\n").append("import ")
        .append(isGWT2 ? widget2Package : widget3Package).append(".client.ui.Widget;\n")
        .append("import io.crysknife.ui.templates.client.annotation.Templated;\n" + "\n" + "/**\n"
            + " * Used to merge a {@link Templated} onto a {@link Composite} component.\n" + " *\n"
            + " * @author <a href=\"mailto:lincolnbaxter@gmail.com\">Lincoln Baxter, III</a>\n"
            + " */\n" + "public class TemplateWidget extends Panel {\n"
            + "    private final Collection<Widget> children;\n" + "\n"
            + "    public TemplateWidget(final Element root, final Collection<Widget> children) {\n"
            + "        this.setElement(root);\n" + "        this.children = children;\n" + "\n"
            + "        for (Widget child : children) {\n"
            + "            if (!(child instanceof TemplateWidget) && child.getParent() instanceof TemplateWidget) {\n"
            + "                child = child.getParent();\n" + "            }\n"
            + "            child.removeFromParent();\n" + "            adopt(child);\n"
            + "        }\n" + "    }\n" + "\n" + "    @Override\n"
            + "    public void onAttach() {\n" + "        super.onAttach();\n" + "    }\n" + "\n"
            + "    @Override\n" + "    public Iterator<Widget> iterator() {\n"
            + "        return children.iterator();\n" + "    }\n" + "\n" + "    @Override\n"
            + "    public boolean remove(final Widget child) {\n"
            + "        if (child.getParent() != this)\n" + "        {\n"
            + "            return false;\n" + "        }\n" + "        orphan(child);\n"
            + "        child.getElement().removeFromParent();\n"
            + "        return children.remove(child);\n" + "    }\n" + "\n"
            + "    public static void initTemplated(final Element wrapped, final Collection<Widget> dataFields) {\n"
            + "        // All template fragments are contained in a single element, during initialization.\n"
            + "        wrapped.removeFromParent();\n"
            + "        final TemplateWidget widget = new TemplateWidget(wrapped, dataFields);\n"
            + "        widget.onAttach();\n" + "        try {\n"
            + "            RootPanel.detachOnWindowClose(widget);\n"
            + "        } catch (Exception e) {\n" + "\n" + "        }\n" + "    }\n" + "\n"
            + "}\n");

    return this;
  }

  void generate() {
    JavaFileObject builderFile = null;
    try {
      builderFile = iocContext.getGenerationContext().getProcessingEnvironment().getFiler()
          .createSourceFile("io.crysknife.templates.client.TemplateWidget");
      try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
        out.append(clazz);
      }
    } catch (IOException e) {
      throw new GenerationException("Unable to generate TemplateWidget");
    }
  }
}
