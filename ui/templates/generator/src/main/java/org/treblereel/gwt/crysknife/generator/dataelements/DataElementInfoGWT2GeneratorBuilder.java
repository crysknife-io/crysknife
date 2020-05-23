package org.treblereel.gwt.crysknife.generator.dataelements;

import java.io.IOException;

import javax.inject.Inject;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;

import com.google.auto.common.MoreElements;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;
import org.treblereel.gwt.crysknife.templates.client.annotation.DataField;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/26/20
 */
class DataElementInfoGWT2GeneratorBuilder extends Generator {

    private final String newLine = System.lineSeparator();
    private BeanDefinition bean;
    private StringBuilder clazz;

    DataElementInfoGWT2GeneratorBuilder(IOCContext iocContext) {
        super(iocContext);
    }

    @Override
    protected String build(BeanDefinition bean) throws IOException {
        this.bean = bean;
        this.clazz = new StringBuilder();
        initClass();
        addFields();
        return clazz.append(newLine).append("}").toString();
    }

    private void initClass() {
        clazz.append("package ").append(bean.getPackageName()).append(";");
        clazz.append(newLine);
        clazz.append("class ").append(bean.getClassName())
                .append("DataElementInfo").append(" {");
    }

    private void addFields() {
        ElementFilter.fieldsIn(bean.getType().getEnclosedElements()).stream()
                .filter(field -> MoreElements.isAnnotationPresent(field, DataField.class))
                .filter(field -> !MoreElements.isAnnotationPresent(field, Inject.class))
                .forEach(fieldPoint -> {
                    clazz.append(newLine);
                    makeSetter(fieldPoint);
                    clazz.append(newLine);
                    makeGetter(fieldPoint);
                });
    }

    private void makeSetter(VariableElement fieldPoint) {
        clazz.append("static native void ")
                .append(fieldPoint.getSimpleName())
                .append("(");
        clazz.append(bean.getClassName()).append(" ").append(" instance").append(",");
        clazz.append("Object").append(" ").append(" value").append(")/*-{");
        clazz.append(newLine);

        clazz.append("    ")
                .append("instance.@").append(bean.getQualifiedName())
                .append("::").append(fieldPoint.getSimpleName()).append("=").append("value;");

        clazz.append(newLine).append("}-*/;");
    }

    private void makeGetter(VariableElement fieldPoint) {
        clazz.append("static native ")
                .append(fieldPoint.asType().toString())
                .append(" ")
                .append(fieldPoint.getSimpleName())
                .append("(");
        clazz.append(bean.getClassName()).append(" ").append(" instance");
        clazz.append(")/*-{");
        clazz.append(newLine);

        clazz.append("    ")
                .append("return instance.@").append(bean.getQualifiedName())
                .append("::").append(fieldPoint.getSimpleName())
                .append(";");

        clazz.append(newLine).append("}-*/;");
    }
}
