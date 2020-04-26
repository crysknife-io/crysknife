package org.treblereel;

import org.junit.Test;

import static org.junit.Assert.assertNotEquals;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/26/20
 */
public class SimpleDependentTest extends AbstractTest {

    @Test
    public void testDependent() {
        int fieldOne = app.simpleDependentTest.getFieldOne().getRandom();
        int fieldTwo = app.simpleDependentTest.getFieldTwo().getRandom();
        int constrOne = app.simpleDependentTest.getConstrOne().getRandom();
        int constrTwo = app.simpleDependentTest.getConstrTwo().getRandom();

        assertNotEquals(fieldOne, fieldTwo);
        assertNotEquals(constrOne, constrTwo);
    }
}
