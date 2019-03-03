package org.treblereel.gwt.crysknife.generator;

import javax.enterprise.context.Dependent;

import com.github.javaparser.ast.Modifier;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.annotation.Generator;
import org.treblereel.gwt.crysknife.generator.api.ClassBuilder;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;
import org.treblereel.gwt.crysknife.generator.definition.Definition;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/2/19
 */
@Generator
public class DependentGenerator extends ScopedBeanGenerator {

    @Override
    public void register(IOCContext iocContext) {
        iocContext.register(Dependent.class, WiringElementType.DEPENDENT_BEAN, this);
    }

    @Override
    public void generate(ClassBuilder builder, Definition definition) {
        BeanDefinition beanDefinition = (BeanDefinition) definition;
        builder.getClassDeclaration().addField(beanDefinition.getClassName(), "instance", Modifier.Keyword.PRIVATE);

        builder.getGetMethodDeclaration().getBody()
                .get()
                .addAndGetStatement(generateInstanceInitializer(builder, beanDefinition));
        beanDefinition.getFieldInjectionPoints().forEach(injection -> {
            builder.getGetMethodDeclaration().getBody().get().addAndGetStatement(injection.generate());
        });
    }
}
