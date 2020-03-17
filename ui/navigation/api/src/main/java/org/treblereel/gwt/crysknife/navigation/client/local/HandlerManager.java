package org.treblereel.gwt.crysknife.navigation.client.local;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.gwtproject.event.shared.Event;
import org.gwtproject.event.shared.EventBus;
import org.gwtproject.event.shared.HandlerRegistration;
import org.gwtproject.event.shared.HasHandlers;
import org.gwtproject.event.shared.UmbrellaException;

/**
 * Manager responsible for adding handlers to event sources and firing those
 * handlers on passed in events. Primitive ancestor of {@link EventBus}.
 */
public class HandlerManager implements HasHandlers {

    private final Bus eventBus;
    // source of the events
    private final Object source;

    /**
     * Creates a handler manager with a source to be set on all events fired via
     * {@link #fireEvent(Event)}. Handlers will be fired in the order that they
     * are added.
     * @param source the default event source
     */
    public HandlerManager(Object source) {
        this(source, false);
    }

    /**
     * Creates a handler manager with the given source, specifying the order in
     * which handlers are fired.
     * @param source the event source
     * @param fireInReverseOrder true to fire handlers in reverse order
     */
    public HandlerManager(Object source, boolean fireInReverseOrder) {
        eventBus = new Bus(fireInReverseOrder);
        this.source = source;
    }

    /**
     * Adds a handler.
     * @param <H> The type of handler
     * @param type the event type associated with this handler
     * @param handler the handler
     * @return the handler registration, can be stored in order to remove the
     * handler later
     */
    public <H> HandlerRegistration addHandler(Event.Type<H> type, H handler) {
        return eventBus.addHandler(type, handler);
    }

    /**
     * Fires the given event to the handlers listening to the event's type.
     * <p>
     * Any exceptions thrown by handlers will be bundled into a
     * {@link UmbrellaException} and then re-thrown after all handlers have
     * completed. An exception thrown by a handler will not prevent other handlers
     * from executing.
     * <p>
     * Note, any subclass should be very careful about overriding this method, as
     * adds/removes of handlers will not be safe except within this
     * implementation.
     * @param event the event
     */
    @Override
    public void fireEvent(Event<?> event) {
        try {
            // May throw an UmbrellaException.
            eventBus.fireEventFromSource(event, source);
        } catch (UmbrellaException e) {
            throw new UmbrellaException(e.getCauses());
        }
    }

    /**
     * Gets the number of handlers listening to the event type.
     * @param type the event type
     * @return the number of registered handlers
     * @deprecated
     */
    @Deprecated // Removal: gwt-widgets:2.0
    public int getHandlerCount(Event.Type<?> type) {
        return eventBus.getHandlerCount(type);
    }

    /**
     * Does this handler manager handle the given event type?
     * @param e the event type
     * @return whether the given event type is handled
     * @deprecated
     */
    @Deprecated // Removal: gwt-widgets:2.0
    public boolean isEventHandled(Event.Type<?> e) {
        return eventBus.isEventHandled(e);
    }

    private static class Bus extends EventBus {

        /**
         * Map of event type to map of event source to list of their handlers.
         */
        private final Map<Event.Type<?>, Map<Object, List<?>>> map = new HashMap<>();
        private final boolean isReverseOrder;
        private int firingDepth = 0;
        /**
         * Add and remove operations received during dispatch.
         */
        private List<Command> deferredDeltas;

        private Bus(boolean fireInReverseOrder) {
            this.isReverseOrder = fireInReverseOrder;
        }

        @Override
        public <H> HandlerRegistration addHandler(Event.Type<H> type, H handler) {
            return doAdd(type, null, handler);
        }

        @Override
        public <H> HandlerRegistration addHandlerToSource(
                final Event.Type<H> type, final Object source, final H handler) {
            if (source == null) {
                throw new NullPointerException("Cannot add a handler with a null source");
            }

            return doAdd(type, source, handler);
        }

        @Override
        public void fireEvent(Event<?> event) {
            doFire(event, null);
        }

        @Override
        public void fireEventFromSource(Event<?> event, Object source) {
            doFire(event, source);
        }

        protected <H> void doRemove(Event.Type<H> type, Object source, H handler) {
            if (firingDepth > 0) {
                enqueueRemove(type, source, handler);
            } else {
                doRemoveNow(type, source, handler);
            }
        }

        private void defer(Command command) {
            if (deferredDeltas == null) {
                deferredDeltas = new ArrayList<>();
            }
            deferredDeltas.add(command);
        }

        private <H> HandlerRegistration doAdd(
                final Event.Type<H> type, final Object source, final H handler) {
            if (type == null) {
                throw new NullPointerException("Cannot add a handler with a null type");
            }
            if (handler == null) {
                throw new NullPointerException("Cannot add a null handler");
            }

            if (firingDepth > 0) {
                enqueueAdd(type, source, handler);
            } else {
                doAddNow(type, source, handler);
            }

            return () -> doRemove(type, source, handler);
        }

        private <H> void doAddNow(Event.Type<H> type, Object source, H handler) {
            List<H> l = ensureHandlerList(type, source);
            l.add(handler);
        }

        private <H> void doFire(Event<H> event, Object source) {
            if (event == null) {
                throw new NullPointerException("Cannot fire null event");
            }
            try {
                firingDepth++;

                if (source != null) {
                    setSourceOfEvent(event, source);
                }

                List<H> handlers = getDispatchList(event.getAssociatedType(), source);
                Set<Throwable> causes = null;

                ListIterator<H> it =
                        isReverseOrder ? handlers.listIterator(handlers.size()) : handlers.listIterator();
                while (isReverseOrder ? it.hasPrevious() : it.hasNext()) {
                    H handler = isReverseOrder ? it.previous() : it.next();
                    try {
                        dispatchEvent(event, handler);
                    } catch (Throwable e) {
                        if (causes == null) {
                            causes = new HashSet<>();
                        }
                        causes.add(e);
                    }
                }

                if (causes != null) {
                    throw new UmbrellaException(causes);
                }
            } finally {
                firingDepth--;
                if (firingDepth == 0) {
                    handleQueuedAddsAndRemoves();
                }
            }
        }

        private <H> void doRemoveNow(Event.Type<H> type, Object source, H handler) {
            List<H> l = getHandlerList(type, source);

            boolean removed = l.remove(handler);

            if (removed && l.isEmpty()) {
                prune(type, source);
            }
        }

        private <H> void enqueueAdd(final Event.Type<H> type, final Object source, final H handler) {
            defer(() -> doAddNow(type, source, handler));
        }

        private <H> void enqueueRemove(final Event.Type<H> type, final Object source, final H handler) {
            defer(() -> doRemoveNow(type, source, handler));
        }

        private <H> List<H> ensureHandlerList(Event.Type<H> type, Object source) {
            Map<Object, List<?>> sourceMap = map.computeIfAbsent(type, k -> new HashMap<>());

            // safe, we control the puts.
            @SuppressWarnings("unchecked")
            List<H> handlers = (List<H>) sourceMap.get(source);
            if (handlers == null) {
                handlers = new ArrayList<>();
                sourceMap.put(source, handlers);
            }

            return handlers;
        }

        private <H> List<H> getDispatchList(Event.Type<H> type, Object source) {
            List<H> directHandlers = getHandlerList(type, source);
            if (source == null) {
                return directHandlers;
            }

            List<H> globalHandlers = getHandlerList(type, null);

            List<H> rtn = new ArrayList<>(directHandlers);
            rtn.addAll(globalHandlers);
            return rtn;
        }

        private <H> List<H> getHandlerList(Event.Type<H> type, Object source) {
            Map<Object, List<?>> sourceMap = map.get(type);
            if (sourceMap == null) {
                return Collections.emptyList();
            }

            // safe, we control the puts.
            @SuppressWarnings("unchecked")
            List<H> handlers = (List<H>) sourceMap.get(source);
            if (handlers == null) {
                return Collections.emptyList();
            }

            return handlers;
        }

        private void handleQueuedAddsAndRemoves() {
            if (deferredDeltas != null) {
                try {
                    for (Command c : deferredDeltas) {
                        c.execute();
                    }
                } finally {
                    deferredDeltas = null;
                }
            }
        }

        private void prune(Event.Type<?> type, Object source) {
            Map<Object, List<?>> sourceMap = map.get(type);

            List<?> pruned = sourceMap.remove(source);

            assert pruned != null : "Can't prune what wasn't there";
            assert pruned.isEmpty() : "Pruned unempty list!";

            if (sourceMap.isEmpty()) {
                map.remove(type);
            }
        }

        protected <H> H getHandler(Event.Type<H> type, int index) {
            assert index < getHandlerCount(type) : "handlers for " + type.getClass() + " have size: "
                    + getHandlerCount(type) + " so do not have a handler at index: " + index;

            List<H> l = getHandlerList(type, null);
            return l.get(index);
        }

        protected int getHandlerCount(Event.Type<?> type) {
            return getHandlerList(type, null).size();
        }

        protected boolean isEventHandled(Event.Type<?> type) {
            return map.containsKey(type);
        }

        private interface Command {

            void execute();
        }
    }
}
