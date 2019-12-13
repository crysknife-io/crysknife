package org.jboss.gwt.elemento.processor.context;

import java.util.Arrays;

import javax.lang.model.type.TypeMirror;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 6/2/19
 */
public class EventHandlerInfo {

    private DataElementInfo info;

    private String[] events;

    private String methodName;

    private String eventType;

    public EventHandlerInfo(DataElementInfo info, String[] events, String methodName, String eventType) {

        this.info = info;
        this.events = events;
        this.methodName = methodName;
        this.eventType = eventType;
    }

    public DataElementInfo getInfo() {
        return info;
    }

    public String[] getEvents() {
        return events;
    }

    @Override
    public String toString() {
        return "EventHandlerInfo{" +
                "info=" + info +
                ", events=" + Arrays.toString(events) +
                ", methodName=" + methodName +
                '}';
    }

    public String getMethodName() {
        return methodName;
    }

    public String getEventType() {
        return eventType;
    }
}
