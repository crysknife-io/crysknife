/*
 * Copyright 2012 Johannes Barop
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.treblereel.gwt.crysknife.navigation.client.local.pushstate;

import java.util.Arrays;

import elemental2.dom.DomGlobal;

/**
 * Utility class with methods to extract and add the <code>gwt.codesvr</code> to the URL.
 *
 * <p>
 * This is needed so that we not leave the development mode after pushing a history state.
 * </p>
 * @author <a href="mailto:jb@barop.de">Johannes Barop</a>
 */
public final class CodeServerParameterHelper {

    /**
     * Hidden constructor as this class is not meant to be instantiated.
     */
    private CodeServerParameterHelper() {
    }

    /**
     * Append the <code>gwt.codesvr</code> parameter to the token when needed.
     */
    public static String append(final String token) {
        String result = token;

        /*
         * This gets compiled out in production mode!
         */
        String gwtCodesvr = getQueryParam("gwt.codesvr");
        if (gwtCodesvr != null) {
            if (token.contains("?")) {
                result += "&";
            } else {
                result += "?";
            }
            result += "gwt.codesvr=" + gwtCodesvr;
        }

        return result;
    }

    private static String getQueryParam(String param) {
        String found = null;
        for (String item : DomGlobal.window.location.search.substring(1).split("&")) {
            if (param == item.split("=")[0]) {
                found = item.split("=")[1];
            }
        }
        return found;
    };

    /**
     * Removes the <code>gwt.codesvr</code> parameter from the given string.
     */
    public static String remove(final String queryString) {
        String result = queryString;

        /*
         * This gets compiled out in production mode!
         */
        if (queryString != null) {
            StringBuilder builder = new StringBuilder();

            String separator = "";
            for (String keyValue : queryString.split("&")) {
                if (keyValue.startsWith("?")) {
                    keyValue = keyValue.substring(1);
                }
                if (!keyValue.matches("gwt\\.codesvr=.*")) {
                    builder.append(separator + keyValue.trim());
                    separator = "&";
                }
            }
            result = builder.toString();
        }

        return result;
    }
}