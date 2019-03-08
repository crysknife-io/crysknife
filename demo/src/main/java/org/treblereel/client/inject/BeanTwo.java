package org.treblereel.client.inject;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import elemental2.dom.DomGlobal;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/19/19
 */
@Singleton
public class BeanTwo {

    @Inject
    DependentBean dependentBean;
    private BeanOne beanOne;
    private BeanThree beanThree;

    @Inject
    public BeanTwo(BeanOne beanOne, BeanThree beanThree) {
        this.beanOne = beanOne;
        this.beanThree = beanThree;
    }

    @PostConstruct
    void init() {
        DomGlobal.console.log("i am ready");
        dependentBean.sayHello();
    }

    public void say() {
        DomGlobal.console.log(this.getClass().getCanonicalName());
        beanOne.say();
        beanThree.say();
    }

    public String callBeanOne() {
        return beanOne.callCar();
    }
}
