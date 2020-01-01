package org.treblereel.gwt.crysknife.client;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import elemental2.dom.DomGlobal;
import jsinterop.base.Js;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 12/16/19
 */
public final class ProxyGetInterceptor implements GetFN {

    private Object target;

    private Map<String, BiFunction<Object, String, Object>> propHolder = new HashMap<>();
    private Map<String, BiFunction<Object, String, Object>> methodHolder = new HashMap<>();

    public ProxyGetInterceptor(Object target) {
        this.target = target;
    }

    public void addProperty(String obfuscated, BiFunction function) {
        DomGlobal.console.debug("register property interceptor [" + obfuscated + "] for [" + target.getClass().getCanonicalName() + "]");
        propHolder.put(obfuscated, function);
    }

    public void addMethod(String obfuscated, BiFunction function) {
        DomGlobal.console.debug("register method interceptor [" + obfuscated + "] for [" + target.getClass().getCanonicalName() + "]");
        methodHolder.put(obfuscated, function);
    }

    @Override
    public Object onInvoke(Object object, String objectKey, Object receiver) {
        DomGlobal.console.debug("invoked interceptor [" + objectKey + "] for [" + target.getClass().getCanonicalName() + "]");
        if (Js.typeof(Js.asPropertyMap(object).get(objectKey)).equals("function")) {
            if (object.equals(target) && methodHolder.containsKey(objectKey)) {
                DomGlobal.console.debug("process method interceptor " + objectKey + " " + methodHolder.get(objectKey).getClass().getCanonicalName());
                return methodHolder.get(objectKey).apply(object, objectKey);
            }
        } else {
            if (object.equals(target) && propHolder.containsKey(objectKey)) {
                return propHolder.get(objectKey).apply(object, objectKey);
            }
        }
        return Js.asPropertyMap(object).get(objectKey);
    }
}
