package org.treblereel.gwt.crysknife.generator;

import org.treblereel.gwt.crysknife.generator.api.ClassBuilder;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.Definition;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/2/19
 */
public abstract class IOCGenerator {

    public abstract void register(IOCContext iocContext);

    public abstract void generate(ClassBuilder clazz, Definition beanDefinition);
}
