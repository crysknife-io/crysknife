package org.treblereel.gwt.crysknife.internal;

import com.google.auto.common.MoreElements;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.Elements;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/21/19
 */
public class Utils {

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
        throw new Error("unable to process bean " + elm.toString());
    }

    public static Set<Element> getAnnotatedElements(
            Elements elements,
            TypeElement type,
            Class<? extends Annotation> annotation) {
        Set<Element> found = new HashSet<>();
        for (Element e : elements.getAllMembers(type)) {
            if (e.getAnnotation(annotation) != null)
                found.add(e);
        }
        return found;
    }

    public static String getPackageName(TypeElement singleton) {
        return MoreElements.getPackage(singleton).getQualifiedName().toString();
    }

    public static Map<String, String> getAllFactoryParameters(BeanDefinition definition) {
        Map<String, String> arguments = new HashMap<>();
        if (definition.getConstructorInjectionPoint() != null) {
            definition.getConstructorInjectionPoint().getParameters().forEach(arg -> {
                arguments.put(arg.getDeclared().toString(), arg.getName());
            });
        }

        if (definition.getFieldInjectionPoints().size() > 0) {
            definition.getFieldInjectionPoints().forEach(arg -> {
                arguments.put(arg.getDeclared().getQualifiedName().toString(), arg.getName());
            });
        }
        return arguments;
    }

    static boolean inComponentScanPackages(Element singleton, Set<String> packages) {
        String pkg = MoreElements.getPackage(singleton).getQualifiedName().toString();
        if(packages!= null && packages.size() > 0){
            for(String p : packages){
                if(pkg.startsWith(p)){
                    return true;
                }
            }
        }
        return false;
    }

    public static String toVariableName(String name) {
        return name.toLowerCase().replaceAll("\\.", "_");
    }
}
