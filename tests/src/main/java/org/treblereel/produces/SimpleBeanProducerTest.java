package org.treblereel.produces;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/26/20
 */
@Singleton
public class SimpleBeanProducerTest {

    @Inject
    private SimpleBeanSingleton simpleBeanSingletonOne;
    @Inject
    private SimpleBeanSingleton simpleBeanSingletonTwo;
    @Inject
    private SimpleBeanDependent simpleBeanDependentOne;
    @Inject
    private SimpleBeanDependent simpleBeanDependentTwo;

    public SimpleBeanSingleton getSimpleBeanSingletonOne() {
        return simpleBeanSingletonOne;
    }

    public SimpleBeanSingleton getSimpleBeanSingletonTwo() {
        return simpleBeanSingletonTwo;
    }

    public SimpleBeanDependent getSimpleBeanDependentOne() {
        return simpleBeanDependentOne;
    }

    public SimpleBeanDependent getSimpleBeanDependentTwo() {
        return simpleBeanDependentTwo;
    }
}
