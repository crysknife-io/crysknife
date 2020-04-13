package org.treblereel;

import com.google.j2cl.junit.apt.J2clTestInput;
import junit.framework.TestCase;
import org.junit.Test;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 9/10/19
 */
@J2clTestInput(SimpleBeanTest.class)
public class SimpleBeanTest extends TestCase {



    @Test
    public void testAppSimpleBean() {
        App app = new App();
        new AppBootstrap(app).initialize();

        assertNotNull(app.getSimpleBean());
    }
}
