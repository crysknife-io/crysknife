package org.treblereel.gwt.crysknife.generator.dataelements;

import java.io.IOException;

import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/26/20
 */
abstract class Generator {

    protected IOCContext iocContext;

    protected Generator(IOCContext iocContext) {
        this.iocContext = iocContext;
    }

    protected abstract String build(BeanDefinition bean) throws IOException;
}
