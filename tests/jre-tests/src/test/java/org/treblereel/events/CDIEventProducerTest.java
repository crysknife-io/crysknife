/*
 * Copyright © 2021 Treblereel
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

import org.junit.Test;
import org.treblereel.AbstractTest;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 10/12/21
 */
public class CDIEventProducerTest extends AbstractTest {

  private final PrintStream standardOut = System.out;
  private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();


  @Test
  public void testSimpleEvents1() {
    CDIEventProducer cdiEventProducer =
        app.beanManager.lookupBean(CDIEventProducer.class).getInstance();
    cdiEventProducer.simpleEventEvent.fire(new SimpleEvent());
    assertEquals(1, cdiEventProducer.events.size());
  }

  @Test
  public void testSimpleEvents2() {
    System.setOut(new PrintStream(outputStreamCaptor));

    CDIEventProducer cdiEventProducer =
        app.beanManager.lookupBean(CDIEventProducer.class).getInstance();
    cdiEventProducer.simpleEventEvent.fire(new SimpleEvent());
    assertEquals(2,
        app.beanManager.lookupBean(SimpleEventSubscriber.class).getInstance().events.size());
  }
}
