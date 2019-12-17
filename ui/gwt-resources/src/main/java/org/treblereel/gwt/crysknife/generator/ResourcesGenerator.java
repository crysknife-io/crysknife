package org.treblereel.gwt.crysknife.generator;

import javax.inject.Provider;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.auto.common.MoreElements;
import org.gwtproject.resources.client.Resource;
import org.gwtproject.resources.ext.ResourceGeneratorUtil;
import org.treblereel.gwt.crysknife.annotation.Generator;
import org.treblereel.gwt.crysknife.client.internal.InstanceImpl;
import org.treblereel.gwt.crysknife.generator.api.ClassBuilder;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;
import org.treblereel.gwt.crysknife.generator.definition.Definition;
import org.treblereel.gwt.crysknife.generator.point.FieldPoint;
import org.treblereel.gwt.crysknife.util.Utils;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/7/19
 */
@Generator(priority = 101)
public class ResourcesGenerator extends BeanIOCGenerator {

    @Override
    public void register(IOCContext iocContext) {
        this.iocContext = iocContext;
        iocContext.register(Resource.class, WiringElementType.DEPENDENT_BEAN, this);
    }

    @Override
    public void generateBeanFactory(ClassBuilder classBuilder, Definition definition) {

    }

    public void addFactoryFieldDeclaration(ClassBuilder classBuilder, BeanDefinition beanDefinition) {
        String varName = Utils.toVariableName(beanDefinition.getQualifiedName());
        ClassOrInterfaceType type = new ClassOrInterfaceType();
        type.setName("org.treblereel.gwt.crysknife.client.Instance");
        type.setTypeArguments(new ClassOrInterfaceType().setName(beanDefinition.getQualifiedName()));

        Parameter param = new Parameter();
        param.setName(varName);
        param.setType(type);

        classBuilder.addField(type, varName, Modifier.Keyword.FINAL, Modifier.Keyword.PRIVATE);
    }

    public String getFactoryVariableName() {
        return "";
    }

    public void addFactoryFieldInitialization(ClassBuilder classBuilder, BeanDefinition beanDefinition) {
        String theName = ResourceGeneratorUtil.generateSimpleSourceName(null, beanDefinition.getType());
        String qualifiedImplName = MoreElements.getPackage(beanDefinition.getType()) + "." + theName;

        classBuilder.getClassCompilationUnit().addImport(InstanceImpl.class);
        classBuilder.getClassCompilationUnit().addImport(Provider.class);
        classBuilder.getClassCompilationUnit().addImport(beanDefinition.getType().getQualifiedName().toString());
        classBuilder.getClassCompilationUnit().addImport(qualifiedImplName);

        String varName = Utils.toVariableName(beanDefinition.getQualifiedName());
        FieldAccessExpr field = new FieldAccessExpr(new ThisExpr(), varName);

        AssignExpr assign = new AssignExpr()
                .setTarget(field)
                .setValue(new NameExpr("new InstanceImpl(new Provider<"
                                               + beanDefinition.getType().getSimpleName()
                                               + ">() {" +
                                               "        @Override" +
                                               "        public " + beanDefinition.getType().getSimpleName() + " get() {" +
                                               "            return new " + theName + "();" +
                                               "        }" +
                                               "    })"));
        classBuilder.addStatementToConstructor(assign);
    }

    @Override
    public Expression generateBeanCall(ClassBuilder classBuilder, FieldPoint fieldPoint, BeanDefinition beanDefinition) {
        String theName = ResourceGeneratorUtil.generateSimpleSourceName(null, beanDefinition.getType());
        String qualifiedImplName = MoreElements.getPackage(beanDefinition.getType()) + "." + theName;
        classBuilder.getClassCompilationUnit().addImport(beanDefinition.getType().getQualifiedName().toString());
        classBuilder.getClassCompilationUnit().addImport(qualifiedImplName);


        return new NameExpr("new " + theName + "()");
    }
}