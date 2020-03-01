package org.treblereel.gwt.crysknife.client;

import java.lang.annotation.Annotation;

/**
 * Fake implementation, ll be excluded on package/install
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/1/20
 */
public class BeanManagerImpl implements BeanManager {

    public static BeanManagerImpl get () {
        return null;
    }

    @Override
    public void destroyBean(Object ref) {

    }

    @Override
    public <T> Instance<T> lookupBean(Class type, Class<? extends Annotation> qualifier) {
        return null;
    }

    @Override
    public <T> Instance<T> lookupBean(Class type) {
        return null;
    }
}
