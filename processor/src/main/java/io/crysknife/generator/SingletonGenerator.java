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

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import io.crysknife.client.InstanceFactory;
import io.crysknife.definition.InjectableVariableDefinition;
import io.crysknife.exception.GenerationException;
import io.crysknife.generator.api.ClassMetaInfo;
import io.crysknife.generator.api.IOCGenerator;
import io.crysknife.generator.api.WiringElementType;
import io.crysknife.generator.context.ExecutionEnv;
import io.crysknife.util.StringOutputStream;
import io.crysknife.generator.helpers.PostConstructAnnotationGenerator;
import io.crysknife.generator.helpers.PreDestroyAnnotationGenerator;
import io.crysknife.util.TypeUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Singleton;

import io.crysknife.generator.api.Generator;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.logger.TreeLogger;

import javax.annotation.processing.FilerException;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/2/19
 */
@Generator(priority = 1)
public class SingletonGenerator extends IOCGenerator<BeanDefinition> {

  protected final Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);
  private PreDestroyAnnotationGenerator preDestroyAnnotation =
      new PreDestroyAnnotationGenerator(iocContext);
  private PostConstructAnnotationGenerator postConstructAnnotation =
      new PostConstructAnnotationGenerator(iocContext);

  {
    cfg.setClassForTemplateLoading(this.getClass(), "/templates/");
    cfg.setDefaultEncoding("UTF-8");
    cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    cfg.setLogTemplateExceptions(false);
    cfg.setWrapUncheckedExceptions(true);
    cfg.setFallbackOnNullLoopVariable(false);
  }

  public SingletonGenerator(TreeLogger treeLogger, IOCContext iocContext) {
    super(treeLogger, iocContext);
  }

  @Override
  public void register() {
    iocContext.register(jakarta.ejb.Singleton.class, WiringElementType.BEAN, this);
    iocContext.register(Singleton.class, WiringElementType.BEAN, this);
    iocContext.register(ApplicationScoped.class, WiringElementType.BEAN, this);
    iocContext.register(Dependent.class, WiringElementType.BEAN, this);
  }

  @Override
  public void generate(ClassMetaInfo classMetaInfo, BeanDefinition beanDefinition) {
    Map<String, Object> root = new HashMap<>();
    List<Dep> fields = new ArrayList<>();

    root.put("jre", iocContext.getGenerationContext().getExecutionEnv().equals(ExecutionEnv.JRE));
    root.put("package", beanDefinition.getPackageName());
    root.put("clazz", beanDefinition.getSimpleClassName().replaceAll("\\.", "_"));
    root.put("bean", beanDefinition.getSimpleClassName());
    root.put("isDependent", TypeUtils.isDependent(beanDefinition));
    root.put("imports", classMetaInfo.getImports());
    root.put("deps", fields);
    root.put("isProxy", beanDefinition.isProxy());

    constructor(beanDefinition, root, fields);
    deps(beanDefinition, fields);

    fieldDecorators(beanDefinition, classMetaInfo);
    methodDecorators(beanDefinition, classMetaInfo);
    classDecorators(beanDefinition, classMetaInfo);

    postConstruct(beanDefinition, root);
    preDestroy(classMetaInfo, beanDefinition);

    root.put("fields", classMetaInfo.getBodyStatements());
    root.put("preDestroy", classMetaInfo.getOnDestroy());
    root.put("doInitInstance", classMetaInfo.getDoInitInstance());


    StringOutputStream os = new StringOutputStream();
    try (Writer out = new OutputStreamWriter(os, "UTF-8")) {
      Template temp = cfg.getTemplate("test.ftlh");
      temp.process(root, out);
      String fileName = TypeUtils.getQualifiedFactoryName(beanDefinition.getType());
      write(iocContext, fileName, os.toString());
    } catch (UnsupportedEncodingException | TemplateException e) {
      throw new GenerationException(e);
    } catch (IOException e) {
      throw new GenerationException(e);
    }
  }

  private void preDestroy(ClassMetaInfo classMetaInfo, BeanDefinition beanDefinition) {
    Optional<String> preDestroy = preDestroyAnnotation.generate(beanDefinition);
    preDestroy.ifPresent(preDestroyCall -> classMetaInfo.addToOnDestroy(() -> preDestroyCall));
  }

  protected void postConstruct(BeanDefinition beanDefinition, Map<String, Object> root) {
    List<String> postConstruct = new ArrayList<>();
    postConstructAnnotation.execute(postConstruct, beanDefinition);
    root.put("postConstruct", postConstruct);
  }

  protected void classDecorators(BeanDefinition beanDefinition, ClassMetaInfo classMetaInfo) {
    beanDefinition.getDecorators().stream()
        .sorted(
            Comparator.comparingInt(o -> o.getClass().getAnnotation(Generator.class).priority()))
        .forEach(g -> g.generate(classMetaInfo, beanDefinition));
  }

  protected void methodDecorators(BeanDefinition beanDefinition, ClassMetaInfo classMetaInfo) {
    beanDefinition.getMethods()
        .forEach(method -> method.getDecorators().stream()
            .sorted(Comparator
                .comparingInt(o -> o.getClass().getAnnotation(Generator.class).priority()))
            .forEach(decorator -> decorator.generate(classMetaInfo, method)));
  }

  protected void fieldDecorators(BeanDefinition beanDefinition, ClassMetaInfo classMetaInfo) {
    beanDefinition.getFields()
        .forEach(field -> field.getDecorators().forEach(g -> g.generate(classMetaInfo, field)));
  }

  protected void deps(BeanDefinition beanDefinition, List<Dep> fields) {
    beanDefinition.getFields().stream().map(field -> processField(beanDefinition, field, "field"))
        .forEach(fields::add);
  }

  protected void constructor(BeanDefinition beanDefinition, Map<String, Object> root,
      List<Dep> fields) {
    String params = null;
    if (!beanDefinition.getConstructorParams().isEmpty()) {
      params = beanDefinition.getConstructorParams().stream()
          .map(p -> "_constructor_" + p.getVariableElement().getSimpleName().toString())
          .map(f -> "this." + f + ".get().getInstance()").collect(Collectors.joining(","));

      beanDefinition.getConstructorParams().stream()
          .map(field -> processField(beanDefinition, field, "constructor")).forEach(fields::add);

    }
    root.put("constructorParams", params);
  }

  protected Dep processField(BeanDefinition beanDefinition, InjectableVariableDefinition fieldPoint,
      String kind) {
    Dep dependency = new Dep();
    String varName = "_" + kind + "_" + fieldPoint.getVariableElement().getSimpleName().toString();
    dependency.fieldName = varName;
    String typeQualifiedName = generationUtils.getActualQualifiedBeanName(fieldPoint);

    Expression expression = generateFactoryFieldDeclaration2(fieldPoint);
    dependency.call = expression.toString();
    dependency.fqdn = typeQualifiedName;
    return dependency;
  }

  protected LambdaExpr generateFactoryFieldDeclaration2(InjectableVariableDefinition fieldPoint) {
    String typeQualifiedName = generationUtils.getActualQualifiedBeanName(fieldPoint);
    ClassOrInterfaceType supplier =
        new ClassOrInterfaceType().setName(Supplier.class.getSimpleName());

    ClassOrInterfaceType type = new ClassOrInterfaceType();
    type.setName(InstanceFactory.class.getSimpleName());
    type.setTypeArguments(new ClassOrInterfaceType().setName(typeQualifiedName));
    supplier.setTypeArguments(type);

    String beanCall;
    if (fieldPoint.getImplementation().isPresent()
        && fieldPoint.getImplementation().get().getIocGenerator().isPresent()) {
      beanCall = fieldPoint.getImplementation().get().getIocGenerator().get()
          .generateBeanLookupCall(fieldPoint);
    } else if (fieldPoint.getGenerator().isPresent()) {
      beanCall = fieldPoint.getGenerator().get().generateBeanLookupCall(fieldPoint);
    } else {
      beanCall = generateBeanLookupCall(fieldPoint);
    }

    // Expression beanCall = fieldPoint.generate(iocContext);

    if (beanCall == null) {
      throw new GenerationException("No bean call for " + fieldPoint.getVariableElement().asType());
    }

    LambdaExpr lambda = new LambdaExpr().setEnclosingParameters(true);
    lambda.setBody(new ExpressionStmt(new NameExpr(beanCall)));
    return lambda;
  }

  public String generateBeanLookupCall(InjectableVariableDefinition fieldPoint) {
    String typeQualifiedName = generationUtils.getActualQualifiedBeanName(fieldPoint);
    return new MethodCallExpr(new NameExpr("beanManager"), "lookupBean")
        .addArgument(new FieldAccessExpr(new NameExpr(typeQualifiedName), "class")).toString();
  }

  public static class Dep {
    public String fieldName = "";
    public String fqdn = "";
    public String call;


    public String getFieldName() {
      return fieldName;
    }

    public String getFqdn() {
      return fqdn;
    }

    public String getCall() {
      return call;
    }
  }

  protected void write(IOCContext iocContext, String fileName, String source) throws IOException {

    try {
      JavaFileObject sourceFile = iocContext.getGenerationContext().getProcessingEnvironment()
          .getFiler().createSourceFile(fileName);
      try (Writer writer = sourceFile.openWriter()) {
        writer.write(source);
      }
    } catch (FilerException e) {
    }
  }

}
