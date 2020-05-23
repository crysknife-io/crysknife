package org.treblereel.gwt.crysknife.generator.dataelements;

import java.io.IOException;
import java.io.PrintWriter;

import javax.tools.JavaFileObject;

import org.treblereel.gwt.crysknife.exception.GenerationException;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/26/20
 */
public class DataElementInfoGenerator {

    private IOCContext iocContext;

    private Generator generator;

    public DataElementInfoGenerator(IOCContext iocContext) {
        this.iocContext = iocContext;
        if (iocContext.getGenerationContext().isGwt2()) {
            generator = new DataElementInfoGWT2GeneratorBuilder(iocContext);
        } else if (iocContext.getGenerationContext().isJre()) {
            generator = new DataElementInfoJREGeneratorBuilder(iocContext);
        } else {
            generator = new DataElementInfoJ2CLGeneratorBuilder(iocContext);
        }
    }

    public void generate(BeanDefinition bean) {
        try {
            if (!bean.getFieldInjectionPoints().isEmpty()) {
                generator.build(bean);
                JavaFileObject builderFile = null;
                builderFile = iocContext.getGenerationContext()
                        .getProcessingEnvironment()
                        .getFiler()
                        .createSourceFile(bean.getQualifiedName() + "DataElementInfo");
                try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
                    out.append(generator.build(bean));
                }
            }
        } catch (IOException e) {
            throw new GenerationException("Unable to generate " + bean.getType().getQualifiedName().toString() + "DataElementInfo");
        }
    }
}
