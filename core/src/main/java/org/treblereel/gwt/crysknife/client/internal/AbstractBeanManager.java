package org.treblereel.gwt.crysknife.client.internal;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.Default;
import javax.inject.Provider;

import org.treblereel.gwt.crysknife.client.BeanManager;
import org.treblereel.gwt.crysknife.client.Instance;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/29/19
 */
public abstract class AbstractBeanManager implements BeanManager {

    final private Map<Class, Map<Class<? extends Annotation>, Provider>> beanStore = new java.util.HashMap<>();

    protected AbstractBeanManager() {

    }

    @Override
    public void destroyBean(Object ref) {

    }

    protected void register(Class type, Provider provider) {
        register(type, provider, Default.class);
    }

    protected void register(Class type, Provider provider, Class<? extends Annotation> annotation) {
        if(!beanStore.containsKey(type)){
            beanStore.put(type, new HashMap<>());
        }
        beanStore.get(type).put(annotation, provider);
    }

    @Override
    public <T> Instance<T> lookupBean(Class type, Class<? extends Annotation> qualifier) {
        return new InstanceImpl<T>(beanStore.get(type).get(qualifier));
    }

    @Override
    public <T> Instance<T> lookupBean(Class type) {
        return lookupBean(type, Default.class);
    }
}