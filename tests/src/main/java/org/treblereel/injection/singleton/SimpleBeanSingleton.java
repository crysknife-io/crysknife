package org.treblereel.injection.singleton;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/26/20
 */
@Singleton
public class SimpleBeanSingleton {

    private String postConstruct;

    public String getName() {
        return this.getClass().getSimpleName();
    }

    @PostConstruct
    public void init() {
        postConstruct = "done";
    }

    public String getPostConstruct() {
        return postConstruct;
    }
}
