package org.treblereel.gwt.crysknife.client.internal;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import javax.enterprise.event.Event;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/1/19
 */
public abstract class AbstractEventHandler<T> implements Event<T> {

    private Set<Consumer<T>> subscribers = new HashSet<>();

    public void fire(T t) {
        subscribers.forEach(subscriber -> subscriber.accept(t));
    }

    public void addSubscriber(Consumer<T> subscriber) {
        subscribers.add(subscriber);
    }
}
