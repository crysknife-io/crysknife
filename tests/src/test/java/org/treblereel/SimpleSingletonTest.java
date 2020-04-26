package org.treblereel;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/26/20
 */
public class SimpleSingletonTest extends AbstractTest {

    @Test
    public void testDependent() {
        int fieldOne = app.simpleSingletonTest.getFieldOne().getRandom();
        int fieldTwo = app.simpleSingletonTest.getFieldTwo().getRandom();
        int constrOne = app.simpleSingletonTest.getConstrOne().getRandom();
        int constrTwo = app.simpleSingletonTest.getConstrTwo().getRandom();

        assertEquals(fieldOne, fieldTwo);
        assertEquals(constrOne, constrTwo);
    }
}
