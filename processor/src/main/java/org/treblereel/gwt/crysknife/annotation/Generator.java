package org.treblereel.gwt.crysknife.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/2/19
 */
@Target({TYPE})
@Retention(RUNTIME)
@Documented
public @interface Generator {

    int priority() default 10000;
}
