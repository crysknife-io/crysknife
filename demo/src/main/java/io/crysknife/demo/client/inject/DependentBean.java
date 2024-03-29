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

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import elemental2.dom.DomGlobal;
import io.crysknife.demo.client.inject.named.Animal;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/22/19
 */
@Dependent
public class DependentBean {

    @Inject
    @Named("dog")
    private Animal dog;

    @Inject
    @Named("cow")
    private Animal cow;

    @Inject
    @Named("bird")
    private Animal bird;

    private int random;

    public DependentBean() {
        DomGlobal.console.log(this.getClass().getCanonicalName() + " created");
        this.random = new Random().nextInt();
    }

    public void sayHello() {
        DomGlobal.console.log("Hello");
    }

    @PostConstruct
    public void init() {

        bird.say();
        dog.say();
        cow.say();
    }

    public int getRandom() {
        return random;
    }

    @PreDestroy
    private void onDetach() {
        DomGlobal.console.log(this.getClass().getCanonicalName() + " destroyed");
    }
}
