package org.treblereel.gwt.crysknife.client;

import javax.inject.Provider;
import java.util.Map;
import java.util.HashMap;
import java.lang.annotation.Annotation;
import org.treblereel.gwt.crysknife.client.Instance;
import org.treblereel.gwt.crysknife.client.internal.AbstractBeanManager;

public class BeanManagerImpl extends AbstractBeanManager {

    static private BeanManagerImpl instance;

    private void init() {
        this.register(org.treblereel.gwt.crysknife.client.BeanManager.class, org.treblereel.gwt.crysknife.client.BeanManager_Factory.create());
        this.register(org.treblereel.gwt.crysknife.client.injection.qualifiers.QualifierBean.class, org.treblereel.gwt.crysknife.client.injection.qualifiers.QualifierBeanDefault_Factory.create(), javax.enterprise.inject.Default.class);
        this.register(org.treblereel.gwt.crysknife.client.injection.qualifiers.QualifierBean.class, org.treblereel.gwt.crysknife.client.injection.qualifiers.QualifierBeanTwo_Factory.create(), org.treblereel.gwt.crysknife.client.injection.qualifiers.QualifierTwo.class);
        this.register(org.treblereel.gwt.crysknife.client.injection.qualifiers.QualifierBean.class, org.treblereel.gwt.crysknife.client.injection.qualifiers.QualifierBeanOne_Factory.create(), org.treblereel.gwt.crysknife.client.injection.qualifiers.QualifierOne.class);
    }

    public static BeanManager get() {
        if (instance == null) {
            instance = new BeanManagerImpl();
            instance.init();
        }
        return instance;
    }
}
