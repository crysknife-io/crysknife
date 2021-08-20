/*
 * Copyright © 2020 Treblereel
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

package io.crysknife.demo.client;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import elemental2.dom.HTMLDivElement;
import org.jboss.elemento.IsElement;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.Templated;
import io.crysknife.ui.navigation.client.local.Navigation;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/12/20
 */
@Singleton
@Templated(value = "main.html")
public class Main implements IsElement<HTMLDivElement> {

    @Inject
    @DataField
    private HTMLDivElement root, container;

    @Inject
    private Navigation navigation;

    @Inject
    @DataField
    @Named("span")
    private HTMLDivElement span;

    @PostConstruct
    public void init() {
        navigation.setNavigationContainer(container);
    }

    @Override
    public HTMLDivElement element() {
        return root;
    }
}
