package org.treblereel.gwt.crysknife.client;

import jsinterop.annotations.JsMethod;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 12/16/19
 */
public class Reflect {

    @JsMethod(namespace = "goog.reflect")
    public native static String objectProperty(String property, Object object);

    @JsMethod(namespace = "goog.reflect")
    public native static String sinkValue(String property);

}
