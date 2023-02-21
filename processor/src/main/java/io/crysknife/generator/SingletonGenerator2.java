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


package io.crysknife.generator;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import io.crysknife.client.InstanceFactory;
import io.crysknife.client.Reflect;
import io.crysknife.client.internal.proxy.Interceptor;
import io.crysknife.client.internal.proxy.OnFieldAccessed;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.definition.InjectableVariableDefinition;
import io.crysknife.definition.InjectionParameterDefinition;
import io.crysknife.definition.ProducesBeanDefinition;
import io.crysknife.exception.GenerationException;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.ExecutionEnv;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.generator.steps.AbstractBeanGenerator;
import io.crysknife.generator.steps.DependantField;
import io.crysknife.generator.steps.DependantFields;
import io.crysknife.generator.steps.InitClassBuilder;
import io.crysknife.generator.steps.InitInstanceMethod;
import io.crysknife.generator.steps.InstanceGetFieldDecorators;
import io.crysknife.generator.steps.InstanceGetMethod;
import io.crysknife.generator.steps.InstanceGetMethodDecorators;
import io.crysknife.generator.steps.InstanceGetMethodReturn;
import io.crysknife.generator.steps.InterceptorFieldDeclaration;
import io.crysknife.generator.steps.NewInstanceMethod;
import io.crysknife.generator.steps.PostConstructAnnotation;
import io.crysknife.generator.steps.PostConstructAnnotation2;
import io.crysknife.generator.steps.PreDestroyAnnotation;
import io.crysknife.generator.steps.PreDestroyAnnotation2;
import io.crysknife.generator.steps.SingletonInstanceGetMethod;
import io.crysknife.generator.steps.Step;
import io.crysknife.generator.steps.Write;
import io.crysknife.util.GenerationUtils;
import io.crysknife.util.Utils;

import javax.annotation.processing.FilerException;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SingletonGenerator2 extends AbstractBeanGenerator {

  private final Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);


  public SingletonGenerator2(IOCContext iocContext) {
    /*    super(new InitClassBuilder(), new DependantFields(), new InterceptorFieldDeclaration(),
        new NewInstanceMethod(), new InitInstanceMethod(), new SingletonInstanceGetMethod(),
        new InstanceGetFieldDecorators(), new InstanceGetMethodDecorators(), new DependantField(),
        new InstanceGetMethodReturn(), new PostConstructAnnotation(), new PreDestroyAnnotation(),
        new Write());*/

    cfg.setClassForTemplateLoading(this.getClass(), "/templates/");
    cfg.setDefaultEncoding("UTF-8");
    cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    cfg.setLogTemplateExceptions(false);
    cfg.setWrapUncheckedExceptions(true);
    cfg.setFallbackOnNullLoopVariable(false);
    this.generationUtils = new GenerationUtils(iocContext);


  }

  @Override
  public void register() {
    // iocContext.register(Singleton.class, WiringElementType.BEAN, this);
    // iocContext.register(ApplicationScoped.class, WiringElementType.BEAN, this);
  }

  @Override
  public void generate(IOCContext iocContext, ClassBuilder clazz, BeanDefinition beanDefinition) {

    Map<String, Object> root = new HashMap<>();

    root.put("package", beanDefinition.getPackageName());
    root.put("clazz", beanDefinition.getSimpleClassName().replaceAll("\\.", "_"));
    root.put("jre", iocContext.getGenerationContext().getExecutionEnv().equals(ExecutionEnv.JRE));
    root.put("bean", beanDefinition.getSimpleClassName());
    root.put("imports", clazz.getClassCompilationUnit().getImports().stream()
        .map(i -> i.getNameAsString()).sorted().collect(Collectors.toList()));

    root.put("isDependent", Utils.isDependent(beanDefinition));

    List<Dep> fields = new ArrayList<>();

    String params = null;
    if (!beanDefinition.getConstructorParams().isEmpty()) {
      params = beanDefinition.getConstructorParams().stream()
          .map(p -> "_constructor_" + p.getVariableElement().getSimpleName().toString())
          .map(f -> "this." + f + ".get().getInstance()").collect(Collectors.joining(","));

      beanDefinition.getConstructorParams().stream()
          .map(field -> generateFactoryFieldDeclaration(beanDefinition, field, "constructor"))
          .forEach(fields::add);

    }
    root.put("constructorParams", params);


    beanDefinition.getFields().stream()
        .map(field -> generateFactoryFieldDeclaration(beanDefinition, field, "field"))
        .forEach(fields::add);
    root.put("deps", fields);



    List<String> postConstruct = new ArrayList<>();
    PostConstructAnnotation2 postConstructAnnotation2 = new PostConstructAnnotation2(iocContext);
    postConstructAnnotation2.execute(postConstruct, beanDefinition);
    root.put("postConstruct", postConstruct);

    PreDestroyAnnotation2 preDestroyAnnotation2 = new PreDestroyAnnotation2(iocContext);
    Optional<String> preDestroy = preDestroyAnnotation2.generate(beanDefinition);
    preDestroy.ifPresent(preDestroyCall -> {
      System.out.println("preDestroyCall = " + preDestroyCall);
    });
    preDestroy.ifPresent(preDestroyCall -> root.put("preDestroy", preDestroyCall));



    OutputStream os = new OutputStream() {
      private StringBuilder sb = new StringBuilder();

      @Override
      public void write(int b) {
        sb.append((char) b);
      }

      public String toString() {
        return sb.toString();
      }
    };
    try (Writer out = new OutputStreamWriter(os, "UTF-8")) {
      Template temp = cfg.getTemplate("test.ftlh");
      temp.process(root, out);

      // System.out.println("!!! \n " + os.toString());

      String fileName = Utils.getQualifiedFactoryName(beanDefinition.getType());

      System.out.println("generate " + fileName);

      write(iocContext, fileName, os.toString());


    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    } catch (TemplateException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  protected Dep generateFactoryFieldDeclaration(BeanDefinition beanDefinition,
      InjectableVariableDefinition fieldPoint, String kind) {
    Dep dependency = new Dep();
    String varName = "_" + kind + "_" + fieldPoint.getVariableElement().getSimpleName().toString();
    dependency.fieldName = varName;
    String typeQualifiedName = generationUtils.getActualQualifiedBeanName(fieldPoint);

    Expression expression = generateFactoryFieldDeclaration2(new ClassBuilder(beanDefinition),
        beanDefinition, fieldPoint, kind);
    dependency.call = expression.toString();

    dependency.fqdn = typeQualifiedName;
    return dependency;
  }

  protected LambdaExpr generateFactoryFieldDeclaration2(ClassBuilder classBuilder,
      BeanDefinition definition, InjectableVariableDefinition fieldPoint, String kind) {
    String varName = "_" + kind + "_" + fieldPoint.getVariableElement().getSimpleName().toString();
    String typeQualifiedName = generationUtils.getActualQualifiedBeanName(fieldPoint);
    ClassOrInterfaceType supplier =
        new ClassOrInterfaceType().setName(Supplier.class.getSimpleName());

    ClassOrInterfaceType type = new ClassOrInterfaceType();
    type.setName(InstanceFactory.class.getSimpleName());
    type.setTypeArguments(new ClassOrInterfaceType().setName(typeQualifiedName));
    supplier.setTypeArguments(type);


    Expression beanCall;
    if (fieldPoint.getImplementation().isPresent()
        && fieldPoint.getImplementation().get().getIocGenerator().isPresent()) {
      beanCall = fieldPoint.getImplementation().get().getIocGenerator().get()
          .generateBeanLookupCall(classBuilder, fieldPoint);
    } else if (fieldPoint.getGenerator().isPresent()) {
      beanCall = fieldPoint.getGenerator().get().generateBeanLookupCall(classBuilder, fieldPoint);
    } else {
      beanCall = generateBeanLookupCall(fieldPoint);
    }

    if (beanCall == null) {
      throw new GenerationException();
    }

    LambdaExpr lambda = new LambdaExpr().setEnclosingParameters(true);
    lambda.setBody(new ExpressionStmt(beanCall));
    return lambda;
  }

  public Expression generateBeanLookupCall(InjectableVariableDefinition fieldPoint) {
    String typeQualifiedName = generationUtils.getActualQualifiedBeanName(fieldPoint);
    MethodCallExpr callForProducer = new MethodCallExpr(new NameExpr("beanManager"), "lookupBean")
        .addArgument(new FieldAccessExpr(new NameExpr(typeQualifiedName), "class"));
    return callForProducer;
  }

  public static class Dep {
    private String fieldName = "?";
    private String fqdn = "?";
    private String call;


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
    System.out.println("fileName " + fileName + " \n" + source);

    /*    FileObject file = iocContext.getGenerationContext().getProcessingEnvironment().getFiler()
        .getResource(StandardLocation.ANNOTATION_PROCESSOR_PATH, "", fileName + ".java");

    System.out.println("file " + file);*/


    try {
      JavaFileObject sourceFile = iocContext.getGenerationContext().getProcessingEnvironment()
          .getFiler().createSourceFile(fileName);


      try (Writer writer = sourceFile.openWriter()) {
        writer.write(source);
      }
    } catch (FilerException e) {
      System.out.println("FilerException " + e);

      // throw new GenerationException(e);
    }


  }


}
