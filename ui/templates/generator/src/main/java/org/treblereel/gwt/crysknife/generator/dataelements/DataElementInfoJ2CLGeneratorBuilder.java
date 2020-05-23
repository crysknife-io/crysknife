package org.treblereel.gwt.crysknife.generator.dataelements;

import javax.inject.Inject;
import javax.lang.model.util.ElementFilter;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.google.auto.common.MoreElements;
import org.treblereel.gwt.crysknife.client.Reflect;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;
import org.treblereel.gwt.crysknife.templates.client.annotation.DataField;
import org.treblereel.gwt.crysknife.util.Utils;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/26/20
 */
class DataElementInfoJ2CLGeneratorBuilder extends Generator {

    private BeanDefinition bean;
    private CompilationUnit clazz = new CompilationUnit();
    private ClassOrInterfaceDeclaration classDeclaration;

    DataElementInfoJ2CLGeneratorBuilder(IOCContext iocContext) {
        super(iocContext);
    }

    @Override
    protected String build(BeanDefinition bean) {
        this.bean = bean;
        this.clazz = new CompilationUnit();
        initClass();
        addFields();
        return clazz.toString();
    }

    private void initClass() {
        clazz.setPackageDeclaration(bean.getPackageName());
        classDeclaration = clazz.addClass(bean.getClassName() + "DataElementInfo");
        clazz.addImport(Reflect.class);
    }

    private void addFields() {
        ElementFilter.fieldsIn(bean.getType().getEnclosedElements()).stream()
                .filter(field -> MoreElements.isAnnotationPresent(field, DataField.class))
                .filter(field -> !MoreElements.isAnnotationPresent(field, Inject.class))
                .forEach(fieldPoint -> classDeclaration.addFieldWithInitializer(String.class,
                                                                            fieldPoint.getSimpleName().toString(),
                                                                            new StringLiteralExpr(Utils.getJsFieldName(fieldPoint)),
                                                                            Modifier.Keyword.PUBLIC,
                                                                            Modifier.Keyword.FINAL,
                                                                            Modifier.Keyword.STATIC));
    }
}
