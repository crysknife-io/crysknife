package org.treblereel.gwt.crysknife.client.internal;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import elemental2.dom.DomGlobal;
import jsinterop.base.Js;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 1/1/20
 */
public final class OnFieldAccessed implements BiFunction<Object, String, Object> {

    private final Supplier supplier;

    public OnFieldAccessed(Supplier supplier) {
        this.supplier = supplier;
    }

    @Override
    public Object apply(Object o, String propertyKey) {
        if (Js.asPropertyMap(o).get(propertyKey) == null) {
            DomGlobal.console.log("propertyKey null, set value ...");
            Js.asPropertyMap(o).set(propertyKey, supplier.get());
        }
        return Js.asPropertyMap(o).get(propertyKey);
    }
}
