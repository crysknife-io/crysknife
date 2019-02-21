package org.treblereel.gwt.crysknife;

import org.treblereel.gwt.crysknife.internal.BeanDefinition;
import org.treblereel.gwt.crysknife.internal.FieldInjectionPoint;
import org.treblereel.gwt.crysknife.internal.GenerationContext;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.treblereel.gwt.crysknife.internal.Utils.getAllFactoryParameters;
import static org.treblereel.gwt.crysknife.internal.Utils.getPackageName;
import static org.treblereel.gwt.crysknife.internal.Utils.getQualifiedName;

public class BootstrapperGenerator {
    private final RoundEnvironment roundEnvironment;

    private final ProcessingEnvironment processingEnvironment;
    private final Set<String> packages;
    private final GenerationContext context;
    private TypeElement application;

    private String BOOTSTRAP_EXTENSION = "Bootstrap";

    BootstrapperGenerator(
            GenerationContext context,
            RoundEnvironment roundEnvironment,
            ProcessingEnvironment processingEnvironment,
            Set<String> packages,
            TypeElement application) {
        this.roundEnvironment = roundEnvironment;
        this.processingEnvironment = processingEnvironment;
        this.packages = packages;
        this.application = application;
        this.context = context;
    }

    void generate() {

        try {
            build();
        } catch (IOException e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }


    private void build() throws IOException {
        String className = application.getSimpleName().toString();
        String classBootstrapName = className + BOOTSTRAP_EXTENSION;
        String packageName = getPackageName(application);

        String qualifiedBootstrapName = packageName + "." + classBootstrapName;

        JavaFileObject builderFile = processingEnvironment.getFiler()
                .createSourceFile(qualifiedBootstrapName);

        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {

            if (!packageName.isEmpty()) {
                out.print("package ");
                out.print(packageName);
                out.println(";");
                out.println();
            }

            out.println("import javax.inject.Provider;");
            out.println("import " + getQualifiedName(application) + ";");
            out.println();

            out.print("public final class ");
            out.print(classBootstrapName);
            out.print(" {");
            out.println();

            generateFactoryFields(out);

            generateBody(out, className, classBootstrapName);
            generateInitializer(out);

            out.println("}");
        }
    }

    private void generateInitializer(PrintWriter out) {
        BeanDefinition instance = context.getBeans().get(getQualifiedName(application));
        out.println("    public void initialize() {");
        for (FieldInjectionPoint point : instance.getFieldInjectionPoints()) {
            out.println("        this.instance." + point.getName() + " = " + toVariableName(point.getDeclared().toString()) + ".get();");
        }

        if (instance.getPostConstract() != null) {
            out.println("        this.instance." + instance.getPostConstract() + "();");
        }
        out.println("    }");
    }

    private void generateFactoryFields(PrintWriter out) {


        Map<String, BeanDefinition> beans = context.getBeans();
        List<String> orderedBeans = context.getOrderedBeans();

        for (String orderedBean : orderedBeans) {
            if (!orderedBean.equals(getQualifiedName(application))) {
                String init = getAllInitParametersWithDefinitions(beans.get(orderedBean));
                out.println("    private final Provider<" + orderedBean + "> " + toVariableName(orderedBean) + " = " + orderedBean + "_Factory.create(" + init + ");");
            }
        }

    }

    private String getAllInitParametersWithDefinitions(BeanDefinition definition) {
        System.out.println(definition.toString());

        return getAllFactoryParameters(definition).entrySet()
                .stream()
                .map(v -> toVariableName(v.getKey()))
                .collect(Collectors.joining(", "));
    }


    private String toVariableName(String name) {
        return name.toLowerCase().replaceAll("\\.", "_");
    }

    private void generateBody(PrintWriter out, String className, String classBootstrapName) {

        out.println("    private final " + className + " instance;");
        out.println();

        out.println("    " + classBootstrapName + "(" + className + " instance) {");
        out.println("         this.instance = instance;");
        out.println("     }");
    }

}
