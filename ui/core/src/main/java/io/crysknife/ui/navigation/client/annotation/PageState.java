/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
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

package io.crysknife.ui.navigation.client.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Binds a field to a URL query parameter. The navigation framework injects values from the
 * browser's hash fragment into annotated fields before {@code @PageShowing} is called, and
 * re-injects on every {@code @PageUpdate} when the URL changes while staying on the same page.
 *
 * <h3>Field requirements</h3>
 * The field must not be {@code private} or {@code final}.
 *
 * <h3>Supported scalar types</h3>
 * <ul>
 *   <li>{@code String}</li>
 *   <li>{@code int} / {@code Integer}</li>
 *   <li>{@code long} / {@code Long}</li>
 *   <li>{@code boolean} / {@code Boolean}</li>
 *   <li>{@code double} / {@code Double}</li>
 *   <li>{@code float} / {@code Float}</li>
 *   <li>{@code short} / {@code Short}</li>
 *   <li>{@code byte} / {@code Byte}</li>
 * </ul>
 *
 * <h3>Supported list types</h3>
 * For multi-valued query parameters (e.g. {@code ?tag=a&tag=b&tag=c}):
 * <ul>
 *   <li>{@code List<String>}</li>
 *   <li>{@code List<Integer>}, {@code List<Long>}, {@code List<Boolean>},
 *       {@code List<Double>}, {@code List<Float>}, {@code List<Short>},
 *       {@code List<Byte>}</li>
 * </ul>
 * If the parameter is absent, the field receives an empty list.
 * {@link #defaultValue()} is not supported for list fields (compile-time error).
 *
 * <h3>Parameter naming</h3>
 * By default, the query parameter name matches the field name. Use {@link #value()} to map
 * the field to a differently-named parameter:
 * <pre>{@code
 * @PageState("q")
 * String query;  // reads from ?q=...
 * }</pre>
 *
 * <h3>Default values</h3>
 * Use {@link #defaultValue()} to provide a fallback when the parameter is absent:
 * <pre>{@code
 * @PageState(defaultValue = "1")
 * int page;  // defaults to 1 when ?page is missing
 * }</pre>
 * If no default is specified and the parameter is absent, the field is left unmodified.
 *
 * <h3>Usage example</h3>
 * <pre>{@code
 * @Page(path = "search")
 * public class SearchPage implements IsElement<HTMLDivElement> {
 *
 *     @PageState("q")
 *     String query;
 *
 *     @PageState(defaultValue = "1")
 *     int page;
 *
 *     @PageState
 *     List<String> tags;  // #search?q=hello&page=2&tags=java&tags=j2cl
 *
 *     @PageShown
 *     void onShown() {
 *         // query = "hello", page = 2, tags = ["java", "j2cl"]
 *     }
 * }
 * }</pre>
 *
 * @see Page
 * @see io.crysknife.ui.navigation.client.annotation.PageShowing
 * @see io.crysknife.ui.navigation.client.annotation.PageShown
 * @see io.crysknife.ui.navigation.client.annotation.PageUpdate
 *
 * @author Jonathan Fuerth <jfuerth@redhat.com>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface PageState {

  String DEFAULT_VALUE_UNSET = "\0";

  /**
   * Provides a way to map the field name to a different value for the query parameter. If not
   * specified, the name of the field will be used as the name of the query parameter.
   */
  String value() default "";

  /**
   * Provides a default value for the field when the query parameter is not present in the URL.
   * If not specified and the parameter is absent, the field is not modified.
   */
  String defaultValue() default DEFAULT_VALUE_UNSET;

}
