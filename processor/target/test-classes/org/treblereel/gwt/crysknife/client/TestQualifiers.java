package org.treblereel.gwt.crysknife.client;

import javax.enterprise.inject.Default;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.treblereel.gwt.crysknife.client.injection.qualifiers.QualifierBean;
import org.treblereel.gwt.crysknife.client.injection.qualifiers.QualifierBeanDefault;
import org.treblereel.gwt.crysknife.client.injection.qualifiers.QualifierBeanOne;
import org.treblereel.gwt.crysknife.client.injection.qualifiers.QualifierBeanTwo;
import org.treblereel.gwt.crysknife.client.injection.qualifiers.QualifierOne;
import org.treblereel.gwt.crysknife.client.injection.qualifiers.QualifierTwo;
import org.treblereel.gwt.crysknife.client.injection.singleton.SingletonBean;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/14/19
 */
public class TestQualifiers {

    App app = new App();

    @Before
    public void setup() {

        //org.treblereel.gwt.crysknife.client.Instance<SingletonBean> sb =  BeanManagerImpl.get().lookupBean(org.treblereel.gwt.crysknife.client.injection.singleton.SingletonBean.class);
        org.treblereel.gwt.crysknife.client.Instance<SingletonBean> sb = null;
                //=  BeanManagerImpl.get().lookupBean(org.treblereel.gwt.crysknife.client.injection.singleton.SingletonBean.class);


        Assert.assertNotNull(BeanManager_Factory.create().get());
        Assert.assertNotNull(BeanManager_Factory.create().get().lookupBean(SingletonBean.class));
        Assert.assertNull(BeanManagerImpl.get().lookupBean(SingletonBean.class));
        Assert.assertNull(sb.get());

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
        Assert.assertEquals(QualifierBeanDefault.class.getCanonicalName(), app.qualifierFieldInjection.qualifierBeanDefault.say());
    }

    @Test
    public void testQualifierBeanManager() {
        Assert.assertEquals(QualifierBeanDefault.class, app.beanManager.lookupBean(QualifierBean.class).get().getClass());
        Assert.assertEquals(QualifierBeanDefault.class, app.beanManager.lookupBean(QualifierBean.class, Default.class).get().getClass());
        Assert.assertEquals(QualifierBeanOne.class, app.beanManager.lookupBean(QualifierBean.class, QualifierOne.class).get().getClass());
        Assert.assertEquals(QualifierBeanTwo.class, app.beanManager.lookupBean(QualifierBean.class, QualifierTwo.class).get().getClass());
    }
}
