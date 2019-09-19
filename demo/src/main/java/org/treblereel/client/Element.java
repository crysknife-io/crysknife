package org.treblereel.client;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.gwtproject.dom.client.Node;

@JsType(isNative = true, name="Object", namespace = JsPackage.GLOBAL)
public class Element extends Node {

    public static class UserAgentHolder {
        public static final String user_agent = System.getProperty("user.agent");
    }

}
