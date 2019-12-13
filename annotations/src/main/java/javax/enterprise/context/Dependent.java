package javax.enterprise.context;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.inject.Scope;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ElementType.METHOD, ElementType.TYPE, ElementType.FIELD})
@Retention(RUNTIME)
@Documented
@Scope
@Inherited
public @interface Dependent {
}