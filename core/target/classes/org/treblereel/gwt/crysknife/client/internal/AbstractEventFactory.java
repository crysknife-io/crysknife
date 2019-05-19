package org.treblereel.gwt.crysknife.client.internal;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.event.Event;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/4/19
 */
public abstract class AbstractEventFactory {

    protected Map<Class, EventHolder> holder = new HashMap<>();

    public <T> Event<T> get(Class type) {
        if (!holder.containsKey(type)) {
            holder.put(type, new EventHolder(type));
        }

        return holder.get(type);
    }

    class EventHolder extends AbstractEventHandler {

        private Class type;

        EventHolder(Class type) {
            this.type = type;
        }
    }
}
