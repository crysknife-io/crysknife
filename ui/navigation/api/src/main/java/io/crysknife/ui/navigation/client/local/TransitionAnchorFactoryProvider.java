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

package io.crysknife.ui.navigation.client.local;

import io.crysknife.client.IsElement;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/**
 * Provides new instances of the {@link TransitionAnchorFactory} class, which allows them to be
 * injected.
 *
 * @author eric.wittmann@redhat.com
 */
@Singleton
public class TransitionAnchorFactoryProvider {

  @Inject
  Navigation navigation;
  @Inject
  HistoryTokenFactory htFactory;

  @SuppressWarnings({"rawtypes", "unchecked"})
  public TransitionAnchorFactory provide(Class<?>[] typeargs) {
    Class<IsElement> toPageType = (Class<IsElement>) typeargs[0];
    return new TransitionAnchorFactory<>(navigation, toPageType, htFactory);
  }

}
