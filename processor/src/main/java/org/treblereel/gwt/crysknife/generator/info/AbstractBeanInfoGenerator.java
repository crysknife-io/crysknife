package org.treblereel.gwt.crysknife.generator.info;

import java.io.IOException;

import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/26/20
 */
public abstract class AbstractBeanInfoGenerator {

    protected IOCContext iocContext;

    protected AbstractBeanInfoGenerator(IOCContext iocContext) {
        this.iocContext = iocContext;
    }

    protected abstract String build(BeanDefinition bean) throws IOException;
}
