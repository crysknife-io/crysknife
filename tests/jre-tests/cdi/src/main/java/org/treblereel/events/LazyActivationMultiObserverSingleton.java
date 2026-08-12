/*
 * Copyright © 2024 Treblereel
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

import java.util.ArrayList;
import java.util.List;

import jakarta.inject.Singleton;
import jakarta.enterprise.event.Observes;

@Singleton
public class LazyActivationMultiObserverSingleton {

    public List<LazyActivationEvent> firstEvents = new ArrayList<>();
    public List<LazyActivationSecondEvent> secondEvents = new ArrayList<>();

    public void onFirstEvent(@Observes LazyActivationEvent event) {
        firstEvents.add(event);
    }

    public void onSecondEvent(@Observes LazyActivationSecondEvent event) {
        secondEvents.add(event);
    }
}
