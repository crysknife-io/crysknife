package org.treblereel.events;

import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;


@Singleton
public class Check {

    @Inject
    Event<SimpleEvent> simpleEventEvent;
}
