package org.treblereel.client.inject.iface;

import javax.inject.Singleton;

import elemental2.dom.DomGlobal;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/3/19
 */
@Singleton
public class IBeanImpl implements IBean {

    @Override
    public void sayHello() {
        DomGlobal.console.log("hello " + this.getClass().getCanonicalName());
    }
}
