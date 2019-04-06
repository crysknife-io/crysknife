package org.treblereel.gwt.crysknife.generator.definition;

import java.util.Objects;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/4/19
 */
public class ProducerDefinition extends BeanDefinition {

    private final ExecutableElement method;

    private final TypeElement producer;

    private ProducerDefinition(ExecutableElement method, TypeElement producer) {
        super(MoreElements.asType(MoreTypes.asElement(method.getReturnType())));
        this.method = method;
        this.producer = producer;
    }

    public static ProducerDefinition of(ExecutableElement method, TypeElement producer) {
        return new ProducerDefinition(method, producer);
    }

    @Override
    public String toString() {
        return "ProducerDefinition{" +
                " produces= " + element +
                " , producer= " + producer +
                " , method= " + method +
                " , generator = " + (generator.isPresent() ? generator.get().getClass().getSimpleName() : "") +
                '}';
    }

    public ExecutableElement getMethod() {
        return method;
    }

    public TypeElement getInstance() {
        return producer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProducerDefinition that = (ProducerDefinition) o;
        return Objects.equals(method, that.method) &&
                Objects.equals(producer, that.producer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, producer);
    }
}
