/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.treblereel.gwt.crysknife.navigation.client.local.pushstate;

import elemental2.dom.DomGlobal;
import jsinterop.base.Js;

/**
 * @author Max Barkley <mbarkley@redhat.com>
 * @author Divya Dadlani <ddadlani@redhat.com>
 * @author Dmitrii Tikhomirov <dtikhomi@redhat.com>
 */
public class PushStateUtil {

    /**
     * @return True if Errai Pushstate has not been disabled and
     * the browser supports PushState.
     */
    public static boolean isPushStateActivated() {
        return (isPushStateEnabled() && isPushStateSupported());
    }

    private static boolean isPushStateEnabled() {
        return Js.asPropertyMap(DomGlobal.window).has("erraiPushStateEnabled");
    }

    private static boolean isPushStateSupported() {
        return Js.asPropertyMap(DomGlobal.window.history).has("pushState");
    }

    public static void enablePushState(boolean enabled) {
        Js.asPropertyMap(DomGlobal.window).set("erraiPushStateEnabled", "enabled");
    }
}
