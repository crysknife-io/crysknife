package org.treblereel.gwt.crysknife.client;

import jsinterop.annotations.JsFunction;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 12/18/19
 */
@FunctionalInterface
@JsFunction
public interface SetFN {
    boolean onInvoke(Object object, String objectKey, Object value);
}
