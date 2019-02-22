package org.treblereel.client.inject;


import com.google.gwt.core.client.GWT;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Injector {

    @Inject
    BeanOne beanOne;

    @Inject
    BeanTwo beanTwo;

    public void say() {
        GWT.log(this.getClass().getCanonicalName());
        beanOne.say();
        beanTwo.say();
    }
}
