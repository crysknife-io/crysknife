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

package io.crysknife.generator;

import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.UnknownType;
import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import io.crysknife.annotation.Generator;
import io.crysknife.client.internal.AbstractEventHandler;
import io.crysknife.client.internal.event.EventManager;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.definition.Definition;
import io.crysknife.definition.MethodDefinition;
import io.crysknife.exception.GenerationException;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.api.ClassMetaInfo;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.generator.refactoring.StringOutputStream;
import io.crysknife.logger.TreeLogger;

import io.crysknife.util.Utils;
import jakarta.enterprise.event.Observes;
import jsinterop.base.Js;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/5/19
 */
@Generator(priority = 1000)
@io.crysknife.generator.refactoring.Generator(priority = 1000, annotations = Observes.class,
    elementType = WiringElementType.PARAMETER)
public class ObservesGenerator extends IOCGenerator<MethodDefinition> {

  private final Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);

  {
    cfg.setClassForTemplateLoading(this.getClass(), "/templates/observes/");
    cfg.setDefaultEncoding("UTF-8");
    cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    cfg.setLogTemplateExceptions(false);
    cfg.setWrapUncheckedExceptions(true);
    cfg.setFallbackOnNullLoopVariable(false);
  }


  public ObservesGenerator(TreeLogger logger, IOCContext iocContext) {
    super(logger, iocContext);
  }

  @Override
  public void register() {
    iocContext.register(Observes.class, WiringElementType.PARAMETER, this);
  }

  @Override
  public void generate(ClassBuilder classBuilder, MethodDefinition methodDefinition) {
    ExecutableElement method = methodDefinition.getExecutableElement();
    BeanDefinition parent = iocContext.getBean(method.getEnclosingElement().asType());

    if (method.getParameters().size() > 1) {
      throw new GenerationException("Method annotated with @Observes must contain only one param "
          + method.getEnclosingElement() + " " + method);
    }

    if (method.getModifiers().contains(Modifier.STATIC)) {
      throw new GenerationException("Method annotated with @Observes must be non-static "
          + method.getEnclosingElement() + " " + method);
    }

    if (!MoreElements.getPackage(MoreTypes.asTypeElement(classBuilder.beanDefinition.getType()))
        .equals(MoreElements.getPackage(method.getEnclosingElement()))
        && !method.getModifiers().contains(Modifier.PUBLIC)) {
      throw new GenerationException(
          String.format("Method %s annotated with @Observes is not accessible from parent bean %s",
              (method.getEnclosingElement().toString() + "." + method), parent.getType()));
    }

    classBuilder.getClassCompilationUnit().addImport(AbstractEventHandler.class);
    classBuilder.getClassCompilationUnit().addImport(BiConsumer.class);
    classBuilder.getClassCompilationUnit().addImport(EventManager.class);

    VariableElement parameter = method.getParameters().get(0);
    TypeMirror parameterTypeMirror =
        iocContext.getGenerationContext().getTypes().erasure(parameter.asType());

    String consumer = parameter.getEnclosingElement().getSimpleName().toString() + "_"
        + parameterTypeMirror.toString().replaceAll("\\.", "_") + "_"
        + MoreElements.asType(method.getEnclosingElement()).getQualifiedName().toString()
            .replaceAll("\\.", "_");


    addConsumerField(classBuilder, consumer, methodDefinition, parameter);
    addSubscriberCall(classBuilder, parameter, consumer);
    addUnSubscriberCall(classBuilder, parameter, consumer);
  }

  private void addUnSubscriberCall(ClassBuilder classBuilder, VariableElement parameter,
      String consumer) {
    TypeMirror parameterTypeMirror =
        iocContext.getGenerationContext().getTypes().erasure(parameter.asType());

    classBuilder.getOnDestroyMethod().getBody().ifPresent(body -> {
      body.addStatement(new MethodCallExpr(
          new EnclosedExpr(new CastExpr(new ClassOrInterfaceType().setName("AbstractEventHandler"),
              new MethodCallExpr(
                  new MethodCallExpr(new MethodCallExpr(new NameExpr("beanManager"), "lookupBean")
                      .addArgument("EventManager.class"), "getInstance"),
                  "get").addArgument(parameterTypeMirror.toString() + ".class"))),
          "removeSubscriber").addArgument("instance").addArgument(consumer));
    });
  }

  private void addSubscriberCall(ClassBuilder classBuilder, VariableElement parameter,
      String consumer) {
    TypeMirror parameterTypeMirror =
        iocContext.getGenerationContext().getTypes().erasure(parameter.asType());

    classBuilder.getInitInstanceMethod().getBody().ifPresent(body -> {
      body.addStatement(new MethodCallExpr(
          new EnclosedExpr(new CastExpr(new ClassOrInterfaceType().setName("AbstractEventHandler"),
              new MethodCallExpr(
                  new MethodCallExpr(new MethodCallExpr(new NameExpr("beanManager"), "lookupBean")
                      .addArgument("EventManager.class"), "getInstance"),
                  "get").addArgument(parameterTypeMirror.toString() + ".class"))),
          "addSubscriber").addArgument("instance").addArgument(consumer));
    });
  }

  private void addConsumerField(ClassBuilder classBuilder, String parameterName,
      MethodDefinition methodDefinition, VariableElement parameter) {
    ExecutableElement method = methodDefinition.getExecutableElement();
    BeanDefinition parent = iocContext.getBean(method.getEnclosingElement().asType());

    TypeMirror caller = iocContext.getTypeMirror(Observes.class);
    Statement call = generationUtils.generateMethodCall(caller, method, new NameExpr("event"));

    LambdaExpr lambda = new LambdaExpr();
    lambda.setEnclosingParameters(true);
    lambda.getParameters().add(new Parameter().setName("event").setType(new UnknownType()));
    lambda.getParameters().add(new Parameter().setName("instance").setType(new UnknownType()));
    lambda.setBody(call);

    ClassOrInterfaceType consumerClassDeclaration =
        new ClassOrInterfaceType().setName(BiConsumer.class.getCanonicalName());
    consumerClassDeclaration.setTypeArguments(
        new ClassOrInterfaceType().setName(parameter.asType().toString()),
        new ClassOrInterfaceType().setName(
            iocContext.getGenerationContext().getTypes().erasure(parent.getType()).toString()));

    classBuilder.addFieldWithInitializer(consumerClassDeclaration, parameterName, lambda,
        Keyword.PRIVATE, Keyword.FINAL);
  }


  /*  @Override()
  protected void onDestroy(org.treblereel.observes.ObservesBean instance) {
    ((AbstractEventHandler) beanManager.lookupBean(EventManager.class).getInstance().get(org.treblereel.observes.User.class)).removeSubscriber(instance, onUserEvent_org_treblereel_observes_User_org_treblereel_observes_ObservesBean);
  }
  
      public void doInitInstance(ObservesBean instance) {
        ((AbstractEventHandler) beanManager.lookupBean(EventManager.class).getInstance().get(org.treblereel.observes.User.class)).addSubscriber(instance, onUserEvent_org_treblereel_observes_User_org_treblereel_observes_ObservesBean);
    }
  
      private final java.util.function.BiConsumer<org.treblereel.observes.User, org.treblereel.observes.ObservesBean> onUserEvent_org_treblereel_observes_User_org_treblereel_observes_ObservesBean = (event, instance) -> instance.onUserEvent(jsinterop.base.Js.uncheckedCast(event));
  
  */

  @Override
  public void generate(ClassMetaInfo classMetaInfo, MethodDefinition methodDefinition) {
    classMetaInfo.addImport(AbstractEventHandler.class);
    classMetaInfo.addImport(BiConsumer.class);
    classMetaInfo.addImport(EventManager.class);
    classMetaInfo.addImport(Js.class);

    VariableElement parameter = methodDefinition.getExecutableElement().getParameters().get(0);
    String consumer = getConsumer(methodDefinition.getExecutableElement(), parameter);
    String target =
        iocContext.getGenerationContext().getTypes().erasure(parameter.asType()).toString();

    addConsumerField2(classMetaInfo, methodDefinition, target, consumer);
    addToOnDestroy(classMetaInfo, target, consumer);
    doInitInstance(classMetaInfo, target, consumer);

  }

  private void doInitInstance(ClassMetaInfo classMetaInfo, String target, String consumer) {
    Map<String, Object> root = new HashMap<>();
    root.put("target", target);
    root.put("consumer", consumer);

    StringOutputStream os = new StringOutputStream();
    try (Writer out = new OutputStreamWriter(os, "UTF-8")) {
      Template temp = cfg.getTemplate("subscribe.ftlh");
      temp.process(root, out);
      classMetaInfo.addToDoInitInstance(os::toString);
    } catch (UnsupportedEncodingException | TemplateException e) {
      throw new GenerationException(e);
    } catch (IOException e) {
      throw new GenerationException(e);
    }
  }

  private void addConsumerField2(ClassMetaInfo classMetaInfo, MethodDefinition methodDefinition,
      String target, String consumer) {
    String bean = iocContext.getGenerationContext().getTypes()
        .erasure(methodDefinition.getExecutableElement().getEnclosingElement().asType()).toString();

    Map<String, Object> root = new HashMap<>();
    root.put("target", target);
    root.put("bean", bean);
    root.put("consumer", consumer);
    root.put("method", methodDefinition.getExecutableElement().getSimpleName());

    StringOutputStream os = new StringOutputStream();
    try (Writer out = new OutputStreamWriter(os, "UTF-8")) {
      Template temp = cfg.getTemplate("consumer.ftlh");
      temp.process(root, out);
      classMetaInfo.addToBody(os::toString);
    } catch (UnsupportedEncodingException | TemplateException e) {
      throw new GenerationException(e);
    } catch (IOException e) {
      throw new GenerationException(e);
    }

  }

  private void addToOnDestroy(ClassMetaInfo classMetaInfo, String target, String consumer) {
    Map<String, Object> root = new HashMap<>();
    root.put("target", target);
    root.put("subscriber", consumer);

    StringOutputStream os = new StringOutputStream();
    try (Writer out = new OutputStreamWriter(os, "UTF-8")) {
      Template temp = cfg.getTemplate("onDestroy.ftlh");
      temp.process(root, out);
      classMetaInfo.addToOnDestroy(os::toString);
    } catch (UnsupportedEncodingException | TemplateException e) {
      throw new GenerationException(e);
    } catch (IOException e) {
      throw new GenerationException(e);
    }
  }

  private String getConsumer(ExecutableElement beanDefinition, VariableElement parameter) {
    TypeMirror parameterTypeMirror =
        iocContext.getGenerationContext().getTypes().erasure(parameter.asType());

    String consumer = parameter.getEnclosingElement().getSimpleName().toString() + "_"
        + parameterTypeMirror.toString().replaceAll("\\.", "_") + "_"
        + MoreElements.asType(beanDefinition.getEnclosingElement()).getQualifiedName().toString()
            .replaceAll("\\.", "_");
    return consumer;
  }
}
