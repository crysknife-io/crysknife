package org.treblereel.gwt.crysknife.client;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.treblereel.gwt.crysknife.client.injection.qualifiers.QualifierBeanOne;
import org.treblereel.gwt.crysknife.client.injection.qualifiers.QualifierBeanTwo;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/14/19
 */
public class TestQualifiers {

    App app = new App();

    @Before
    public void setup() {
        new AppBootstrap(app).initialize();
    }

    @Test
    public void testQualifierConstructorInjection() {
        Assert.assertEquals(QualifierBeanOne.class.getCanonicalName(), app.qualifierConstructorInjection.qualifierBeanOne.say());
        Assert.assertEquals(QualifierBeanTwo.class.getCanonicalName(), app.qualifierConstructorInjection.qualifierBeanTwo.say());
    }

    @Test
    public void testQualifierFieldInjection() {
        Assert.assertEquals(QualifierBeanOne.class.getCanonicalName(), app.qualifierFieldInjection.qualifierBeanOne.say());
        Assert.assertEquals(QualifierBeanTwo.class.getCanonicalName(), app.qualifierFieldInjection.qualifierBeanTwo.say());
    }
}
