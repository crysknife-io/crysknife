package org.treblereel.client.inject;

import java.util.Random;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.DomGlobal;
import org.treblereel.client.inject.named.Animal;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/22/19
 */
@Dependent
public class DependentBean {

    @Inject
    @Named("dog")
    Animal dog;

    @Inject
    @Named("cow")
    Animal cow;

    @Inject
    @Named("bird")
    Animal bird;

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
}
