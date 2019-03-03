package org.treblereel.gwt.crysknife.generator.definition;

import java.util.Objects;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import org.treblereel.gwt.crysknife.util.Utils;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/4/19
 */
public class ProducerDefinition extends BeanDefinition {

    private final ExecutableElement executableElement;

    private final TypeElement enclosingElement;

    private final TypeElement produces;


    private ProducerDefinition(ExecutableElement executableElement, TypeElement enclosingElement) {
        super(enclosingElement);
        this.executableElement = executableElement;
        this.enclosingElement = enclosingElement;
        this.produces = MoreElements.asType(MoreTypes.asElement(executableElement.getReturnType()));
    }

    public static ProducerDefinition of(ExecutableElement executableElement, TypeElement enclosingElement) {
        return new ProducerDefinition(executableElement, enclosingElement);
    }

    @Override
    public String getQualifiedName() {
        return Utils.getQualifiedName(produces);
    }

    @Override
    public String getClassFactoryName() {
        return Utils.getFactoryClassName(produces);
    }

    @Override
    public String getClassName() {
        return Utils.getQualifiedName(produces);
    }

    @Override
    public String getPackageName() {
        return MoreElements.getPackage(produces).getQualifiedName().toString();
    }

    @Override
    public String toString() {
        return "ProducerDefinition{" +
                "producer=" + enclosingElement +
                '}';
    }

    public ExecutableElement getMethod() {
        return executableElement;
    }

    public TypeElement getInstance() {
        return enclosingElement;
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
        return Objects.equals(executableElement, that.executableElement) &&
                Objects.equals(enclosingElement, that.enclosingElement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(executableElement, enclosingElement);
    }
}
