package org.treblereel.client.inject;

import com.google.gwt.core.client.GWT;

import javax.inject.Singleton;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/19/19
 */
@Singleton
public class BeanThree {

    public void say() {
        GWT.log(this.getClass().getCanonicalName());
    }
}
