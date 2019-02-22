package org.treblereel.client.inject.named;

import javax.inject.Named;
import javax.inject.Singleton;

@Named("dog")
@Singleton
public class Dog implements Animal {


    @Override
    public String say() {
        return "woof";
    }
}
