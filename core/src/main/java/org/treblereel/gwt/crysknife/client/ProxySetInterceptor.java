package org.treblereel.gwt.crysknife.client;

import elemental2.dom.DomGlobal;
import jsinterop.base.Js;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 12/18/19
 */
public final class ProxySetInterceptor implements SetFN {

    private Object target;

    public ProxySetInterceptor(Object target) {
        this.target = target;
    }

    @Override
    public boolean onInvoke(Object object, String objectKey, Object value) {
        DomGlobal.console.debug("invoked set [" + value + "] +interceptor [" + objectKey + "] for [" + target.getClass().getCanonicalName() + "]");
        Js.asPropertyMap(object).set(objectKey, value);
        return true;
    }
}
