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

import io.crysknife.ui.databinding.api.Converter;

/**
 * Marks a UI element field for automatic binding to a model property. The code generator wires this
 * as a {@code binder.bind(element, property)} call.
 *
 * <p>The bean must also contain an {@code @Inject DataBinder<T>} field; a compile-time error is
 * raised if no DataBinder is found.
 *
 * @author Dmitrii Tikhomirov
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Bound {

  /**
   * The model property to bind to. Supports dot-notation for nested properties (e.g.
   * {@code "address.city"}). If empty, defaults to the annotated field name.
   */
  String property() default "";

  /**
   * Optional converter class for transforming values between the model and the UI element.
   */
  Class<? extends Converter> converter() default Converter.class;
}
