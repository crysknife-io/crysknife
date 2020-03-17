package org.treblereel.gwt.crysknife.generator;

import org.treblereel.gwt.crysknife.generator.api.ClassBuilder;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.Definition;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/2/19
 */
public abstract class IOCGenerator {

    protected final IOCContext iocContext;

    public IOCGenerator(IOCContext iocContext) {
        this.iocContext = iocContext;
    }

    public abstract void register();

    public abstract void generateBeanFactory(ClassBuilder clazz, Definition beanDefinition);

    public void before() {

    }

    public void after() {

    }

}
