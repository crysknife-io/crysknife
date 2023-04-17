package org.treblereel.events;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Singleton;
import java.util.HashSet;
import java.util.Set;

@Singleton
public class AnotherPersonSingletonEventHolder {

    public Set<PersonEvent> events = new HashSet<>();

    public void onEvent(@Observes PersonEvent<? extends Person> event) {
        events.add(event);
    }
}
