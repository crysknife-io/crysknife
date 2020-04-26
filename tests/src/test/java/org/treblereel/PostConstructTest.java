package org.treblereel;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/26/20
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

}
