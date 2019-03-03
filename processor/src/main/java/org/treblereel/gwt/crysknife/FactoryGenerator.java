package org.treblereel.gwt.crysknife;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.FilerException;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

import org.treblereel.gwt.crysknife.client.Application;
import org.treblereel.gwt.crysknife.generator.api.ClassBuilder;
import org.treblereel.gwt.crysknife.generator.context.GenerationContext;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;
import org.treblereel.gwt.crysknife.util.Utils;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/20/19
 */
public class FactoryGenerator {

    private final GenerationContext generationContext;

    private final IOCContext iocContext;

    FactoryGenerator(IOCContext iocContext, GenerationContext generationContext) {
        this.iocContext = iocContext;
        this.generationContext = generationContext;
    }

    void generate() {
        try {

            Set<Map.Entry<TypeElement, BeanDefinition>> beans = iocContext.getBeans()
                    .entrySet()
                    .stream()
                    .filter(entry -> entry.getValue().getType().getAnnotation(Application.class) == null)
                    .collect(Collectors.toSet());

            for (Map.Entry<TypeElement, BeanDefinition> entry : beans) {
                String fileName = Utils.getQualifiedFactoryName(entry.getKey());
                String source = new ClassBuilder(entry.getValue()).build();
                build(fileName, source);
            }
        } catch (IOException e1) {
            throw new Error(e1);
        }
    }

    private void build(String fileName, String source) throws IOException {
        JavaFileObject builderFile = generationContext.getProcessingEnvironment().getFiler()
                .createSourceFile(fileName);

        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
            out.append(source);
        } catch (FilerException e) {
            throw new Error(e);
        }
    }
}
