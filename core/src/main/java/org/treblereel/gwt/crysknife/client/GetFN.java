package org.treblereel.gwt.crysknife.client;

import jsinterop.annotations.JsFunction;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 12/16/19
 */
@FunctionalInterface
@JsFunction
public interface GetFN {
    Object onInvoke(Object object, String objectKey, Object receiver);
}
