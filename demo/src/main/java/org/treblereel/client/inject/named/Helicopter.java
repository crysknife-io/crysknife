package org.treblereel.client.inject.named;

import com.google.gwt.core.client.GWT;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/22/19
 */
@Named("Helicopter")
@Singleton
public class Helicopter implements Vehicle {

    @Override
    public String whoAmI() {
        return "Helicopter";
    }
}
