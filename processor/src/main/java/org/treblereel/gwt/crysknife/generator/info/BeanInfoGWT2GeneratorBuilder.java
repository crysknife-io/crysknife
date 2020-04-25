package org.treblereel.gwt.crysknife.generator.info;

import java.io.IOException;

import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;
import org.treblereel.gwt.crysknife.generator.point.FieldPoint;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/26/20
 */
public class BeanInfoGWT2GeneratorBuilder extends AbstractBeanInfoGenerator {

    private final String newLine = System.lineSeparator();
    private BeanDefinition bean;
    private StringBuilder clazz;

    BeanInfoGWT2GeneratorBuilder(IOCContext iocContext) {
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
                .append("Info").append(" {");
    }

    private void addFields() {
        for (FieldPoint fieldPoint : bean.getFieldInjectionPoints()) {
            clazz.append(newLine);
            makeSetter(fieldPoint);
            clazz.append(newLine);
            makeGetter(fieldPoint);
        }
    }

    private void makeSetter(FieldPoint fieldPoint) {
        clazz.append("static native void ")
                .append(fieldPoint.getName())
                .append("(");
        clazz.append(bean.getClassName()).append(" ").append(" instance").append(",");
        clazz.append("Object").append(" ").append(" value").append(")/*-{");
        clazz.append(newLine);

        clazz.append("    ")
                .append("instance.@").append(bean.getQualifiedName())
                .append("::").append(fieldPoint.getName()).append("=").append("value;");

        clazz.append(newLine).append("}-*/;");
    }

    private void makeGetter(FieldPoint fieldPoint) {
        clazz.append("static native ")
                .append(fieldPoint.getType().getQualifiedName().toString())
                .append(" ")
                .append(fieldPoint.getName())
                .append("(");
        clazz.append(bean.getClassName()).append(" ").append(" instance");
        clazz.append(")/*-{");
        clazz.append(newLine);

        clazz.append("    ")
                .append("return instance.@").append(bean.getQualifiedName())
                .append("::").append(fieldPoint.getName())
                .append(";");

        clazz.append(newLine).append("}-*/;");
    }
}
