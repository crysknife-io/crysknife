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
package org.treblereel.databinding;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.treblereel.AbstractTest;

import io.crysknife.ui.databinding.api.DataBinder;
import io.crysknife.ui.databinding.api.PropertyChangeEvent;

public class DataBinderTest extends AbstractTest {

  @Test
  public void testInjectedBinderNotNull() {
    assertNotNull(app.userBinder);
  }

  @Test
  public void testGetModelReturnsProxy() {
    UserModel model = app.userBinder.getModel();
    assertNotNull(model);
  }

  @Test
  public void testSetAndGetProperty() {
    UserModel model = app.userBinder.getModel();
    model.setName("John");
    assertEquals("John", model.getName());
  }

  @Test
  public void testPropertyChangeHandler() {
    List<PropertyChangeEvent<?>> events = new ArrayList<>();
    app.userBinder.addPropertyChangeHandler(events::add);

    UserModel model = app.userBinder.getModel();
    model.setName("Alice");

    assertEquals(1, events.size());
    assertEquals("name", events.get(0).getPropertyName());
    assertEquals("Alice", events.get(0).getNewValue());
  }

  @Test
  public void testPropertySpecificHandler() {
    List<PropertyChangeEvent<?>> nameEvents = new ArrayList<>();
    app.userBinder.addPropertyChangeHandler("name", nameEvents::add);

    UserModel model = app.userBinder.getModel();
    model.setName("Bob");
    model.setEmail("bob@example.com");

    assertEquals(1, nameEvents.size());
    assertEquals("name", nameEvents.get(0).getPropertyName());
  }

  @Test
  public void testSetModel() {
    UserModel original = new UserModel();
    original.setName("Original");

    app.userBinder.setModel(original);
    UserModel proxy = app.userBinder.getModel();
    assertEquals("Original", proxy.getName());
  }

  @Test
  public void testSetModelPreservesHandlers() {
    List<PropertyChangeEvent<?>> events = new ArrayList<>();
    app.userBinder.addPropertyChangeHandler(events::add);

    UserModel newModel = new UserModel();
    newModel.setName("New");
    app.userBinder.setModel(newModel);

    UserModel proxy = app.userBinder.getModel();
    proxy.setEmail("test@test.com");

    assertEquals(1, events.size());
    assertEquals("email", events.get(0).getPropertyName());
  }

  @Test
  public void testUnsubscribeHandler() {
    List<PropertyChangeEvent<?>> events = new ArrayList<>();
    var handle = app.userBinder.addPropertyChangeHandler(events::add);

    UserModel model = app.userBinder.getModel();
    model.setName("Before");
    assertEquals(1, events.size());

    handle.unsubscribe();
    model.setName("After");
    assertEquals(1, events.size());
  }

  @Test
  public void testForType() {
    DataBinder<UserModel> binder = DataBinder.forType(UserModel.class);
    assertNotNull(binder);

    UserModel model = binder.getModel();
    assertNotNull(model);
    model.setName("Test");
    assertEquals("Test", model.getName());
  }

  @Test
  public void testForModel() {
    UserModel existing = new UserModel();
    existing.setName("Existing");
    existing.setAge(30);

    DataBinder<UserModel> binder = DataBinder.forModel(existing);
    UserModel proxy = binder.getModel();

    assertEquals("Existing", proxy.getName());
    assertEquals(30, proxy.getAge());
  }

  @Test
  public void testMultiplePropertyChanges() {
    List<PropertyChangeEvent<?>> events = new ArrayList<>();
    app.userBinder.addPropertyChangeHandler(events::add);

    UserModel model = app.userBinder.getModel();
    model.setName("John");
    model.setEmail("john@example.com");
    model.setAge(25);

    assertEquals(3, events.size());
    assertEquals("name", events.get(0).getPropertyName());
    assertEquals("email", events.get(1).getPropertyName());
    assertEquals("age", events.get(2).getPropertyName());
  }

  @Test
  public void testUnbindAllClearsHandlers() {
    List<PropertyChangeEvent<?>> events = new ArrayList<>();
    app.userBinder.addPropertyChangeHandler(events::add);
    app.userBinder.addPropertyChangeHandler("name", events::add);

    app.userBinder.unbind();

    UserModel model = app.userBinder.getModel();
    model.setName("Ghost");
    assertEquals(0, events.size());
  }

  @Test
  public void testUnbindPropertyClearsPropertyHandler() {
    List<PropertyChangeEvent<?>> nameEvents = new ArrayList<>();
    List<PropertyChangeEvent<?>> globalEvents = new ArrayList<>();
    app.userBinder.addPropertyChangeHandler(globalEvents::add);
    app.userBinder.addPropertyChangeHandler("name", nameEvents::add);

    app.userBinder.unbind("name");

    UserModel model = app.userBinder.getModel();
    model.setName("Test");

    assertEquals(0, nameEvents.size());
    assertEquals(1, globalEvents.size());
  }

  @Test
  public void testOldValueInEvent() {
    UserModel model = app.userBinder.getModel();
    model.setName("First");

    List<PropertyChangeEvent<?>> events = new ArrayList<>();
    app.userBinder.addPropertyChangeHandler(events::add);
    model.setName("Second");

    assertEquals(1, events.size());
    assertEquals("First", events.get(0).getOldValue());
    assertEquals("Second", events.get(0).getNewValue());
  }
}
