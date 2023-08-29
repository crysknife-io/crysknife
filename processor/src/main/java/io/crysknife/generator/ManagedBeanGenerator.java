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
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.auto.common.MoreTypes;
import elemental2.core.Function;
import io.crysknife.client.InstanceFactory;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.definition.InjectableVariableDefinition;
import io.crysknife.exception.GenerationException;
import io.crysknife.generator.api.ClassMetaInfo;
import io.crysknife.generator.api.Generator;
import io.crysknife.generator.api.IOCGenerator;
import io.crysknife.generator.api.WiringElementType;
import io.crysknife.generator.context.ExecutionEnv;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.generator.helpers.FreemarkerTemplateGenerator;
import io.crysknife.generator.helpers.PostConstructAnnotationGenerator;
import io.crysknife.generator.helpers.PreDestroyAnnotationGenerator;
import io.crysknife.logger.TreeLogger;
import io.crysknife.util.TypeUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import org.treblereel.j2cl.processors.utils.J2CLUtils;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Modifier;
import javax.lang.model.util.ElementFilter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/2/19
 */
@Generator(priority = 1)
public class ManagedBeanGenerator extends IOCGenerator<BeanDefinition> {

  private final FreemarkerTemplateGenerator freemarkerTemplateGenerator =
      new FreemarkerTemplateGenerator("managedbean.ftlh");
  private final J2CLUtils j2CLUtils;
  private PreDestroyAnnotationGenerator preDestroyAnnotation =
      new PreDestroyAnnotationGenerator(iocContext);
  private PostConstructAnnotationGenerator postConstructAnnotation =
      new PostConstructAnnotationGenerator(iocContext);

  public ManagedBeanGenerator(TreeLogger treeLogger, IOCContext iocContext) {
    super(treeLogger, iocContext);
    this.j2CLUtils = new J2CLUtils(iocContext.getGenerationContext().getProcessingEnvironment());
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
    Map<String, Object> beanDTO = new HashMap<>();
    List<Dep> fields = new ArrayList<>();

    beanDTO.put("jre",
        iocContext.getGenerationContext().getExecutionEnv().equals(ExecutionEnv.JRE));
    beanDTO.put("package", beanDefinition.getPackageName());
    beanDTO.put("clazz", beanDefinition.getSimpleClassName().replaceAll("\\.", "_"));
    beanDTO.put("bean", beanDefinition.getSimpleClassName());
    beanDTO.put("isDependent", TypeUtils.isDependent(beanDefinition));
    beanDTO.put("imports", classMetaInfo.getImports());
    beanDTO.put("deps", fields);
    beanDTO.put("isProxy", beanDefinition.isProxy());

    constructor(beanDefinition, beanDTO, fields);
    deps(beanDefinition, fields);

    privateMethods(beanDefinition, classMetaInfo, beanDTO);
    fieldDecorators(beanDefinition, classMetaInfo);
    interceptorFieldDecorators(beanDefinition, beanDTO);
    methodDecorators(beanDefinition, classMetaInfo);
    classDecorators(beanDefinition, classMetaInfo);
    postConstruct(beanDefinition, beanDTO);
    preDestroy(classMetaInfo, beanDefinition);

    beanDTO.put("fields", classMetaInfo.getBodyStatements());
    beanDTO.put("preDestroy", classMetaInfo.getOnDestroy());
    beanDTO.put("doInitInstance", classMetaInfo.getDoInitInstance());
    beanDTO.put("doCreateInstance", classMetaInfo.getDoCreateInstance());

    String source = freemarkerTemplateGenerator.toSource(beanDTO);
    String fileName = TypeUtils.getQualifiedFactoryName(beanDefinition.getType());
    writeJavaFile(fileName, source);
  }

  public String generateBeanLookupCall(InjectableVariableDefinition fieldPoint) {
    String typeQualifiedName = generationUtils.getActualQualifiedBeanName(fieldPoint);

    MethodCallExpr call = new MethodCallExpr(new NameExpr("beanManager"), "lookupBean")
        .addArgument(new FieldAccessExpr(new NameExpr(typeQualifiedName), "class"));

    if (fieldPoint.getImplementation().isEmpty()) {
      List<AnnotationMirror> qualifiers = new ArrayList<>(
          TypeUtils.getAllElementQualifierAnnotations(iocContext, fieldPoint.getVariableElement()));
      for (AnnotationMirror qualifier : qualifiers) {
        call.addArgument(generationUtils.createQualifierExpression(qualifier));
      }
      Named named = fieldPoint.getVariableElement().getAnnotation(Named.class);
      if (named != null) {
        call.addArgument(new MethodCallExpr(
            new NameExpr("io.crysknife.client.internal.QualifierUtil"), "createNamed")
                .addArgument(new StringLiteralExpr(
                    fieldPoint.getVariableElement().getAnnotation(Named.class).value())));
      }
    }
    return call.toString();
  }

  private void privateMethods(BeanDefinition beanDefinition, ClassMetaInfo classMetaInfo,
      Map<String, Object> root) {
    if (iocContext.getGenerationContext().getExecutionEnv().equals(ExecutionEnv.J2CL)) {
      Set<String> methods = ElementFilter
          .methodsIn(MoreTypes.asTypeElement(beanDefinition.getType()).getEnclosedElements())
          .stream().filter(method -> method.getModifiers().contains(Modifier.PRIVATE))
          .filter(method -> !method.getModifiers().contains(Modifier.NATIVE))
          .map(method -> j2CLUtils.createDeclarationMethodDescriptor(method).getMangledName())
          .collect(Collectors.toSet());
      if (!methods.isEmpty()) {
        classMetaInfo.addImport(JsType.class);
        classMetaInfo.addImport(JsProperty.class);
        classMetaInfo.addImport(Function.class);
        root.put("privateMethods", methods);
      }
    }
  }

  private void interceptorFieldDecorators(BeanDefinition beanDefinition, Map<String, Object> root) {

    if (iocContext.getGenerationContext().getExecutionEnv().equals(ExecutionEnv.J2CL)) {
      List<String> fieldInterceptors = new ArrayList<>();
      root.put("fieldInterceptors", fieldInterceptors);
      beanDefinition.getFields().forEach(fieldPoint -> {
        Expression expr = generationUtils.getFieldAccessorExpression(fieldPoint, "field");
        fieldInterceptors.add(expr.toString());
      });
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
    beanDefinition.getFields().stream().map(field -> processField(field, "field"))
        .forEach(fields::add);
  }

  protected void constructor(BeanDefinition beanDefinition, Map<String, Object> root,
      List<Dep> fields) {
    String params = null;
    if (!beanDefinition.getConstructorParams().isEmpty()) {
      params = beanDefinition.getConstructorParams().stream()
          .map(p -> "_constructor_" + p.getVariableElement().getSimpleName().toString())
          .map(f -> "addDependencyConstructor(this." + f + ".get())")
          .collect(Collectors.joining(","));

      beanDefinition.getConstructorParams().stream()
          .map(field -> processField(field, "constructor")).forEach(fields::add);

    }
    root.put("constructorParams", params);
  }

  protected Dep processField(InjectableVariableDefinition fieldPoint, String kind) {
    Dep dependency = new Dep();
    String varName = "_" + kind + "_" + fieldPoint.getVariableElement().getSimpleName().toString();
    dependency.fieldName = varName;
    String typeQualifiedName = generationUtils.getActualQualifiedBeanName(fieldPoint);

    String expression = generateFactoryFieldDeclaration(fieldPoint);
    dependency.call = expression;
    dependency.fqdn = typeQualifiedName;
    return dependency;
  }

  protected String generateFactoryFieldDeclaration(InjectableVariableDefinition fieldPoint) {
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
    if (beanCall == null) {
      throw new GenerationException("No bean call for " + fieldPoint.getVariableElement().asType());
    }

    return String.format("() -> %s", beanCall);
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
}
