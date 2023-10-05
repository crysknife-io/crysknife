package org.treblereel.lifecycle.dependent;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class LCSingletonFiendInjectionBean {

    @Inject
    private LCDependentBeanOne one;

    @Inject
    private LCDependentBeanTwo two;

    @PostConstruct
    private void init() {
        System.err.println(one.getClass().getCanonicalName());
        System.err.println(two.getClass().getCanonicalName());
    }

    @PreDestroy
    private void destroy() {
        LCDependentBeanTrap.CLASSES.add(getClass());
        this.one = null;
        this.two = null;
    }
}
