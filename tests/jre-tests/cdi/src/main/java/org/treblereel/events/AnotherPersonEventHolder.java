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

package org.treblereel.events;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.event.Observes;
import java.util.HashSet;
import java.util.Set;

@Dependent
public class AnotherPersonEventHolder {

    public Set<PersonEvent> events = new HashSet<>();

    public void onEvent(@Observes PersonEvent<? extends Person> event) {
        events.add(event);
    }
}
