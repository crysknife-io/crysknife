package org.treblereel.client.inject;

import com.google.gwt.core.client.GWT;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/19/19
 */
@Singleton
public class BeanTwo {

    private BeanOne beanOne;
    private BeanThree beanThree;

    @Inject
    DependentBean dependentBean;

    @Inject
    public BeanTwo(BeanOne beanOne, BeanThree beanThree) {
        this.beanOne = beanOne;
        this.beanThree = beanThree;
    }


    @PostConstruct
    void init() {
        GWT.log("i am ready");
        dependentBean.sayHello();
    }

    public void say() {
        GWT.log(this.getClass().getCanonicalName());
        beanOne.say();
        beanThree.say();
    }

    public String callBeanOne(){
        return beanOne.callCar();
    }
}
