package org.treblereel.gwt.crysknife.mutationobserver.generator;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.auto.common.MoreElements;
import elemental2.dom.HTMLElement;
import elemental2.dom.MutationRecord;
import org.treblereel.gwt.crysknife.annotation.Generator;
import org.treblereel.gwt.crysknife.generator.ScopedBeanGenerator;
import org.treblereel.gwt.crysknife.generator.WiringElementType;
import org.treblereel.gwt.crysknife.generator.api.ClassBuilder;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;
import org.treblereel.gwt.crysknife.generator.definition.Definition;
import org.treblereel.gwt.crysknife.generator.definition.ExecutableDefinition;
import org.treblereel.gwt.crysknife.mutationobserver.client.api.MutationObserver;
import org.treblereel.gwt.crysknife.mutationobserver.client.api.ObserverCallback;
import org.treblereel.gwt.crysknife.mutationobserver.client.api.OnAttach;
import org.treblereel.gwt.crysknife.mutationobserver.client.api.OnDetach;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/7/19
 */
@Generator(priority = 100002)
public class MutationObserverGenerator extends ScopedBeanGenerator {

    private IOCContext iocContext;

    private TypeMirror htmlElement;

    private BeanDefinition mutationObserverBeanDefinition;

    @Override
    public void register(IOCContext iocContext) {
        this.iocContext = iocContext;
        iocContext.register(OnAttach.class, WiringElementType.METHOD_DECORATOR, this);
        iocContext.register(OnDetach.class, WiringElementType.METHOD_DECORATOR, this);

        htmlElement = iocContext.getGenerationContext()
                .getElements()
                .getTypeElement(HTMLElement.class.getCanonicalName())
                .asType();

        TypeElement mutationObserver = iocContext.getGenerationContext()
                .getElements()
                .getTypeElement(MutationObserver.class.getCanonicalName());

        mutationObserverBeanDefinition = iocContext.getBeanDefinitionOrCreateAndReturn(mutationObserver);

        iocContext.getBeans().put(mutationObserver, mutationObserverBeanDefinition);
        if (!iocContext.getOrderedBeans().contains(mutationObserver)) {
            iocContext.getOrderedBeans().add(mutationObserver);
        }
    }

    public void generateBeanFactory(ClassBuilder builder, Definition definition) {
        if (definition instanceof ExecutableDefinition) {
            ExecutableDefinition mutationObserver = (ExecutableDefinition) definition;
            ifValid(mutationObserver);
            VariableElement target = findField(mutationObserver);
            isValid(target);
            generateCallback(builder, mutationObserver);
        }
    }

    private void isValid(VariableElement target) {
        if (target.getModifiers().contains(Modifier.PRIVATE)) {
            throw new Error("MutationObserver target  [" + target.getSimpleName() +
                                    " in " + target.getEnclosingElement().getSimpleName() + " must not be private");
        }

        if (target.getModifiers().contains(Modifier.STATIC)) {
            throw new Error("MutationObserver target  [" + target.getSimpleName() +
                                    " in " + target.getEnclosingElement().getSimpleName() + " must not be static");
        }

        if (iocContext.getGenerationContext().getTypes().isSubtype(htmlElement, target.asType())) {
            throw new Error("MutationObserver target  [" + target.getSimpleName() +
                                    " in " + target.getEnclosingElement().getSimpleName() + " must be subtype of HTMLElement atm");
        }
    }

    private VariableElement findField(ExecutableDefinition mutationObserver) {
        String fieldName = findFieldName(mutationObserver.getExecutableElement());

        Element target = mutationObserver.
                getExecutableElement()
                .getEnclosingElement()
                .getEnclosedElements().stream().filter(elm -> elm.getKind().equals(ElementKind.FIELD))
                .filter(elm -> MoreElements.asVariable(elm).getSimpleName().toString().equals(fieldName))
                .findAny().orElseThrow(() -> new Error("Unable to find field named " + fieldName + " in " + mutationObserver.
                        getExecutableElement()
                        .getEnclosingElement()));

        return MoreElements.asVariable(target);
    }

    private String findFieldName(ExecutableElement executableElement) {
        OnDetach onDetach = executableElement.getAnnotation(OnDetach.class);
        if (onDetach != null && !onDetach.value().isEmpty()) {
            return onDetach.value();
        }

        OnAttach onAttach = executableElement.getAnnotation(OnAttach.class);
        if (onAttach != null && !onAttach.value().isEmpty()) {
            return onAttach.value();
        }
        throw new Error("Unable to find field name");
    }

    private void ifValid(ExecutableDefinition mutationObserver) {
        if (mutationObserver.getExecutableElement().getParameters().size() > 1 || mutationObserver.getExecutableElement().getParameters().isEmpty()) {
            throw new Error("Method [" + mutationObserver.getExecutableElement().getSimpleName() +
                                    " in " + mutationObserver.getExecutableElement().getEnclosingElement().getSimpleName() + " must have only one arg of type MutationRecord");
        }

        if (mutationObserver.getExecutableElement().getModifiers().contains(Modifier.PRIVATE)) {
            throw new Error("Method [" + mutationObserver.getExecutableElement().getSimpleName() +
                                    " in " + mutationObserver.getExecutableElement().getEnclosingElement().getSimpleName() + " must not be private");
        }

        if (mutationObserver.getExecutableElement().getModifiers().contains(Modifier.STATIC)) {
            throw new Error("Method [" + mutationObserver.getExecutableElement().getSimpleName() +
                                    " in " + mutationObserver.getExecutableElement().getEnclosingElement().getSimpleName() + " must not be static");
        }

        TypeMirror mutationRecord = iocContext.getGenerationContext()
                .getElements()
                .getTypeElement(MutationRecord.class.getCanonicalName())
                .asType();

        if (!mutationObserver.getExecutableElement()
                .getParameters()
                .get(0).asType().equals(mutationRecord)) {
            throw new Error("Method [" + mutationObserver.getExecutableElement().getSimpleName() +
                                    " in " + mutationObserver.getExecutableElement().getEnclosingElement().getSimpleName() + " must have arg of type MutationRecord");
        }
    }

    public void generateCallback(ClassBuilder builder, ExecutableDefinition definition) {
        builder.getClassCompilationUnit().addImport(MutationObserver.class);
        builder.getClassCompilationUnit().addImport(ObserverCallback.class);
        builder.getClassCompilationUnit().addImport("org.treblereel.gwt.crysknife.client.BeanManagerImpl");

        ClassOrInterfaceDeclaration factoryDeclaration = new ClassOrInterfaceDeclaration()
                .setName("BeanManagerImpl");

        String callbackMethodName = definition.getExecutableElement()
                .getAnnotation(OnAttach.class) != null ? "addOnAttachListener" : "addOnDetachListener";

        String fieldName = definition.getExecutableElement()
                .getAnnotation(OnAttach.class) != null ? definition.getExecutableElement()
                .getAnnotation(OnAttach.class).value() : definition.getExecutableElement()
                .getAnnotation(OnDetach.class).value();

        EnclosedExpr castToAbstractEventHandler = new EnclosedExpr(new CastExpr(new ClassOrInterfaceType().setName("MutationObserver"), new MethodCallExpr(
                new MethodCallExpr(
                        new MethodCallExpr(factoryDeclaration.getNameAsExpression(), "get"),
                        "lookupBean").addArgument("MutationObserver.class"), "get")));

        builder.getGetMethodDeclaration()
                .getBody()
                .get().addAndGetStatement(new MethodCallExpr(castToAbstractEventHandler, callbackMethodName)
                                                  .addArgument("this.instance." + fieldName)
                                                  .addArgument("(ObserverCallback) m -> this.instance." + definition.getExecutableElement().getSimpleName().toString() + "(m)"));
    }
}