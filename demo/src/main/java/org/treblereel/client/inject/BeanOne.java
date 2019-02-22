package org.treblereel.client.inject;

import com.google.gwt.core.client.GWT;
import org.treblereel.client.inject.named.Vehicle;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Random;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/19/19
 */
@Singleton
public class BeanOne {

    Vehicle car;
    Vehicle helicopter;

    private int random;

    @Inject
    public BeanOne(@Named("Car") Vehicle car, @Named("Helicopter") Vehicle helicopter) {
        this.helicopter = helicopter;
        this.car = car;
        this.random = new Random().nextInt();
    }

    @PostConstruct
    public void init() {
        car.whoAmI();
        helicopter.whoAmI();
    }

    public void say() {
        GWT.log(this.getClass().getCanonicalName());
    }

    public int getRandom() {
        return random;
    }

    public String callCar(){
      return car.whoAmI();
    };
}
