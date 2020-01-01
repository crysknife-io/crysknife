package org.treblereel.gwt.crysknife.client;

import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.treblereel.gwt.crysknife.client.injection.named.NamedBeanDefault;
import org.treblereel.gwt.crysknife.client.injection.named.NamedBeanOne;
import org.treblereel.gwt.crysknife.client.injection.named.NamedBeanTwo;
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

    //@Before
    public void setup() {
        new AppBootstrap(app).initialize();
    }

    //@Test
    public void testQualifierConstructorInjection() {
        Assert.assertEquals(QualifierBeanOne.class, app.qualifierConstructorInjection.qualifierBeanOne.getClass());
        Assert.assertEquals(QualifierBeanTwo.class.getCanonicalName(), app.qualifierConstructorInjection.qualifierBeanTwo.say());
    }

    //@Test
    public void testQualifierFieldInjection() {
        Assert.assertEquals(QualifierFieldInjection.class, app.qualifierFieldInjection.getClass());
        Assert.assertEquals(QualifierBeanOne.class.getCanonicalName(), app.qualifierFieldInjection.qualifierBeanOne.say());
        Assert.assertEquals(QualifierBeanTwo.class.getCanonicalName(), app.qualifierFieldInjection.qualifierBeanTwo.say());
        Assert.assertEquals(QualifierBeanDefault.class.getCanonicalName(), app.qualifierFieldInjection.qualifierBeanDefault.say());
    }

    //@Test
    public void testQualifierBeanManager() {
        Assert.assertEquals(QualifierBeanDefault.class, app.beanManager.lookupBean(QualifierBean.class).get().getClass());
        Assert.assertEquals(QualifierBeanDefault.class, app.beanManager.lookupBean(QualifierBean.class, Default.class).get().getClass());
        Assert.assertEquals(QualifierBeanOne.class, app.beanManager.lookupBean(QualifierBean.class, QualifierOne.class).get().getClass());
        Assert.assertEquals(QualifierBeanTwo.class, app.beanManager.lookupBean(QualifierBean.class, QualifierTwo.class).get().getClass());
    }

    //@Inject
    public void testApplicationScopedConstructorInjection() {
        Assert.assertNotNull(app.applicationScopedConstructorInjection);
        Assert.assertNotNull(app.applicationScopedConstructorInjection.bean);
        Assert.assertNotNull(app.applicationScopedConstructorInjection.bean2);
        Assert.assertEquals(app.applicationScopedConstructorInjection.bean, app.applicationScopedConstructorInjection.bean2);
        Assert.assertEquals(app.applicationScopedConstructorInjection.bean.say(), app.applicationScopedConstructorInjection.bean2.say());
    }

    //@Inject
    public void testNamedConstructorInjection() {
        Assert.assertNotNull(app.namedConstructorInjection);
        Assert.assertNotNull(app.namedConstructorInjection.one);
        Assert.assertNotNull(app.namedConstructorInjection.two);
        Assert.assertNotNull(app.namedConstructorInjection.def);
        Assert.assertEquals(app.namedConstructorInjection.one.say(), NamedBeanOne.class.getCanonicalName());
        Assert.assertEquals(app.namedConstructorInjection.two.say(), NamedBeanTwo.class.getCanonicalName());
        Assert.assertEquals(app.namedConstructorInjection.def.say(), NamedBeanDefault.class.getCanonicalName());
    }

    //@Inject
    public void testNamedFieldInjection() {
        Assert.assertNotNull(app.namedFieldInjection);
        Assert.assertNotNull(app.namedFieldInjection.one);
        Assert.assertNotNull(app.namedFieldInjection.two);
        Assert.assertNotNull(app.namedFieldInjection.def);
        Assert.assertEquals(app.namedFieldInjection.one.say(), NamedBeanOne.class.getCanonicalName());
        Assert.assertEquals(app.namedFieldInjection.two.say(), NamedBeanTwo.class.getCanonicalName());
        Assert.assertEquals(app.namedFieldInjection.def.say(), NamedBeanDefault.class.getCanonicalName());
    }
}
