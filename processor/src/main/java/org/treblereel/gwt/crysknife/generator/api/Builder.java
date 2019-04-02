package org.treblereel.gwt.crysknife.generator.api;

import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/3/19
 */
public abstract class Builder {

    protected final ClassBuilder classBuilder;

    public Builder(ClassBuilder classBuilder){
        this.classBuilder = classBuilder;
    }

    abstract void build(BeanDefinition argument);
}
