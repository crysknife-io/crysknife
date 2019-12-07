package org.treblereel.gwt.crysknife.util;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.Elements;

import com.google.auto.common.MoreElements;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/21/19
 */
public class Utils {

    private Utils() {

    }

    public static String getQualifiedName(Element elm) {
        if (elm.getKind().equals(ElementKind.FIELD) || elm.getKind().equals(ElementKind.PARAMETER)) {
            VariableElement variableElement = MoreElements.asVariable(elm);
            DeclaredType declaredType = (DeclaredType) variableElement.asType();
            return declaredType.toString();
        } else if (elm.getKind().equals(ElementKind.CONSTRUCTOR)) {
            ExecutableElement executableElement = MoreElements.asExecutable(elm);
            return executableElement.getEnclosingElement().toString();
        } else if (elm.getKind().equals(ElementKind.CLASS)) {
            TypeElement typeElement = MoreElements.asType(elm);
            return typeElement.getQualifiedName().toString();
        } else if (elm.getKind().equals(ElementKind.INTERFACE)) {
            TypeElement typeElement = MoreElements.asType(elm);
            return typeElement.getQualifiedName().toString();
        }
        throw new Error("Unable to process bean " + elm.toString());
    }

    public static String getQualifiedFactoryName(TypeElement bean) {
        return getPackageName(bean) + "." + getFactoryClassName(bean);
    }

    public static String getFactoryClassName(TypeElement bean) {
        return bean.getSimpleName().toString() + "_Factory";
    }

    public static Set<Element> getAnnotatedElements(
            Elements elements,
            TypeElement type,
            Class<? extends Annotation> annotation) {
        Set<Element> found = new HashSet<>();
        for (Element e : elements.getAllMembers(type)) {
            if (e.getAnnotation(annotation) != null) {
                found.add(e);
            }
        }
        return found;
    }

    public static String getPackageName(TypeElement singleton) {
        return MoreElements.getPackage(singleton).getQualifiedName().toString();
    }

    public static String toVariableName(String name) {
        return name.toLowerCase().replaceAll("\\.", "_");
    }

    public static String toVariableName(TypeElement injection) {
        return toVariableName(getQualifiedName(injection));
    }
}
