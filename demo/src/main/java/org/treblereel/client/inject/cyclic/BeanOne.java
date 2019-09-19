package org.treblereel.client.inject.cyclic;

import javax.inject.Inject;
import javax.inject.Singleton;

import elemental2.dom.DomGlobal;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 6/20/19
 */
@Singleton
public class BeanOne {

    @Inject
    BeanTwo beanTwo;

    public void say(){
        DomGlobal.console.log("HELLO !");
    }

}
