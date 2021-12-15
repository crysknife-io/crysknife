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

package io.crysknife.ui.translation.generator;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import io.crysknife.annotation.Generator;
import io.crysknife.client.InstanceFactory;
import io.crysknife.definition.Definition;
import io.crysknife.definition.InjectableVariableDefinition;
import io.crysknife.generator.ScopedBeanGenerator;
import io.crysknife.generator.WiringElementType;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.TreeLogger;
import io.crysknife.ui.translation.api.spi.TranslationService;

import javax.inject.Inject;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 11/5/21
 */
@Generator(priority = 100002)
public class TranslationServiceGenerator extends ScopedBeanGenerator {

  public TranslationServiceGenerator(TreeLogger treeLogger, IOCContext iocContext) {
    super(treeLogger, iocContext);
  }

  @Override
  public void register() {
    iocContext.register(Inject.class, TranslationService.class, WiringElementType.BEAN, this);
  }

  @Override
  public void generate(ClassBuilder clazz, Definition beanDefinition) {

    new TranslationServiceImplGenerator(iocContext).generate();

    /*    Set<TypeElement> annotations = new HashSet<>();
    TypeElement resource = iocContext.getGenerationContext().getElements()
        .getTypeElement(Resource.class.getCanonicalName());
    annotations.add(resource);
    ClientBundleAnnotationProcessor clientBundleAnnotationProcessor =
        new ClientBundleAnnotationProcessor();
    clientBundleAnnotationProcessor
        .init(iocContext.getGenerationContext().getProcessingEnvironment());
    clientBundleAnnotationProcessor.process(annotations,
        iocContext.getGenerationContext().getRoundEnvironment());*/
  }

  @Override
  public Expression generateBeanLookupCall(ClassBuilder clazz,
      InjectableVariableDefinition fieldPoint) {
    ClassOrInterfaceType type = new ClassOrInterfaceType();
    type.setName(InstanceFactory.class.getCanonicalName());
    type.setTypeArguments(
        new ClassOrInterfaceType().setName(TranslationService.class.getCanonicalName()));

    ObjectCreationExpr factory = new ObjectCreationExpr().setType(type);
    NodeList<BodyDeclaration<?>> supplierClassBody = new NodeList<>();

    MethodDeclaration getInstance = new MethodDeclaration();
    getInstance.setModifiers(com.github.javaparser.ast.Modifier.Keyword.PUBLIC);
    getInstance.setName("getInstance");
    getInstance.addAnnotation(Override.class);
    getInstance
        .setType(new ClassOrInterfaceType().setName(TranslationService.class.getCanonicalName()));

    getInstance.getBody().get().addAndGetStatement(new ReturnStmt(new FieldAccessExpr(
        new NameExpr(TranslationService.class.getCanonicalName() + "Impl"), "INSTANCE")));
    supplierClassBody.add(getInstance);

    factory.setAnonymousClassBody(supplierClassBody);

    return factory;
  }
}
