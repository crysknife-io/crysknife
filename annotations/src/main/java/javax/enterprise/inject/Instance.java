package javax.enterprise.inject;

import java.lang.annotation.Annotation;
import javax.inject.Provider;

public interface Instance<T> extends Iterable<T>, Provider<T> {
    Instance<T> select(Annotation... var1);

    <U extends T> Instance<U> select(Class<U> var1, Annotation... var2);

    boolean isUnsatisfied();

    boolean isAmbiguous();

    void destroy(T var1);
}
