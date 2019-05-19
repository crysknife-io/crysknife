package org.treblereel.gwt.crysknife.client.injection.singleton;

import org.treblereel.gwt.crysknife.client.internal.Factory;
import javax.inject.Provider;
import org.treblereel.gwt.crysknife.client.injection.singleton.SingletonBean;

public class SingletonBean_Factory implements Factory<SingletonBean> {

    @Override()
    public SingletonBean get() {
        if (this.instance == null)
            this.instance = new SingletonBean();
        return this.instance;
    }

    private SingletonBean instance;

    private SingletonBean_Factory() {
    }

    public static SingletonBean_Factory create() {
        return new SingletonBean_Factory();
    }
}
