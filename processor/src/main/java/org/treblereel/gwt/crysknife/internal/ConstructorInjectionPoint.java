package org.treblereel.gwt.crysknife.internal;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/19/19
 */
public class ConstructorInjectionPoint extends InjectionPoint {

    private final List<Arg> params = new LinkedList<>();

    public ConstructorInjectionPoint(TypeElement parent, ElementKind type, String name) {
        super(parent, type, name);
    }

    public void addParam(String name, DeclaredType declared) {
        params.add(new Arg(name, declared));
    }

    public List<Arg> getParameters(){
        return params;
    }

    @Override
    public String toString() {
        return "ConstructorInjectionPoint{" +
                "params=" + params.stream().map(m -> m.toString()).collect(Collectors.joining(", ")) +
                '}';
    }

    public static class Arg {
        private final String name;
        private final DeclaredType declared;

        public Arg(String name, DeclaredType declared){
            this.name = name;
            this.declared = declared;
        }

        public String getName() {
            return name;
        }

        public DeclaredType getDeclared() {
            return declared;
        }

        @Override
        public String toString() {
            return "Arg{" +
                    "name='" + name + '\'' +
                    ", declared=" + declared.asElement().getSimpleName() +
                    '}';
        }
    }
}
