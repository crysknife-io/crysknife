/*
 * Copyright © 2022 Treblereel
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

package io.crysknife.client.internal.logger;

import elemental2.dom.DomGlobal;
import io.crysknife.client.GwtIncompatible;

public class SimpleLogger {

  public static JRELogger get(Class clazz) {
    return new JRELogger(clazz);
  }

  private static class J2CLLogger {

    protected final Class clazz;

    private J2CLLogger(Class clazz) {
      this.clazz = clazz;
    }

    private void log(Object... var_data) {
      DomGlobal.console.log(clazz.getCanonicalName() + " " + var_data);
    }

    private void warn(Object... var_data) {
      DomGlobal.console.warn(clazz.getCanonicalName() + " " + var_data);
    }

    private void error(Object... var_data) {
      DomGlobal.console.error(clazz.getCanonicalName() + " " + var_data);
    }

    private void debug(Object... var_data) {
      DomGlobal.console.debug(clazz.getCanonicalName() + " " + var_data);
    }

  }

  private static class JRELogger extends J2CLLogger {

    private JRELogger(Class clazz) {
      super(clazz);
    }

    @GwtIncompatible
    private void log(Object... var_data) {
      System.out.println(clazz.getCanonicalName() + " " + var_data);
    }

    @GwtIncompatible
    private void warn(Object... var_data) {
      System.out.println("WARN : " + clazz.getCanonicalName() + " " + var_data);
    }

    @GwtIncompatible
    private void error(Object... var_data) {
      System.err.println(clazz.getCanonicalName() + " " + var_data);
    }

    @GwtIncompatible
    private void debug(Object... var_data) {
      System.err.println("DEBUG : " + clazz.getCanonicalName() + " " + var_data);
    }

  }
}
