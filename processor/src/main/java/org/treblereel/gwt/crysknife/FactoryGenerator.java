package org.treblereel.gwt.crysknife;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.lang.model.element.TypeElement;

import org.treblereel.gwt.crysknife.generator.api.ClassBuilder;
import org.treblereel.gwt.crysknife.generator.context.GenerationContext;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/20/19
 */
public class FactoryGenerator {

    private final IOCContext iocContext;

    FactoryGenerator(IOCContext iocContext) {
        this.iocContext = iocContext;
    }

    void generate() {
        Set<Map.Entry<TypeElement, BeanDefinition>> beans = iocContext.getBeans()
                .entrySet()
                .stream()
                .collect(Collectors.toSet());

        for (Map.Entry<TypeElement, BeanDefinition> entry : beans) {
            new ClassBuilder(entry.getValue()).build();
        }
    }
}
