package org.treblereel.gwt.crysknife.internal;

import com.google.auto.common.MoreElements;

import javax.annotation.PostConstruct;
import javax.lang.model.element.Element;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/20/19
 */
public class BeanDefinition {

    private Set<FieldInjectionPoint> fieldInjectionPoints = new HashSet<>();

    private ConstructorInjectionPoint constructorInjectionPoint;

    private String postConstract;

    private Element element;

    public BeanDefinition() {

    }

    public BeanDefinition(Element element) {
        this.element = element;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Utils.getQualifiedName(element));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BeanDefinition that = (BeanDefinition) o;
        return Objects.equals(Utils.getQualifiedName(element), Utils.getQualifiedName(that.element));
    }

    @Override
    public String toString() {
        return "BeanDefinition{" +
                "element=" + Utils.getQualifiedName(element) +
                ", fields=" + getFieldInjectionPoints().stream().map(m -> m.toString()).collect(Collectors.joining(", ")) +
                (getConstructorInjectionPoint() == null ? "" : ", constructor=" + getConstructorInjectionPoint().toString()) +
                (getPostConstract() == null ? "" : ", postConstract=" + getPostConstract()) +
                '}';
    }

    public Set<FieldInjectionPoint> getFieldInjectionPoints() {
        return fieldInjectionPoints;
    }

    public void setFieldInjectionPoints(Set<FieldInjectionPoint> fieldInjectionPoints) {
        this.fieldInjectionPoints = fieldInjectionPoints;
    }

    public ConstructorInjectionPoint getConstructorInjectionPoint() {
        return constructorInjectionPoint;
    }

    public void setConstructorInjectionPoint(ConstructorInjectionPoint constructorInjectionPoint) {
        this.constructorInjectionPoint = constructorInjectionPoint;
    }

    public Element getElement() {
        return element;
    }

    public String getPostConstract() {
        return postConstract;
    }

    public void setPostConstract(String postConstract) {
        this.postConstract = postConstract;
    }
}
