/*
 * Copyright © 2021 Treblereel
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

package org.treblereel;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import io.crysknife.annotation.Application;
import io.crysknife.annotation.ComponentScan;
import io.crysknife.client.ManagedInstance;
import org.treblereel.managedinstance.ComponentIface;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/27/21
 */
@Application
@ComponentScan("io.crysknife")
public class App {

    boolean initialized = false;

    @Inject
    private ManagedInstance<ComponentIface> componentIfaces;

    @PostConstruct
    public void init () {
        assert componentIfaces != null;
        initialized = true;
    }

    public ManagedInstance<ComponentIface> getComponentIfaces() {
        return componentIfaces;
    }
}
