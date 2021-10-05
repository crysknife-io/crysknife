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

package io.crysknife.ui.gwtproject.dom;

import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.auto.common.MoreTypes;
import io.crysknife.annotation.Generator;
import io.crysknife.client.internal.InstanceImpl;
import io.crysknife.definition.InjectableVariableDefinition;
import io.crysknife.exception.GenerationException;
import io.crysknife.generator.BeanIOCGenerator;
import io.crysknife.generator.WiringElementType;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.definition.Definition;
import org.gwtproject.dom.client.AnchorElement;
import org.gwtproject.dom.client.AreaElement;
import org.gwtproject.dom.client.AudioElement;
import org.gwtproject.dom.client.BRElement;
import org.gwtproject.dom.client.BaseElement;
import org.gwtproject.dom.client.ButtonElement;
import org.gwtproject.dom.client.CanvasElement;
import org.gwtproject.dom.client.DListElement;
import org.gwtproject.dom.client.DivElement;
import org.gwtproject.dom.client.Document;
import org.gwtproject.dom.client.FieldSetElement;
import org.gwtproject.dom.client.FormElement;
import org.gwtproject.dom.client.FrameElement;
import org.gwtproject.dom.client.FrameSetElement;
import org.gwtproject.dom.client.HRElement;
import org.gwtproject.dom.client.HeadElement;
import org.gwtproject.dom.client.HeadingElement;
import org.gwtproject.dom.client.IFrameElement;
import org.gwtproject.dom.client.ImageElement;
import org.gwtproject.dom.client.InputElement;
import org.gwtproject.dom.client.LIElement;
import org.gwtproject.dom.client.LabelElement;
import org.gwtproject.dom.client.LegendElement;
import org.gwtproject.dom.client.LinkElement;
import org.gwtproject.dom.client.MapElement;
import org.gwtproject.dom.client.MediaElement;
import org.gwtproject.dom.client.MetaElement;
import org.gwtproject.dom.client.OListElement;
import org.gwtproject.dom.client.ObjectElement;
import org.gwtproject.dom.client.OptGroupElement;
import org.gwtproject.dom.client.OptionElement;
import org.gwtproject.dom.client.ParagraphElement;
import org.gwtproject.dom.client.ParamElement;
import org.gwtproject.dom.client.PreElement;
import org.gwtproject.dom.client.QuoteElement;
import org.gwtproject.dom.client.ScriptElement;
import org.gwtproject.dom.client.SelectElement;
import org.gwtproject.dom.client.SourceElement;
import org.gwtproject.dom.client.SpanElement;
import org.gwtproject.dom.client.StyleElement;
import org.gwtproject.dom.client.TableCaptionElement;
import org.gwtproject.dom.client.TableCellElement;
import org.gwtproject.dom.client.TableColElement;
import org.gwtproject.dom.client.TableElement;
import org.gwtproject.dom.client.TableRowElement;
import org.gwtproject.dom.client.TableSectionElement;
import org.gwtproject.dom.client.TextAreaElement;
import org.gwtproject.dom.client.TitleElement;
import org.gwtproject.dom.client.UListElement;
import org.gwtproject.dom.client.VideoElement;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/7/19
 */
@Generator(priority = 100000)
public class GwtDomFactoryGenerator extends BeanIOCGenerator {

  private static final Map<Class, Function<InjectableVariableDefinition, MethodCallExpr>> HTML_ELEMENTS =
      new HashMap<>();

  static {
    HTML_ELEMENTS.put(AnchorElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createAnchorElement"));

    HTML_ELEMENTS.put(AreaElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createAreaElement"));

    HTML_ELEMENTS.put(AudioElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createAudioElement"));

    HTML_ELEMENTS.put(BaseElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createBaseElement"));

    HTML_ELEMENTS.put(BRElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createBRElement"));

    HTML_ELEMENTS.put(BRElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createBRElement"));

    HTML_ELEMENTS.put(ButtonElement.class, fieldPoint -> {
      if (isNamed(fieldPoint)) {
        if (getNamed(fieldPoint).toLowerCase(Locale.ROOT).equals("submit")) {
          return new MethodCallExpr(
              new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
              "createSubmitButtonElement");
        } else if (getNamed(fieldPoint).toLowerCase(Locale.ROOT).equals("reset")) {
          return new MethodCallExpr(
              new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
              "createResetButtonElement");
        }
      }
      return new MethodCallExpr(
          new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
          "createPushButtonElement");
    });

    HTML_ELEMENTS.put(CanvasElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createCanvasElement"));

    HTML_ELEMENTS.put(DivElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createDivElement"));

    HTML_ELEMENTS.put(DListElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createDLElement"));

    HTML_ELEMENTS.put(FieldSetElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createFieldSetElement"));

    HTML_ELEMENTS.put(FormElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createFormElement"));

    HTML_ELEMENTS.put(FrameElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createFrameElement"));

    HTML_ELEMENTS.put(FrameSetElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createFrameSetElement"));

    HTML_ELEMENTS.put(HeadElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createHeadElement"));

    HTML_ELEMENTS.put(HeadingElement.class, fieldPoint -> {
      if (isNamed(fieldPoint)) {
        String h = getNamed(fieldPoint);
        return new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createHElement").addArgument(h);
      } else {
        throw new GenerationException(
            HeadingElement.class.getCanonicalName() + " must be annotated with @Named");
      }
    });

    HTML_ELEMENTS.put(HRElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createHRElement"));

    HTML_ELEMENTS.put(IFrameElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createIFrameElement"));

    HTML_ELEMENTS.put(ImageElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createImageElement"));

    HTML_ELEMENTS.put(InputElement.class, fieldPoint -> {
      if (isNamed(fieldPoint)) {

        if (getNamed(fieldPoint).toLowerCase(Locale.ROOT).equals("checkbox")) {
          return new MethodCallExpr(
              new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
              "createCheckInputElement");
        } else if (getNamed(fieldPoint).toLowerCase(Locale.ROOT).equals("file")) {
          return new MethodCallExpr(
              new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
              "createFileInputElement");
        } else if (getNamed(fieldPoint).toLowerCase(Locale.ROOT).equals("hidden")) {
          return new MethodCallExpr(
              new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
              "createHiddenInputElement");
        } else if (getNamed(fieldPoint).toLowerCase(Locale.ROOT).equals("image")) {
          return new MethodCallExpr(
              new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
              "createImageInputElement");
        } else if (getNamed(fieldPoint).toLowerCase(Locale.ROOT).equals("password")) {
          return new MethodCallExpr(
              new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
              "createPasswordInputElement");
        } else if (getNamed(fieldPoint).toLowerCase(Locale.ROOT).equals("radio")) {
          return new MethodCallExpr(
              new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
              "createRadioInputElement");
        } else if (getNamed(fieldPoint).toLowerCase(Locale.ROOT).equals("reset")) {
          return new MethodCallExpr(
              new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
              "createResetInputElement");
        } else if (getNamed(fieldPoint).toLowerCase(Locale.ROOT).equals("submit")) {
          return new MethodCallExpr(
              new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
              "createSubmitInputElement");
        } else if (getNamed(fieldPoint).toLowerCase(Locale.ROOT).equals("text")) {
          return new MethodCallExpr(
              new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
              "createTextInputElement");
        }
      }
      return new MethodCallExpr(
          new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
          "createButtonInputElement");
    });

    HTML_ELEMENTS.put(LabelElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createLabelElement"));

    HTML_ELEMENTS.put(LegendElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createLegendElement"));

    HTML_ELEMENTS.put(LIElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createLIElement"));

    HTML_ELEMENTS.put(LinkElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createLinkElement"));

    HTML_ELEMENTS.put(MapElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createMapElement"));

    HTML_ELEMENTS.put(MediaElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createMetaElement"));

    HTML_ELEMENTS.put(MetaElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createMetaElement"));

    HTML_ELEMENTS.put(ObjectElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createObjectElement"));

    HTML_ELEMENTS.put(OListElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createOLElement"));

    HTML_ELEMENTS.put(OptGroupElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createOptGroupElement"));

    HTML_ELEMENTS.put(OptionElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createOptionElement"));

    HTML_ELEMENTS.put(ParagraphElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createPElement"));

    HTML_ELEMENTS.put(ParamElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createParamElement"));

    HTML_ELEMENTS.put(PreElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createPreElement"));

    HTML_ELEMENTS.put(QuoteElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createQElement"));

    HTML_ELEMENTS.put(ScriptElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createScriptElement"));

    HTML_ELEMENTS.put(SelectElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createSelectElement"));

    HTML_ELEMENTS.put(SourceElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createSourceElement"));

    HTML_ELEMENTS.put(SpanElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createSpanElement"));

    HTML_ELEMENTS.put(SpanElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createSpanElement"));

    HTML_ELEMENTS.put(StyleElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createStyleElement"));

    HTML_ELEMENTS.put(TableCaptionElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createCaptionElement"));

    HTML_ELEMENTS.put(TableCellElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createTDElement"));

    HTML_ELEMENTS.put(TableColElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createColElement"));

    HTML_ELEMENTS.put(TableElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createTableElement"));

    HTML_ELEMENTS.put(TableRowElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createTRElement"));

    HTML_ELEMENTS.put(TableSectionElement.class, fieldPoint -> {
      if (isNamed(fieldPoint)) {
        String h = getNamed(fieldPoint).toLowerCase(Locale.ROOT);
        if (h.equals("tbody")) {
          return new MethodCallExpr(
              new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
              "createTBodyElement").addArgument(h);
        } else if (h.equals("thead")) {
          return new MethodCallExpr(
              new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
              "createTHeadElement").addArgument(h);
        } else if (h.equals("tfoot")) {
          return new MethodCallExpr(
              new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
              "createTFootElement").addArgument(h);
        }
        throw new GenerationException(
            TableSectionElement.class.getCanonicalName() + " must be annotated with valid @Named");
      } else {
        throw new GenerationException(
            TableSectionElement.class.getCanonicalName() + " must be annotated with @Named");
      }
    });

    HTML_ELEMENTS.put(TextAreaElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createTextAreaElement"));

    HTML_ELEMENTS.put(TitleElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createTitleElement"));

    HTML_ELEMENTS.put(UListElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createULElement"));

    HTML_ELEMENTS.put(VideoElement.class,
        fieldPoint -> new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Document.class.getCanonicalName()), "get"),
            "createVideoElement"));
  }

  private static boolean isNamed(InjectableVariableDefinition fieldPoint) {
    return fieldPoint.getVariableElement().getAnnotation(Named.class) != null
        && !fieldPoint.getVariableElement().getAnnotation(Named.class).value().equals("");
  }

  private static String getNamed(InjectableVariableDefinition fieldPoint) {
    return fieldPoint.getVariableElement().getAnnotation(Named.class).value();
  }

  public GwtDomFactoryGenerator(IOCContext iocContext) {
    super(iocContext);
  }

  @Override
  public void register() {
    HTML_ELEMENTS.keySet().forEach(clazz -> {
      iocContext.register(Inject.class, clazz, WiringElementType.FIELD_TYPE, this);
    });
  }

  @Override
  public void generate(ClassBuilder clazz, Definition beanDefinition) {

  }

  @Override
  public Expression generateBeanLookupCall(ClassBuilder classBuilder,
      InjectableVariableDefinition fieldPoint) {
    classBuilder.getClassCompilationUnit().addImport(InstanceImpl.class);
    classBuilder.getClassCompilationUnit().addImport(Provider.class);
    classBuilder.getClassCompilationUnit().addImport(MoreTypes
        .asTypeElement(fieldPoint.getVariableElement().asType()).getQualifiedName().toString());

    Class clazz;
    try {
      clazz = Class.forName(MoreTypes.asTypeElement(fieldPoint.getVariableElement().asType())
          .getQualifiedName().toString());
    } catch (ClassNotFoundException e) {
      throw new Error(
          "Unable to process " + MoreTypes.asTypeElement(fieldPoint.getVariableElement().asType())
              .getQualifiedName().toString() + " " + e.getMessage());
    }
    return new ObjectCreationExpr().setType(InstanceImpl.class)
        .addArgument(HTML_ELEMENTS.get(clazz).apply(fieldPoint));
  }

}
