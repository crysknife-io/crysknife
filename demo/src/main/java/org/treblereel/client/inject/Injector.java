package org.treblereel.client.inject;


import com.google.gwt.core.client.GWT;
import org.treblereel.client.inject.iface.IBean;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Injector {

    @Inject
    BeanOne beanOne;

    @Inject
    BeanTwo beanTwo;

    @Inject
    IBean iBean;

    public void say() {
        GWT.log(this.getClass().getCanonicalName());
        beanOne.say();
        beanTwo.say();
        iBean.sayHello();
    }

    public String callBeanTwo(){
        return beanTwo.callBeanOne();
    }
}
