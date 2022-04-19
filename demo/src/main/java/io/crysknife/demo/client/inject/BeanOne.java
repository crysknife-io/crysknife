/*
 * Copyright © 2020 Treblereel
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package io.crysknife.demo.client.inject;

import java.util.Random;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import elemental2.dom.DomGlobal;
import io.crysknife.demo.client.inject.named.Vehicle;

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
        DomGlobal.console.log(this.getClass().getCanonicalName());
    }

    public int getRandom() {
        return random;
    }

    public String callCar() {
        return car.whoAmI();
    }

}
