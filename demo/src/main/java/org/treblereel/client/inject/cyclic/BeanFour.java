package org.treblereel.client.inject.cyclic;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.treblereel.gwt.crysknife.client.BeanManager;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 6/20/19
 */
@Singleton
public class BeanFour {

    //@Inject
    BeanOne beanOne;

    @Inject
    BeanManager beanManager;


    @PostConstruct
    public void init() {
        beanOne = beanManager.lookupBean(BeanOne.class).get();
        beanOne.say();
    }
}
