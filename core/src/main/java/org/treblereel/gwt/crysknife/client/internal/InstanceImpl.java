package org.treblereel.gwt.crysknife.client.internal;

import javax.inject.Provider;

import org.treblereel.gwt.crysknife.client.Instance;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/29/19
 */
public class InstanceImpl<T> implements Instance<T> {

    Provider<T> provider;

    public InstanceImpl(Provider<T> provider) {
        this.provider = provider;
    }

    @Override
    public T get() {
        return provider.get();
    }

    @Override
    public void destroy(T var1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void destroyAll() {

    }
}
