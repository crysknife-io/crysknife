package org.treblereel.gwt.crysknife;

import com.google.auto.common.MoreElements;
import org.treblereel.gwt.crysknife.internal.BeanDefinition;
import org.treblereel.gwt.crysknife.internal.GenerationContext;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.treblereel.gwt.crysknife.internal.Utils.getAllFactoryParameters;
import static org.treblereel.gwt.crysknife.internal.Utils.getPackageName;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/20/19
 */
public class FactoryGenerator {

    private final RoundEnvironment roundEnvironment;

    private final ProcessingEnvironment processingEnvironment;

    private final GenerationContext context;

    private final Map<TypeElement, BeanDefinition> definitions;


    FactoryGenerator(GenerationContext context,
                     Map<TypeElement, BeanDefinition> definitions,
                     RoundEnvironment roundEnvironment,
                     ProcessingEnvironment processingEnvironment) {
        this.context = context;
        this.definitions = definitions;
        this.roundEnvironment = roundEnvironment;
        this.processingEnvironment = processingEnvironment;
    }

    static String getQualifiedFactoryName(TypeElement singleton) {
        return getPackageName(singleton) + "." + getFactoryClassName(singleton);
    }

    static String getQualifiedProviderName(TypeElement singleton) {
        return getPackageName(singleton) + "." + getFactoryClassName(singleton);
    }

    static String getFactoryClassName(TypeElement singleton) {
        return singleton.getSimpleName().toString() + "_Factory";
    }

    void generate() {
        try {
            for (BeanDefinition bean : context.getBeans().values()) {
                build(bean);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

    private void build(BeanDefinition definition) throws IOException {
        TypeElement bean = MoreElements.asType(definition.getElement());
        String className = definition.getElement().getSimpleName().toString();
        String classFactoryName = getFactoryClassName(bean);
        String packageName = getPackageName(bean);
        String qualifiedName = getQualifiedFactoryName(bean);

        JavaFileObject builderFile = processingEnvironment.getFiler()
                .createSourceFile(qualifiedName);

        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {

            if (!packageName.isEmpty()) {
                out.print("package ");
                out.print(packageName);
                out.println(";");
                out.println();
            }
            out.println("import org.treblereel.gwt.crysknife.client.internal.Factory;");
            out.println("import javax.inject.Provider;");
            out.println();

            out.print("public final class ");
            out.print(classFactoryName);
            out.print(" implements Factory<" + className + ">");
            out.print(" {");
            out.println();

            generateBody(definition, out, classFactoryName, className);

            out.println("}");
        }
    }

    private void generateBody(BeanDefinition definition, PrintWriter out, String classFactoryName, String className) {
        generateFieldInjectionFactory(definition, out, classFactoryName, className);
    }

    private void generateFieldInjectionFactory(BeanDefinition definition, PrintWriter out, String classFactoryName, String className) {
        String params = definition.getFieldInjectionPoints()
                .stream()
                .map(point -> getQualifiedProviderName(point.getDeclared()) + " " + point.getName()).collect(Collectors.joining(","));

        String args = definition.getFieldInjectionPoints()
                .stream()
                .map(point -> point.getName()).collect(Collectors.joining(","));

        generateFields(definition, out);
        generateGet(definition, out, className);
        generateConstructor(definition, out, classFactoryName, className, params, args);

    }

    private void generateGet(BeanDefinition definition, PrintWriter out, String className) {
        String args = "";

        System.out.println(definition);


        if (definition.getConstructorInjectionPoint() != null)
            args = definition.getConstructorInjectionPoint()
                    .getParameters()
                    .stream()
                    .map(p -> p.getName() + ".get()")
                    .collect(Collectors.joining(", "));

        out.println("    public " + className + " get() {");
        out.println("        " + className + " instance = new  " + className + "(" + args + ");");


        definition.getFieldInjectionPoints()
                .stream().forEach(param -> {
            out.println("        instance." + param.getName() + " = " + param.getName() + ".get();");

        });

        if (definition.getPostConstract() != null) {
            out.println("        instance." + definition.getPostConstract() + "();");
        }

        out.println("        return instance;");
        out.println("    }");
    }

    private void generateFields(BeanDefinition definition, PrintWriter out) {
        getAllFactoryParameters(definition).entrySet()
                .stream().forEach(v -> {
            out.println("    private final Provider<"+v.getKey() + "> " + v.getValue() + ";");
        });
    }

    private void generateConstructor(BeanDefinition definition, PrintWriter out, String classFactoryName, String className, String params, String args) {
        String constructorAndFieldArgsWithDefinitions = getAllInitParametersWithDefinitions(definition);
        String constructorAndFieldArgs = getAllInitParameters(definition);

        out.println("    public " + classFactoryName + "(" + constructorAndFieldArgsWithDefinitions + ") {");
        getAllFactoryParameters(definition).entrySet()
                .stream().forEach(v -> {
            out.println("        this." + v.getValue() + " = " + v.getValue() + ";");
        });
        out.println("    }");


        out.println("    public static " + classFactoryName + " create(" + constructorAndFieldArgsWithDefinitions + ") {");
        out.println("        return new " + classFactoryName + "(" + constructorAndFieldArgs + ");");
        out.println("    }");
    }

    private String getAllInitParameters(BeanDefinition definition) {
        return getAllFactoryParameters(definition).entrySet()
                .stream()
                .map(v -> v.getValue())
                .collect(Collectors.joining(", "));
    }

    private String getAllInitParametersWithDefinitions(BeanDefinition definition) {
        return getAllFactoryParameters(definition).entrySet()
                .stream()
                .map(v -> "Provider<"+v.getKey() + "> " + v.getValue())
                .collect(Collectors.joining(", "));
    }

}
