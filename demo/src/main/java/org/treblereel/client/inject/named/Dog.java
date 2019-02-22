package org.treblereel.client.inject.named;

import com.google.gwt.core.client.GWT;

import javax.inject.Named;
import javax.inject.Singleton;

@Named("dog")
@Singleton
public class Dog implements Animal {


    @Override
    public void say() {
        GWT.log("woof");
    }
}
