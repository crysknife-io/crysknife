package org.treblereel.gwt.crysknife.client;

import java.lang.annotation.Annotation;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/28/19
 */
public interface BeanManager {

    void destroyBean(Object ref);

    <T> Instance<T> lookupBean(final Class type, Class<? extends Annotation> qualifier);

    <T> Instance<T> lookupBean(final Class type);
}
