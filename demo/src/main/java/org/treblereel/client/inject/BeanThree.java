package org.treblereel.client.inject;

import javax.inject.Singleton;

import elemental2.dom.DomGlobal;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/19/19
 */
@Singleton
public class BeanThree {

    public void say() {
        DomGlobal.console.log(this.getClass().getCanonicalName());
    }
}
