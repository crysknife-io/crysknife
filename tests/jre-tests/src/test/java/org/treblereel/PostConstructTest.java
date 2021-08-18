/*
 * Copyright © 2020 Treblereel
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

package org.treblereel;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/26/20
 */
public class PostConstructTest extends AbstractTest {

  @Test
  public void testPostConstructAppBootstrap() {
    assertEquals("PostConstruct", app.testPostConstruct);
  }

  @Test
  public void testSimpleBeanSingleton() {
    assertEquals("done", app.getSimpleBeanSingleton().getPostConstruct());
  }

  @Test
  public void testSimpleBeanApplicationScoped() {
    assertEquals("done", app.getSimpleBeanApplicationScoped().getPostConstruct());
  }

  @Test
  public void testSimpleDependent() {
    assertEquals("done", app.getSimpleBeanDependent().getPostConstruct());
  }

  @Test
  public void testChildPostConstructCalled() {
    assertEquals(1, app.postConstructs.child.calls.size());
    assertEquals("Parent", app.postConstructs.child.calls.get(0));
    assertEquals(2, app.postConstructs.childTwo.calls.size());
    assertEquals("Parent", app.postConstructs.childTwo.calls.get(0));
    assertEquals("ChildTwo", app.postConstructs.childTwo.calls.get(1));
    assertEquals(2, app.postConstructs.childThree.calls.size());
    assertEquals(3, app.postConstructs.childFour.calls.size());
    assertEquals("Parent", app.postConstructs.childTwo.calls.get(0));
    assertEquals("ChildTwo", app.postConstructs.childTwo.calls.get(1));
    assertEquals("ChildFour", app.postConstructs.childFour.calls.get(2));
  }

}
