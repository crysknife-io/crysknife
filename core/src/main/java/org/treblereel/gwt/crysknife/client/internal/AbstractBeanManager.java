package org.treblereel.gwt.crysknife.client.internal;

import java.lang.annotation.Annotation;
import java.util.Map;

import javax.inject.Provider;

import org.treblereel.gwt.crysknife.client.BeanManager;
import org.treblereel.gwt.crysknife.client.Instance;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/29/19
 */
public class AbstractBeanManager implements BeanManager {

    final protected Map<String, Provider> beanStore = new java.util.HashMap<>();

    protected AbstractBeanManager() {

    }

    @Override
    public void destroyBean(Object ref) {

    }

    @Override
    public <T> Instance<T> lookupBean(String type, Annotation... qualifiers) {
        return null;
    }

    @Override
    public <T> Instance<T> lookupBean(String type) {
        return new InstanceImpl<T>(beanStore.get(type));
    }
}
