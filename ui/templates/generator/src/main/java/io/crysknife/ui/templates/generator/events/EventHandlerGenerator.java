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

import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.Statement;
import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import elemental2.dom.Event;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.ui.templates.client.TemplateUtil;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.SinkNative;
import io.crysknife.ui.templates.generator.TemplatedGenerator;
import io.crysknife.ui.templates.generator.TemplatedGeneratorUtils;
import io.crysknife.util.GenerationUtils;
import io.crysknife.util.Utils;
import org.jboss.gwt.elemento.processor.context.DataElementInfo;
import org.jboss.gwt.elemento.processor.context.EventHandlerInfo;
import org.jboss.gwt.elemento.processor.context.TemplateContext;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.Optional;

public class EventHandlerGenerator {

  private final IOCContext iocContext;
  private final TemplatedGenerator templatedGenerator;
  private final TypeElement gwt3Event;
  private final TypeElement gwt3SharedEvent;
  private final TypeElement gwt3DomEvent;
  private final TypeElement elemental2Event;
  private final TypeElement gwt3Widget;
  private final GWT3DomEventGenerator gwt3DomEventGenerator;
  private final SinkNativeGenerator sinkNativeGenerator;
  private final Elemental2Generator elemental2Generator;
  private GenerationUtils generationUtils;

  public EventHandlerGenerator(IOCContext iocContext, TemplatedGenerator templatedGenerator) {
    this.iocContext = iocContext;
    this.templatedGenerator = templatedGenerator;
    this.generationUtils = new GenerationUtils(iocContext);

    gwt3Event = iocContext.getGenerationContext().getElements()
        .getTypeElement("org.gwtproject.user.client.Event");
    gwt3SharedEvent = iocContext.getGenerationContext().getElements()
        .getTypeElement("org.gwtproject.event.shared.Event");
    gwt3DomEvent = iocContext.getGenerationContext().getElements()
        .getTypeElement("org.gwtproject.event.dom.client.DomEvent");
    elemental2Event = iocContext.getGenerationContext().getElements()
        .getTypeElement(Event.class.getCanonicalName());
    gwt3Widget = iocContext.getGenerationContext().getElements()
        .getTypeElement("org.gwtproject.user.client.ui.Widget");

    gwt3DomEventGenerator = new GWT3DomEventGenerator(iocContext, templatedGenerator);
    sinkNativeGenerator = new SinkNativeGenerator(iocContext, templatedGenerator);
    elemental2Generator = new Elemental2Generator(iocContext, templatedGenerator);
  }

  public void generate(ClassBuilder builder, TemplateContext templateContext) {
    templateContext.getEvents().forEach(event -> getGenerator(event)
        .ifPresent(generator -> generator.generate(builder, templateContext, event)));
  }

  Optional<Generator> getGenerator(EventHandlerInfo eventHandlerInfo) {
    if (MoreElements.isAnnotationPresent(eventHandlerInfo.getMethod(), SinkNative.class)) {
      return Optional.of(sinkNativeGenerator);
    }

    if (isGWT3DomEvent(eventHandlerInfo.getMethod().getParameters().get(0))) {
      return Optional.of(gwt3DomEventGenerator);
    }

    return Optional.of(elemental2Generator);
  }


  private abstract class Generator {

    protected final IOCContext iocContext;
    protected final TemplatedGenerator templatedGenerator;

    Generator(IOCContext iocContext, TemplatedGenerator templatedGenerator) {
      this.iocContext = iocContext;
      this.templatedGenerator = templatedGenerator;
    }

    abstract void generate(ClassBuilder builder, TemplateContext templateContext,
        EventHandlerInfo eventHandlerInfo);
  }

  private class GWT3DomEventGenerator extends Generator {
    private TemplatedGeneratorUtils templatedGeneratorUtils;

    GWT3DomEventGenerator(IOCContext iocContext, TemplatedGenerator templatedGenerator) {
      super(iocContext, templatedGenerator);
      templatedGeneratorUtils = new TemplatedGeneratorUtils(iocContext);

    }

    @Override
    void generate(ClassBuilder builder, TemplateContext templateContext,
        EventHandlerInfo eventHandlerInfo) {

      Expression fieldAccessCallExpr;

      if (eventHandlerInfo.getInfo() == null) {
        DataElementInfo.Kind kind =
            templatedGeneratorUtils.getDataElementInfoKind(templateContext.getDataElementType());
        if (!kind.equals(DataElementInfo.Kind.IsWidget)) {
          templatedGenerator.abortWithError(eventHandlerInfo.getMethod(),
              "It's not possible to bind GWT event to non-GWT template, use elemental2 instead");
        }
        fieldAccessCallExpr = new NameExpr("instance");
      } else {
        VariableElement field =
            templatedGenerator.getVariableElement(eventHandlerInfo.getInfo().getName());
        TypeMirror target = iocContext.getGenerationContext().getTypes().erasure(field.asType());

        if (gwt3Widget != null) {
          if (!iocContext.getGenerationContext().getTypes().isAssignable(target,
              gwt3Widget.asType())) {
            templatedGenerator.abortWithError(eventHandlerInfo.getMethod(),
                "It's not possible to bind GWT event to non-GWT template, use elemental2 instead");
          }
        }

        fieldAccessCallExpr =
            new EnclosedExpr(new CastExpr().setType(target.toString()).setExpression(
                templatedGenerator.getFieldAccessCallExpr(eventHandlerInfo.getInfo().getName())));
      }

      String eventName =
          MoreTypes.asTypeElement(eventHandlerInfo.getMethod().getParameters().get(0).asType())
              .getQualifiedName().toString();

      StringBuffer sb = new StringBuffer();
      sb.append("e -> ");
      sb.append("instance.");
      sb.append(eventHandlerInfo.getMethodName());
      sb.append("(e)");

      builder.getInitInstanceMethod().getBody().get().addAndGetStatement(
          new MethodCallExpr(fieldAccessCallExpr, "addDomHandler").addArgument(sb.toString())
              .addArgument(new MethodCallExpr(new NameExpr(eventName), "getType")));
    }
  }

  private class SinkNativeGenerator extends Generator {

    SinkNativeGenerator(IOCContext iocContext, TemplatedGenerator templatedGenerator) {
      super(iocContext, templatedGenerator);
    }

    @Override
    public void generate(ClassBuilder builder, TemplateContext templateContext,
        EventHandlerInfo eventHandlerInfo) {
      MethodCallExpr fieldAccessCallExpr =
          templatedGenerator.getFieldAccessCallExpr(eventHandlerInfo.getInfo().getName());

      Statement methodCall = generationUtils.generateMethodCall(builder.beanDefinition.getType(),
          eventHandlerInfo.getMethod(), new NameExpr("e"));

      MethodCallExpr theCall =
          new MethodCallExpr(new NameExpr(TemplateUtil.class.getCanonicalName()), "onSinkEvents");
      theCall.addArgument(fieldAccessCallExpr);
      theCall
          .addArgument(eventHandlerInfo.getMethod().getAnnotation(SinkNative.class).value() + "");
      theCall.addArgument("e -> { " + methodCall + " }");


      builder.getInitInstanceMethod().getBody().get().addAndGetStatement(theCall);

    }
  }

  private class Elemental2Generator extends Generator {
    private TemplatedGeneratorUtils templatedGeneratorUtils;

    Elemental2Generator(IOCContext iocContext, TemplatedGenerator templatedGenerator) {
      super(iocContext, templatedGenerator);
      templatedGeneratorUtils = new TemplatedGeneratorUtils(iocContext);

    }

    @Override
    public void generate(ClassBuilder builder, TemplateContext templateContext,
        EventHandlerInfo eventHandlerInfo) {
      for (String event : eventHandlerInfo.getEvents()) {

        if (event == null) {
          templatedGenerator.abortWithError(eventHandlerInfo.getMethod(),
              "It's not possible to determine event type, maybe @SinkNative or @ForEvent missed ?");
        }

        Statement theCall = generationUtils.generateMethodCall(builder.beanDefinition.getType(),
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
          builder.getInitInstanceMethod().getBody().get().addAndGetStatement(methodCallExpr);
        } else {
          Expression fieldAccessCallExpr =
              templatedGenerator.getFieldAccessCallExpr(eventHandlerInfo.getInfo().getName());
          MethodCallExpr methodCallExpr =
              new MethodCallExpr(templatedGenerator.getInstanceByElementKind(
                  eventHandlerInfo.getInfo(), fieldAccessCallExpr), "addEventListener")
                      .addArgument(new StringLiteralExpr(event))
                      .addArgument("e -> { " + theCall + " }");
          builder.getInitInstanceMethod().getBody().get().addAndGetStatement(methodCallExpr);
        }
      }
    }
  }

  private boolean isElemental2Event(DeclaredType declaredType) {
    return iocContext.getGenerationContext().getTypes().isSubtype(declaredType,
        elemental2Event.asType());
  }

  private boolean isGWT3Event(DeclaredType declaredType) {
    return (gwt3Event != null && iocContext.getGenerationContext().getTypes()
        .isSubtype(declaredType, gwt3Event.asType()));
  }

  private boolean isGWT3SharedEvent(VariableElement parameter) {
    return gwt3SharedEvent != null && iocContext.getGenerationContext().getTypes()
        .isSubtype(parameter.asType(), gwt3SharedEvent.asType());
  }

  private boolean isGWT3DomEvent(VariableElement parameter) {
    return gwt3DomEvent != null
        && iocContext.getGenerationContext().getTypes().isSubtype(parameter.asType(),
            iocContext.getGenerationContext().getTypes().erasure(gwt3DomEvent.asType()));
  }
}
