package org.treblereel.client.inject;

import javax.inject.Inject;
import javax.inject.Singleton;

import elemental2.dom.DomGlobal;
import org.treblereel.client.inject.iface.IBean;

@Singleton
public class Injector {

    @Inject
    private BeanOne beanOne;

    @Inject
    private BeanTwo beanTwo;

    @Inject
    private IBean iBean;

    public void say() {
        DomGlobal.console.log(this.getClass().getCanonicalName());
        beanOne.say();
        beanTwo.say();
    }

    public String callBeanTwo() {
        return beanTwo.callBeanOne();
    }
}
