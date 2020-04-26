package org.treblereel.injection.singleton;

import java.util.Random;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/14/19
 */
@Singleton
public class SingletonBean {

    private int random;

    public String say() {
        return this.getClass().getCanonicalName();
    }

    @PostConstruct
    public void init() {
        random = new Random().nextInt();
    }

    public int getRandom() {
        return random;
    }
}
