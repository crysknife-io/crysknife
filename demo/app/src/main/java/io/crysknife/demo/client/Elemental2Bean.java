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

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLHeadingElement;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/22/19
 */
@Singleton
public class Elemental2Bean {

    @Inject
    private HTMLButtonElement buttonElement;

    @Inject
    @Named("h5")
    private HTMLHeadingElement headingElement;

    @PostConstruct
    public void init() {
        buttonElement.textContent = "@Produces test -> HTMLButtonElement";
        buttonElement.className = "btn btn-default";
        buttonElement.addEventListener("click", evt -> DomGlobal.alert("HTMLButtonElement pressed"));

        DomGlobal.document.body.appendChild(buttonElement);
    }
}
