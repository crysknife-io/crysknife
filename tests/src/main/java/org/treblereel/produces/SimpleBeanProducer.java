package org.treblereel.produces;

import java.util.Random;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/26/20
 */
public class SimpleBeanProducer {

    @Inject
    private RandomGenerator randomGenerator;

    @Produces
    @Singleton
    public SimpleBeanSingleton getSimpleBeanSingleton() {
        SimpleBeanSingleton bean = new SimpleBeanSingleton();
        bean.setFoo(this.getClass().getSimpleName());
        bean.setBar(new Random().nextInt());
        bean.setStaticValue(randomGenerator.getRandom());
        return bean;
    }

    @Produces
    @Dependent
    public SimpleBeanDependent getSimpleBeanDependent() {
        SimpleBeanDependent bean = new SimpleBeanDependent();
        bean.setFoo(this.getClass().getSimpleName());
        bean.setBar(new Random().nextInt());
        bean.setStaticValue(randomGenerator.getRandom());
        return bean;
    }
}
