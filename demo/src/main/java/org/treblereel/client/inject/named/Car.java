package org.treblereel.client.inject.named;

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
    public String whoAmI() {
        return "Car";
    }
}
