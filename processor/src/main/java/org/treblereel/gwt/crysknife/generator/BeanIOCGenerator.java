package org.treblereel.gwt.crysknife.generator;

import com.github.javaparser.ast.expr.Expression;
import org.treblereel.gwt.crysknife.generator.api.ClassBuilder;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;
import org.treblereel.gwt.crysknife.generator.point.FieldPoint;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/4/19
 */
public abstract class BeanIOCGenerator extends IOCGenerator {

    protected IOCContext iocContext;

    /**
     * @param clazz
     * @param fieldPoint
     * @param beanDefinition
     * @return Expression, how to call instance of this bean ?
     */
    public abstract Expression generateBeanCall(ClassBuilder clazz, FieldPoint fieldPoint, BeanDefinition beanDefinition);
}