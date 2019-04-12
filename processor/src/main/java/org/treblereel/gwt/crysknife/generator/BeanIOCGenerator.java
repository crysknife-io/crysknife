package org.treblereel.gwt.crysknife.generator;

import org.treblereel.gwt.crysknife.generator.api.ClassBuilder;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;
import org.treblereel.gwt.crysknife.generator.definition.Definition;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/4/19
 */
public abstract class BeanIOCGenerator extends IOCGenerator {

    protected IOCContext iocContext;

    public abstract void generate(ClassBuilder builder, Definition definition);


        //Calling from BeanDefinition
    public abstract void addFactoryFieldDeclaration(ClassBuilder classBuilder, BeanDefinition beanDefinition);


    //
    //Calling from BeanDefinition
    public abstract String getFactoryVariableName();

    //Calling from BeanDefinition
    public abstract void addFactoryFieldInitialization(ClassBuilder classBuilder, BeanDefinition beanDefinition);


}