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

import java.util.Set;

import elemental2.dom.HTMLElement;
import io.crysknife.ui.databinding.internal.BindableProxy;
import io.crysknife.ui.databinding.internal.BindableProxyRegistry;
import io.crysknife.ui.databinding.internal.BindingAgent;

/**
 * Two-way data binder that synchronises a Java model object with UI elements.
 *
 * <p>Typical programmatic usage:
 * <pre>{@code
 * DataBinder<User> binder = DataBinder.forType(User.class);
 * binder.bind(nameInput, "name");
 * User user = binder.getModel();
 * user.setName("John"); // nameInput.value is updated automatically
 * }</pre>
 *
 * <p>Declarative usage with {@code @Inject}:
 * <pre>{@code
 * @Inject DataBinder<User> binder;
 * @Bound(property = "name") HTMLInputElement nameInput;
 * }</pre>
 *
 * @param <T> the model type
 * @author Dmitrii Tikhomirov
 */
public class DataBinder<T> {

  private final BindableProxy<T> proxy;
  private final BindingAgent agent;

  private DataBinder(BindableProxy<T> proxy) {
    this.proxy = proxy;
    this.agent = proxy.getBindingAgent();
  }

  @SuppressWarnings("unchecked")
  public static <T> DataBinder<T> forType(Class<T> modelType) {
    BindableProxy<T> proxy = (BindableProxy<T>) BindableProxyRegistry.create(modelType);
    return new DataBinder<>(proxy);
  }

  @SuppressWarnings("unchecked")
  public static <T> DataBinder<T> forModel(T model) {
    BindableProxy<T> proxy = (BindableProxy<T>) BindableProxyRegistry.create(model.getClass());
    proxy.setTarget(model);
    return new DataBinder<>(proxy);
  }

  @SuppressWarnings("unchecked")
  public T getModel() {
    return (T) proxy;
  }

  public void setModel(T model) {
    setModel(model, StateSync.FROM_MODEL);
  }

  public void setModel(T model, StateSync initialState) {
    proxy.setTarget(model);
    if (initialState == StateSync.FROM_MODEL) {
      agent.updateAllElements();
    }
  }

  public DataBinder<T> bind(HTMLElement element, String property) {
    agent.bind(element, property, null);
    return this;
  }

  public DataBinder<T> bind(HTMLElement element, String property, Converter<?, ?> converter) {
    agent.bind(element, property, converter);
    return this;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public DataBinder<T> bindList(ListComponent<?> component, String property) {
    agent.bindList((ListComponent) component, property);
    return this;
  }

  public DataBinder<T> unbind(String property) {
    agent.unbind(property);
    return this;
  }

  public DataBinder<T> unbind() {
    agent.unbindAll();
    return this;
  }

  public Set<String> getBoundProperties() {
    return agent.getBoundProperties();
  }

  public PropertyChangeUnsubscribeHandle addPropertyChangeHandler(
      PropertyChangeHandler<?> handler) {
    return agent.addPropertyChangeHandler(handler);
  }

  public PropertyChangeUnsubscribeHandle addPropertyChangeHandler(String property,
      PropertyChangeHandler<?> handler) {
    return agent.addPropertyChangeHandler(property, handler);
  }
}
