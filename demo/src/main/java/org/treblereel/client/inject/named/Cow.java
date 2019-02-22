package org.treblereel.client.inject.named;

import com.google.gwt.core.client.GWT;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/21/19
 */
@Named("cow")
@Singleton
public class Cow implements Animal {


    @Override
    public String say() {
        return "moo";
    }
}
