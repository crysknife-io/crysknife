/*
 * Copyright (C) 2014 Google, Inc.
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
package io.crysknife.client.internal;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Scope;

/**
 * An {@linkplain Scope unscoped} {@link Provider}. While a {@link Provider} <i>may</i> apply
 * scoping semantics while providing an instance, a factory implementation is guaranteed to exercise
 * the binding logic upon each call to {@link #get}.
 *
 * <p>
 * Note that while subsequent calls to {@link #get} will create new instances for bindings such as
 * those created by {@link Inject} constructors, a new instance is not guaranteed by all bindings.
 *
 * @author Gregory Kick
 *
 * @since 2.0
 */
public interface Factory<T> extends Provider<T> {

}
