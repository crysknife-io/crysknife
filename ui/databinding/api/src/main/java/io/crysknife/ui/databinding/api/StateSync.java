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
package io.crysknife.ui.databinding.api;

/**
 * Determines the direction of initial state synchronization when a binding is established or a model
 * is set.
 *
 * @author Dmitrii Tikhomirov
 */
public enum StateSync {

  /** The UI element receives the current model value. */
  FROM_MODEL,

  /** The model receives the current UI element value. */
  FROM_UI
}
