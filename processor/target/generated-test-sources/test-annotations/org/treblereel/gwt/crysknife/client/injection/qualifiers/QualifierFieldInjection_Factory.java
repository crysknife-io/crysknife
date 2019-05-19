package org.treblereel.gwt.crysknife.client.injection.qualifiers;

import org.treblereel.gwt.crysknife.client.internal.Factory;
import javax.inject.Provider;
import org.treblereel.gwt.crysknife.client.injection.qualifiers.QualifierFieldInjection;
import org.treblereel.gwt.crysknife.client.BeanManagerImpl;
import org.treblereel.gwt.crysknife.client.Instance;

public class QualifierFieldInjection_Factory implements Factory<QualifierFieldInjection> {

    @Override()
    public QualifierFieldInjection get() {
        if (this.instance == null)
            this.instance = new QualifierFieldInjection();
        this.instance.qualifierBeanOne = org_treblereel_gwt_crysknife_client_injection_qualifiers_qualifierbeanone.get();
        this.instance.qualifierBeanTwo = org_treblereel_gwt_crysknife_client_injection_qualifiers_qualifierbeantwo.get();
        this.instance.qualifierBeanDefault = org_treblereel_gwt_crysknife_client_injection_qualifiers_qualifierbeandefault.get();
        return this.instance;
    }

    private QualifierFieldInjection instance;

    private QualifierFieldInjection_Factory() {
        this.org_treblereel_gwt_crysknife_client_injection_qualifiers_qualifierbeanone = BeanManagerImpl.get().lookupBean(org.treblereel.gwt.crysknife.client.injection.qualifiers.QualifierBeanOne.class);
        this.org_treblereel_gwt_crysknife_client_injection_qualifiers_qualifierbeantwo = BeanManagerImpl.get().lookupBean(org.treblereel.gwt.crysknife.client.injection.qualifiers.QualifierBeanTwo.class);
        this.org_treblereel_gwt_crysknife_client_injection_qualifiers_qualifierbeandefault = BeanManagerImpl.get().lookupBean(org.treblereel.gwt.crysknife.client.injection.qualifiers.QualifierBeanDefault.class);
    }

    final private org.treblereel.gwt.crysknife.client.Instance<org.treblereel.gwt.crysknife.client.injection.qualifiers.QualifierBeanOne> org_treblereel_gwt_crysknife_client_injection_qualifiers_qualifierbeanone;

    final private org.treblereel.gwt.crysknife.client.Instance<org.treblereel.gwt.crysknife.client.injection.qualifiers.QualifierBeanTwo> org_treblereel_gwt_crysknife_client_injection_qualifiers_qualifierbeantwo;

    final private org.treblereel.gwt.crysknife.client.Instance<org.treblereel.gwt.crysknife.client.injection.qualifiers.QualifierBeanDefault> org_treblereel_gwt_crysknife_client_injection_qualifiers_qualifierbeandefault;

    public static QualifierFieldInjection_Factory create() {
        return new QualifierFieldInjection_Factory();
    }
}
