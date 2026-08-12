/*
 * Copyright © 2025 Treblereel
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
package io.crysknife.ui.databinding.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a model class for data binding proxy generation. The annotation processor generates a
 * delegation-based proxy subclass that intercepts setter calls to propagate property changes to
 * bound UI elements and registered {@link io.crysknife.ui.databinding.api.PropertyChangeHandler}s.
 *
 * <p>Nested model classes reachable from a {@code @Bindable} class are transitively treated as
 * bindable without requiring the annotation.
 *
 * <p>Compile-time validation errors are raised for:
 * <ul>
 *   <li>{@code final} classes</li>
 *   <li>{@code final} or {@code private} setters</li>
 *   <li>Array-typed fields</li>
 *   <li>Circular references in the model object graph</li>
 * </ul>
 *
 * @author Dmitrii Tikhomirov
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Bindable {
}
