/*
 * Copyright © 2021 Treblereel
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

package io.crysknife.ui.templates.generator.events;

import java.util.Optional;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import elemental2.dom.Event;
import elemental2.dom.HTMLElement;
import io.crysknife.client.IsElement;
import io.crysknife.client.Reflect;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.generator.api.ClassMetaInfo;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.ui.templates.client.TemplateUtil;
import io.crysknife.ui.templates.client.annotation.SinkNative;
import io.crysknife.ui.templates.generator.TemplateGenerator;
import io.crysknife.ui.templates.generator.TemplatedGeneratorUtils;
import io.crysknife.util.GenerationUtils;
import io.crysknife.util.TypeUtils;
import jsinterop.base.Js;
import org.jboss.gwt.elemento.processor.context.DataElementInfo;
import org.jboss.gwt.elemento.processor.context.EventHandlerInfo;
import org.jboss.gwt.elemento.processor.context.TemplateContext;
import org.treblereel.j2cl.processors.utils.J2CLUtils;

public class EventHandlerGenerator {

  private final IOCContext iocContext;
  private final TemplateGenerator templatedGenerator;
  private final TypeElement elemental2Event;
  private GenerationUtils generationUtils;

  private final J2CLUtils j2CLUtils;
  private final SinkNativeGenerator sinkNativeGenerator;
  private final Elemental2Generator elemental2Generator;


  public EventHandlerGenerator(IOCContext iocContext, TemplateGenerator templatedGenerator) {
    this.iocContext = iocContext;
    this.templatedGenerator = templatedGenerator;
    this.generationUtils = new GenerationUtils(iocContext);

    this.elemental2Event = iocContext.getGenerationContext().getElements()
        .getTypeElement(Event.class.getCanonicalName());

    this.sinkNativeGenerator = new SinkNativeGenerator(iocContext);
    this.elemental2Generator = new Elemental2Generator(iocContext);
    this.j2CLUtils = new J2CLUtils(iocContext.getGenerationContext().getProcessingEnvironment());
  }

  public void generate(ClassMetaInfo builder, StringBuffer body, BeanDefinition beanDefinition,
      TemplateContext templateContext) {
    templateContext.getEvents().forEach(event -> getGenerator(event).ifPresent(
        generator -> generator.generate(builder, body, templateContext, event, beanDefinition)));
  }

  Optional<Generator> getGenerator(EventHandlerInfo eventHandlerInfo) {
    if (MoreElements.isAnnotationPresent(eventHandlerInfo.getMethod(), SinkNative.class)) {
      return Optional.of(sinkNativeGenerator);
    }
    return Optional.of(elemental2Generator);
  }


  private abstract class Generator {

    protected final IOCContext iocContext;

    Generator(IOCContext iocContext) {
      this.iocContext = iocContext;
    }

    abstract void generate(ClassMetaInfo builder, StringBuffer body,
        TemplateContext templateContext, EventHandlerInfo eventHandlerInfo,
        BeanDefinition beanDefinition);
  }

  private class SinkNativeGenerator extends Generator {

    SinkNativeGenerator(IOCContext iocContext) {
      super(iocContext);
    }

    @Override
    public void generate(ClassMetaInfo builder, StringBuffer body, TemplateContext templateContext,
        EventHandlerInfo eventHandlerInfo, BeanDefinition beanDefinition) {
      MethodCallExpr fieldAccessCallExpr =
          getFieldAccessCallExpr(eventHandlerInfo.getInfo().getName(), beanDefinition);

      Statement methodCall = generationUtils.generateMethodCall(beanDefinition.getType(),
          eventHandlerInfo.getMethod(), new NameExpr("e"));

      MethodCallExpr theCall =
          new MethodCallExpr(new NameExpr(TemplateUtil.class.getCanonicalName()), "onSinkEvents");
      theCall.addArgument(fieldAccessCallExpr);
      theCall.addArgument(
          String.valueOf(eventHandlerInfo.getMethod().getAnnotation(SinkNative.class).value()));
      theCall.addArgument("e -> { " + methodCall + " }");
      body.append(theCall);
    }
  }

  private class Elemental2Generator extends Generator {
    private TemplatedGeneratorUtils templatedGeneratorUtils;
    private GenerationUtils generationUtils;

    Elemental2Generator(IOCContext iocContext) {
      super(iocContext);
      this.generationUtils = new GenerationUtils(iocContext);
      templatedGeneratorUtils = new TemplatedGeneratorUtils(iocContext);

    }

    @Override
    public void generate(ClassMetaInfo builder, StringBuffer body, TemplateContext templateContext,
        EventHandlerInfo eventHandlerInfo, BeanDefinition beanDefinition) {
      for (String event : eventHandlerInfo.getEvents()) {

        if (event == null) {
          templatedGenerator.abortWithError(eventHandlerInfo.getMethod(),
              "It's not possible to determine event type, maybe @SinkNative or @ForEvent missed ?");
        }

        Statement theCall = generationUtils.generateMethodCall(beanDefinition.getType(),
            eventHandlerInfo.getMethod(), new NameExpr("e"));

        // handle event, that binds to the root of the template
        if (eventHandlerInfo.getInfo() == null) {
          DataElementInfo.Kind kind =
              templatedGeneratorUtils.getDataElementInfoKind(templateContext.getDataElementType());
          Expression fieldAccessCallExpr = templatedGeneratorUtils.getInstanceMethodName(kind);
          MethodCallExpr methodCallExpr =
              new MethodCallExpr(fieldAccessCallExpr, "addEventListener")
                  .addArgument(new StringLiteralExpr(event))
                  .addArgument("e -> { " + theCall + " }");
          body.append(methodCallExpr.toString());
        } else {
          Expression fieldAccessCallExpr =
              getFieldAccessCallExpr(eventHandlerInfo.getInfo().getName(), beanDefinition);
          MethodCallExpr methodCallExpr = new MethodCallExpr(
              getInstanceByElementKind(eventHandlerInfo.getInfo(), fieldAccessCallExpr),
              "addEventListener").addArgument(new StringLiteralExpr(event))
                  .addArgument("e -> { " + theCall + " }");
          body.append(methodCallExpr.toString() + ";\n");
        }
      }
    }
  }

  private boolean isElemental2Event(DeclaredType declaredType) {
    return iocContext.getGenerationContext().getTypes().isSubtype(declaredType,
        elemental2Event.asType());
  }

  public Expression getInstanceByElementKind(DataElementInfo element, Expression instance) {
    return getInstanceByElementKind(element.getKind(), element.getType(), instance);
  }

  // TODO this method must be refactored
  public Expression getInstanceByElementKind(DataElementInfo.Kind kind, TypeMirror element,
      Expression instance) {
    if (kind.equals(DataElementInfo.Kind.IsElement)) {
      instance = new MethodCallExpr(
          new EnclosedExpr(new CastExpr(
              new ClassOrInterfaceType().setName(IsElement.class.getCanonicalName()), instance)),
          "getElement");
    }

    return new EnclosedExpr(new CastExpr(
        new ClassOrInterfaceType().setName(HTMLElement.class.getCanonicalName()), instance));
  }

  public MethodCallExpr getFieldAccessCallExpr(String fieldName, BeanDefinition beanDefinition) {
    VariableElement field = getVariableElement(fieldName, beanDefinition);
    return getFieldAccessCallExpr(field, beanDefinition);
  }

  public MethodCallExpr getFieldAccessCallExpr(VariableElement field,
      BeanDefinition beanDefinition) {
    String mangleName = j2CLUtils.createFieldDescriptor(field).getMangledName();

    return new MethodCallExpr(
        new MethodCallExpr(new NameExpr(Js.class.getSimpleName()), "asPropertyMap")
            .addArgument("instance"),
        "get").addArgument(
            new MethodCallExpr(new NameExpr(Reflect.class.getSimpleName()), "objectProperty")
                .addArgument(new StringLiteralExpr(mangleName)).addArgument("instance"));
  }

  public VariableElement getVariableElement(String elementName, BeanDefinition beanDefinition) {
    return TypeUtils
        .getAllFieldsIn(iocContext.getGenerationContext().getElements(),
            MoreTypes.asTypeElement(beanDefinition.getType()))
        .stream().filter(elm -> elm.getSimpleName().toString().equals(elementName))
        .map(elm -> MoreElements.asVariable(elm)).findFirst()
        .orElseThrow(() -> new Error("Unable to find @DataField " + elementName + " in "
            + MoreTypes.asTypeElement(beanDefinition.getType()).getQualifiedName()));
  }

}
