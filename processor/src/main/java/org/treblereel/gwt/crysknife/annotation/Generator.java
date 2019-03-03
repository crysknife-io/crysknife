package org.treblereel.gwt.crysknife.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/2/19
 */
@Target({ TYPE})
@Retention(RUNTIME)
@Documented
public @interface Generator {



}
