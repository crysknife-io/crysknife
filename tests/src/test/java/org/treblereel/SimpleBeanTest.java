package org.treblereel;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 9/10/19
 */
public class SimpleBeanTest {

    @Test
    public void testAppSimpleBean() {
        App app = new App();
        new AppBootstrap(app).initialize();

        assertNotNull(app.getSimpleBean());
        assertEquals(SimpleBean.class.getSimpleName(), app.getSimpleBean().getName());
    }
}
