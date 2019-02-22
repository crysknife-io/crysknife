package org.treblereel.client;

import com.google.gwt.core.client.GWT;
import org.treblereel.client.inject.named.Animal;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

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

    public DependentBean(){
        GWT.log(this.getClass().getCanonicalName() + " created");
    }

    public void sayHello() {
        GWT.log("Hello");
    }


    @PostConstruct
    public void init(){

        bird.say();
        dog.say();
        cow.say();

    }
}
