package org.treblereel;

import org.junit.Test;
import org.treblereel.injection.named.NamedBeanDefault;
import org.treblereel.injection.named.NamedBeanOne;
import org.treblereel.injection.named.NamedBeanTwo;

import static org.junit.Assert.assertEquals;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/26/20
 */
public class NamedTest extends AbstractTest {

    @Test
    public void testNamedConstructorInjection() {
        assertEquals(NamedBeanDefault.class.getSimpleName(), app.getNamedTestBean().namedConstructorInjection.def.getClass().getSimpleName());
        assertEquals(NamedBeanOne.class.getSimpleName(), app.getNamedTestBean().namedConstructorInjection.one.getClass().getSimpleName());
        assertEquals(NamedBeanTwo.class.getSimpleName(), app.getNamedTestBean().namedConstructorInjection.two.getClass().getSimpleName());
    }

    @Test
    public void testNamedFieldInjection() {
        assertEquals(NamedBeanDefault.class.getSimpleName(), app.getNamedTestBean().namedFieldInjection.def.getClass().getSimpleName());
        assertEquals(NamedBeanOne.class.getSimpleName(), app.getNamedTestBean().namedFieldInjection.one.getClass().getSimpleName());
        assertEquals(NamedBeanTwo.class.getSimpleName(), app.getNamedTestBean().namedFieldInjection.two.getClass().getSimpleName());
    }
}
