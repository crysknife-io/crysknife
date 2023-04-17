package org.treblereel.events;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class TesterWithSingletonEventListener {

    public AnotherPersonSingletonEventHolder holder;

    @Inject
    public TesterWithSingletonEventListener(AnotherPersonSingletonEventHolder holder) {
        this.holder = holder;
    }

    public void destroy() {
        this.holder = null;
    }
}
