package org.treblereel.client.inject.iface;

import javax.inject.Singleton;

import com.google.gwt.core.client.GWT;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/3/19
 */
@Singleton
public class IBeanImpl implements IBean{

    @Override
    public void sayHello() {
        GWT.log("hello " + this.getClass().getCanonicalName());
    }
}
