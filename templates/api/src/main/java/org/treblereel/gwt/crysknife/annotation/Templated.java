package org.treblereel.gwt.crysknife.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/7/19
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Templated {

    String value() default "";

    String stylesheet() default "";

}
