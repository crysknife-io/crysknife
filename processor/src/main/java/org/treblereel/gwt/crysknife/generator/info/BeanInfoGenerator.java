package org.treblereel.gwt.crysknife.generator.info;

import java.io.IOException;
import java.io.PrintWriter;

import javax.tools.JavaFileObject;

import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/26/20
 */
public class BeanInfoGenerator {

    private IOCContext iocContext;

    private AbstractBeanInfoGenerator generator;

    public BeanInfoGenerator(IOCContext iocContext) {
        this.iocContext = iocContext;
        if (iocContext.getGenerationContext().isGwt2()) {
            generator = new BeanInfoGWT2GeneratorBuilder(iocContext);
        } else if (iocContext.getGenerationContext().isJre()) {
            generator = new BeanInfoJREGeneratorBuilder(iocContext);
        } else {
            generator = new BeanInfoJ2CLGeneratorBuilder(iocContext);
        }
    }

    public void generate() {
        iocContext.getBeans().forEach((k, bean) -> {
            try {
                generate(bean);
            } catch (IOException e) {
                throw new Error(e);
            }
        });
    }

    private void generate(BeanDefinition bean) throws IOException {
        if (!bean.getFieldInjectionPoints().isEmpty()) {
            JavaFileObject builderFile = iocContext.getGenerationContext()
                    .getProcessingEnvironment()
                    .getFiler()
                    .createSourceFile(bean.getQualifiedName() + "Info");
            try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
                out.append(generator.build(bean));
            }
        }
    }
}
