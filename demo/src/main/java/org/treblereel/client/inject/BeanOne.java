package org.treblereel.client.inject;

import com.google.gwt.core.client.GWT;
import org.treblereel.client.inject.named.Vehicle;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/19/19
 */
@Singleton
public class BeanOne {

    Vehicle car;
    Vehicle helicopter;

    @Inject
    public BeanOne(@Named("Car") Vehicle car, @Named("Helicopter") Vehicle helicopter) {
        this.helicopter = helicopter;
        this.car = car;
    }

    @PostConstruct
    public void init() {
        car.whoAmI();
        helicopter.whoAmI();
    }

    public void say() {
        GWT.log(this.getClass().getCanonicalName());
    }
}
