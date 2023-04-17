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

import com.google.auto.common.MoreElements;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import io.crysknife.generator.api.Generator;
import io.crysknife.client.internal.AbstractEventHandler;
import io.crysknife.client.internal.event.EventManager;
import io.crysknife.definition.MethodDefinition;
import io.crysknife.exception.GenerationException;
import io.crysknife.generator.api.ClassMetaInfo;
import io.crysknife.generator.api.IOCGenerator;
import io.crysknife.generator.api.WiringElementType;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.util.StringOutputStream;
import io.crysknife.logger.TreeLogger;

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

  public void generate(ClassMetaInfo classMetaInfo, MethodDefinition methodDefinition) {
    validate(methodDefinition);

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

  private void validate(MethodDefinition methodDefinition) {
    ExecutableElement method = methodDefinition.getExecutableElement();

    if (method.getParameters().size() > 1) {
      throw new GenerationException("Method annotated with @Observes must contain only one param "
          + method.getEnclosingElement() + " " + method);
    }

    if (method.getModifiers().contains(Modifier.STATIC)) {
      throw new GenerationException("Method annotated with @Observes must be non-static "
          + method.getEnclosingElement() + " " + method);
    }

    /*    if (!MoreElements.getPackage(MoreTypes.asTypeElement(classBuilder.beanDefinition.getType()))
            .equals(MoreElements.getPackage(method.getEnclosingElement()))
            && !method.getModifiers().contains(Modifier.PUBLIC)) {
      throw new GenerationException(
              String.format("Method %s annotated with @Observes is not accessible from parent bean %s",
                      (method.getEnclosingElement().toString() + "." + method), parent.getType()));
    }*/
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
