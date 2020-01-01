package org.treblereel.gwt.crysknife.client;

import java.util.function.BiFunction;

import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 12/17/19
 */
public class Interceptor {


    private Proxy proxy;

    private ProxyGetInterceptor get;

    private ProxySetInterceptor set;

    public Interceptor(Object target) {
        get = new ProxyGetInterceptor(target);
        set = new ProxySetInterceptor(target);
        JsPropertyMap holder = JsPropertyMap.of();
        holder.set("get", get);
        holder.set("set", set);

        proxy = new Proxy(target, holder);
    }

    public Interceptor addGetPropertyInterceptor(String obfuscatedPropertyName, BiFunction<Object, String, Object> function) {
        get.addProperty(obfuscatedPropertyName, function);
        return this;
    }

    public Interceptor addGetMethodInterceptor(String obfuscatedPropertyName, BiFunction function) {
        get.addMethod(obfuscatedPropertyName, function);
        return this;
    }

    public <T> T getProxy() {
        return Js.uncheckedCast(proxy);
    }
}
