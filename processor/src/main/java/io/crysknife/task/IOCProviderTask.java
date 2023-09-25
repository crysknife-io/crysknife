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

package io.crysknife.task;


import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.inject.Named;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ArrayCreationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import io.crysknife.client.InstanceFactory;
import io.crysknife.client.ioc.ContextualTypeProvider;
import io.crysknife.client.ioc.IOCProvider;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.definition.InjectableVariableDefinition;
import io.crysknife.exception.GenerationException;
import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.generator.ManagedBeanGenerator;
import io.crysknife.generator.api.IOCGenerator;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.TreeLogger;
import io.crysknife.util.TypeUtils;
import io.crysknife.validation.Check;
import io.crysknife.validation.Validator;


/**
 * @author Dmitrii Tikhomirov Created by treblereel 11/5/21
 */
// TODO fix broken field injection points
public class IOCProviderTask implements Task {

    private IOCContext context;
    private TreeLogger logger;
    private TypeMirror contextualTypeProvider;
    private TypeMirror provider;
    private Validator validator;

    public IOCProviderTask(IOCContext context, TreeLogger logger) {
        this.context = context;
        this.logger = logger;

        this.contextualTypeProvider = context.getGenerationContext().getTypes()
                .erasure(context.getTypeMirror(ContextualTypeProvider.class));
        this.provider = context.getGenerationContext().getTypes()
                .erasure(context.getTypeMirror(jakarta.inject.Provider.class));

        validator = new ProviderValidator(context);
    }

    @Override
    public void execute() throws UnableToCompleteException {
        for (TypeElement typeElement : context
                .getTypeElementsByAnnotation(IOCProvider.class.getCanonicalName())) {
            process(typeElement);
        }
    }

    private void process(TypeElement type) throws UnableToCompleteException {
        for (TypeMirror iface : type.getInterfaces()) {
            if (isContextualTypeProvider(iface) || isProvider(iface)) {
                DeclaredType asDeclaredType = (DeclaredType) iface;
                TypeMirror provided = asDeclaredType.getTypeArguments().get(0);
                TypeMirror erased = context.getGenerationContext().getTypes().erasure(provided);

                validator.validate(type);
                logger.log(TreeLogger.Type.INFO, String.format("registered @IOCProvider for %s", erased));

                BeanDefinition beanDefinitionContextualTypeProvider =
                        context.getBeanDefinitionOrCreateAndReturn(type.asType());

                beanDefinitionContextualTypeProvider
                        .setIocGenerator(new ManagedBeanGenerator(logger, context));

                BeanDefinition beanDefinition = context.getBeanDefinitionOrCreateAndReturn(erased);
                beanDefinition.setHasFactory(false);
                beanDefinition
                        .setIocGenerator(new ProviderStatelessIOCGenerator(context, type, erased, iface));
                break;
            }
        }
    }

    private boolean isContextualTypeProvider(TypeMirror iface) {
        return context.getGenerationContext().getTypes().isSameType(contextualTypeProvider,
                context.getGenerationContext().getTypes().erasure(iface));
    }

    private boolean isProvider(TypeMirror iface) {
        return context.getGenerationContext().getTypes().isSameType(provider,
                context.getGenerationContext().getTypes().erasure(iface));
    }

    private class ProviderStatelessIOCGenerator
            extends IOCGenerator<io.crysknife.definition.BeanDefinition> {

        private final TypeElement type;
        private final TypeMirror erased;
        private final TypeMirror iface;

        public ProviderStatelessIOCGenerator(IOCContext iocContext, TypeElement type, TypeMirror erased,
                                             TypeMirror iface) {
            super(IOCProviderTask.this.logger, iocContext);
            this.type = type;
            this.erased = erased;
            this.iface = iface;
        }

        @Override
        public void register() {

        }

        @Override
        public String generateBeanLookupCall(InjectableVariableDefinition fieldPoint) {
            if (isProvider(iface)) {
                MethodCallExpr get =
                        new MethodCallExpr(
                                new MethodCallExpr(new MethodCallExpr(new NameExpr("beanManager"), "lookupBean")
                                        .addArgument(type.getQualifiedName().toString() + ".class"), "getInstance"),
                                "get");

                ClassOrInterfaceType type = new ClassOrInterfaceType();
                type.setName(InstanceFactory.class.getCanonicalName());
                type.setTypeArguments(new ClassOrInterfaceType().setName(erased.toString()));

                ObjectCreationExpr factory = new ObjectCreationExpr().setType(type);
                NodeList<BodyDeclaration<?>> supplierClassBody = new NodeList<>();

                MethodDeclaration getInstance = new MethodDeclaration();
                getInstance.setModifiers(com.github.javaparser.ast.Modifier.Keyword.PUBLIC);
                getInstance.setName("getInstance");
                getInstance.addAnnotation(Override.class);
                getInstance.setType(new ClassOrInterfaceType().setName(erased.toString()));

                getInstance.getBody().get().addAndGetStatement(new ReturnStmt(get));
                supplierClassBody.add(getInstance);

                return factory.setAnonymousClassBody(supplierClassBody).toString();

            } else {
                MethodCallExpr methodCallExpr = new MethodCallExpr(
                        new MethodCallExpr(new MethodCallExpr(new NameExpr("beanManager"), "lookupBean")
                                .addArgument(type.getQualifiedName().toString() + ".class"), "getInstance"),
                        "provide");

                ArrayInitializerExpr withAssignableTypesValues = new ArrayInitializerExpr();

                TypeMirror fieldEnclosingElement =
                        context.getGenerationContext().getProcessingEnvironment().getTypeUtils()
                                .erasure(TypeUtils.getEnclosingElement(fieldPoint.getVariableElement()).asType());

                TypeMirror beanEnclosing = context.getGenerationContext().getProcessingEnvironment()
                        .getTypeUtils().erasure(fieldPoint.getEnclosingBeanDefinition().getType());

                if (context.getGenerationContext().getProcessingEnvironment().getTypeUtils()
                        .isSameType(fieldEnclosingElement, beanEnclosing)) {
                    ((DeclaredType) fieldPoint.getVariableElement().asType()).getTypeArguments().stream()
                            .map(t -> context.getGenerationContext().getProcessingEnvironment().getTypeUtils()
                                    .erasure(t))
                            .forEach(
                                    type -> withAssignableTypesValues.getValues().add(new NameExpr(type + ".class")));
                } else {
                    List<TypeMirror> args = ((DeclaredType) fieldPoint.getVariableElement().asType())
                            .getTypeArguments().stream().collect(Collectors.toUnmodifiableList());

                    for (int i = 0; i < args.size(); i++) {
                        TypeMirror type = args.get(i);
                        if (type.getKind().equals(TypeKind.TYPEVAR)) {
                            DeclaredType exec = (DeclaredType) iocContext.getGenerationContext()
                                    .getProcessingEnvironment().getTypeUtils()
                                    .asMemberOf((DeclaredType) fieldPoint.getEnclosingBeanDefinition().getType(),
                                            fieldPoint.getVariableElement());
                            TypeMirror paramType = exec.getTypeArguments().get(i);
                            if (paramType.getKind().equals(TypeKind.TYPEVAR)) {
                                throw new GenerationException("Type variable not supported in "
                                        + fieldPoint.getEnclosingBeanDefinition().getQualifiedName() + "."
                                        + fieldPoint.getVariableElement().getSimpleName());
                            } else {
                                withAssignableTypesValues.getValues()
                                        .add(new NameExpr(exec.getTypeArguments().get(i) + ".class"));
                            }
                        } else {
                            withAssignableTypesValues.getValues().add(new NameExpr(type + ".class"));
                        }
                    }
                }

                ArrayCreationExpr withAssignableTypes = new ArrayCreationExpr();
                withAssignableTypes.setElementType(Class.class);
                withAssignableTypes.setInitializer(withAssignableTypesValues);

                methodCallExpr.addArgument(withAssignableTypes);
                List<AnnotationMirror> qualifiers = new ArrayList<>(TypeUtils
                        .getAllElementQualifierAnnotations(iocContext, fieldPoint.getVariableElement()));
                Set<Expression> qualifiersExpression = new HashSet<>();

                qualifiers.forEach(
                        type -> qualifiersExpression.add(generationUtils.createQualifierExpression(type)));

                Named named = fieldPoint.getVariableElement().getAnnotation(Named.class);
                if (named != null) {
                    qualifiersExpression
                            .add(new MethodCallExpr(new NameExpr("io.crysknife.client.internal.QualifierUtil"),
                                    "createNamed")
                                    .addArgument(new StringLiteralExpr(
                                            fieldPoint.getVariableElement().getAnnotation(Named.class).value())));
                }

                ArrayInitializerExpr withQualifiersValues = new ArrayInitializerExpr();
                qualifiersExpression.forEach(type -> withQualifiersValues.getValues().add(type));
                ArrayCreationExpr withQualifiers = new ArrayCreationExpr();
                withQualifiers.setElementType(Annotation.class);
                withQualifiers.setInitializer(withQualifiersValues);
                methodCallExpr.addArgument(withQualifiers);

                ClassOrInterfaceType type = new ClassOrInterfaceType();
                type.setName(InstanceFactory.class.getCanonicalName());
                type.setTypeArguments(new ClassOrInterfaceType().setName(erased.toString()));

                ObjectCreationExpr factory = new ObjectCreationExpr().setType(type);
                NodeList<BodyDeclaration<?>> supplierClassBody = new NodeList<>();

                MethodDeclaration getInstance = new MethodDeclaration();
                getInstance.setModifiers(com.github.javaparser.ast.Modifier.Keyword.PUBLIC);
                getInstance.setName("getInstance");
                getInstance.addAnnotation(Override.class);
                getInstance.setType(new ClassOrInterfaceType().setName(erased.toString()));

                getInstance.getBody().get().addAndGetStatement(new ReturnStmt(methodCallExpr));
                supplierClassBody.add(getInstance);

                factory.setAnonymousClassBody(supplierClassBody);

                return factory.toString();
            }
        }
    }

    private class ProviderValidator extends Validator<TypeElement> {

        private Set<Check> checks = new HashSet<>() {
            {
                add(new Check<TypeElement>() {
                    @Override
                    public void check(TypeElement element) throws UnableToCompleteException {
                        if (element.getModifiers().contains(Modifier.ABSTRACT)) {
                            log(element, "IOCProvider must not be abstract");
                        }
                    }
                });

                add(new Check<TypeElement>() {
                    @Override
                    public void check(TypeElement element) throws UnableToCompleteException {
                        if (!element.getModifiers().contains(Modifier.PUBLIC)) {
                            log(element, "IOCProvider must be public");
                        }
                    }
                });

            }
        };

        public ProviderValidator(IOCContext context) {
            super(context);
            checks.forEach(check -> addCheck(check));
        }
    }
}
