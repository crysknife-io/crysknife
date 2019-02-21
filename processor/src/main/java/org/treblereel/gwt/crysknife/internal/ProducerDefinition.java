package org.treblereel.gwt.crysknife.internal;

import javax.lang.model.element.TypeElement;

import static org.treblereel.gwt.crysknife.internal.Utils.getQualifiedName;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/21/19
 */
public class ProducerDefinition {

    private final TypeElement element;
    private final TypeElement producer;
    private final String method;

    public ProducerDefinition(TypeElement element, TypeElement producer, String method){
        this.element = element;
        this.producer = producer;
        this.method = method;
    }

    public TypeElement getElement() {
        return element;
    }

    @Override
    public String toString() {
        return "ProducerDefinition{" +
                "element=" + getQualifiedName(element) +
                ", producer=" + getQualifiedName(producer) +
                ", method='" + method + '\'' +
                '}';
    }

    public TypeElement getProducer() {
        return producer;
    }

    public String getMethod() {
        return method;
    }
}
