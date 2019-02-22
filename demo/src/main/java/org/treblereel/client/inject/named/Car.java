package org.treblereel.client.inject.named;

import com.google.gwt.core.client.GWT;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/22/19
 */
@Named("Car")
@Singleton
public class Car implements Vehicle {

    @Override
    public void whoAmI() {
        GWT.log("Car");
    }
}
