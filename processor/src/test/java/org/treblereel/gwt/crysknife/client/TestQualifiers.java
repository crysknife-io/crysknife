package org.treblereel.gwt.crysknife.client;

import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.treblereel.gwt.crysknife.client.injection.qualifiers.QualifierBean;
import org.treblereel.gwt.crysknife.client.injection.qualifiers.QualifierBeanDefault;
import org.treblereel.gwt.crysknife.client.injection.qualifiers.QualifierBeanOne;
import org.treblereel.gwt.crysknife.client.injection.qualifiers.QualifierBeanTwo;
import org.treblereel.gwt.crysknife.client.injection.qualifiers.QualifierFieldInjection;
import org.treblereel.gwt.crysknife.client.injection.qualifiers.QualifierOne;
import org.treblereel.gwt.crysknife.client.injection.qualifiers.QualifierTwo;

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
        Assert.assertEquals(QualifierBeanOne.class, app.qualifierConstructorInjection.qualifierBeanOne.getClass());
        Assert.assertEquals(QualifierBeanTwo.class.getCanonicalName(), app.qualifierConstructorInjection.qualifierBeanTwo.say());
    }

    @Test
    public void testQualifierFieldInjection() {
        Assert.assertEquals(QualifierFieldInjection.class, app.qualifierFieldInjection.getClass());
        Assert.assertEquals(QualifierBeanOne.class.getCanonicalName(), app.qualifierFieldInjection.qualifierBeanOne.say());
        Assert.assertEquals(QualifierBeanTwo.class.getCanonicalName(), app.qualifierFieldInjection.qualifierBeanTwo.say());
        Assert.assertEquals(QualifierBeanDefault.class.getCanonicalName(), app.qualifierFieldInjection.qualifierBeanDefault.say());
    }

    @Test
    public void testQualifierBeanManager() {
        Assert.assertEquals(QualifierBeanDefault.class, app.beanManager.lookupBean(QualifierBean.class).get().getClass());
        Assert.assertEquals(QualifierBeanDefault.class, app.beanManager.lookupBean(QualifierBean.class, Default.class).get().getClass());
        Assert.assertEquals(QualifierBeanOne.class, app.beanManager.lookupBean(QualifierBean.class, QualifierOne.class).get().getClass());
        Assert.assertEquals(QualifierBeanTwo.class, app.beanManager.lookupBean(QualifierBean.class, QualifierTwo.class).get().getClass());
    }

    @Inject
    public void testApplicationScopedConstructorInjection() {
        Assert.assertNotNull(app.applicationScopedConstructorInjection);
        Assert.assertNotNull(app.applicationScopedConstructorInjection.bean);
        Assert.assertNotNull(app.applicationScopedConstructorInjection.bean2);
        Assert.assertEquals(app.applicationScopedConstructorInjection.bean, app.applicationScopedConstructorInjection.bean2);
        Assert.assertEquals(app.applicationScopedConstructorInjection.bean.say(), app.applicationScopedConstructorInjection.bean2.say());
    }
}
