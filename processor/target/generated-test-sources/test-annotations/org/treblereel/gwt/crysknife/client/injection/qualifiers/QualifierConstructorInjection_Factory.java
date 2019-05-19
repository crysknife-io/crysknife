package org.treblereel.gwt.crysknife.client.injection.qualifiers;

import org.treblereel.gwt.crysknife.client.internal.Factory;
import javax.inject.Provider;
import org.treblereel.gwt.crysknife.client.injection.qualifiers.QualifierConstructorInjection;
import org.treblereel.gwt.crysknife.client.BeanManagerImpl;
import org.treblereel.gwt.crysknife.client.Instance;

public class QualifierConstructorInjection_Factory implements Factory<QualifierConstructorInjection> {

    @Override()
    public QualifierConstructorInjection get() {
        this.instance.qualifierBeanOne = org_treblereel_gwt_crysknife_client_injection_qualifiers_qualifierbeanone.get();
        this.instance.qualifierBeanTwo = org_treblereel_gwt_crysknife_client_injection_qualifiers_qualifierbeantwo.get();
        if (this.instance == null)
            this.instance = new org.treblereel.gwt.crysknife.client.injection.qualifiers.QualifierConstructorInjection(org_treblereel_gwt_crysknife_client_injection_qualifiers_qualifierbeanone.get(), org_treblereel_gwt_crysknife_client_injection_qualifiers_qualifierbeantwo.get());
        return this.instance;
    }

    private QualifierConstructorInjection_Factory() {
        this.org_treblereel_gwt_crysknife_client_injection_qualifiers_qualifierbeanone = BeanManagerImpl.get().lookupBean(org.treblereel.gwt.crysknife.client.injection.qualifiers.QualifierBeanOne.class);
        this.org_treblereel_gwt_crysknife_client_injection_qualifiers_qualifierbeantwo = BeanManagerImpl.get().lookupBean(org.treblereel.gwt.crysknife.client.injection.qualifiers.QualifierBeanTwo.class);
    }

    final private org.treblereel.gwt.crysknife.client.Instance<org.treblereel.gwt.crysknife.client.injection.qualifiers.QualifierBeanOne> org_treblereel_gwt_crysknife_client_injection_qualifiers_qualifierbeanone;

    final private org.treblereel.gwt.crysknife.client.Instance<org.treblereel.gwt.crysknife.client.injection.qualifiers.QualifierBeanTwo> org_treblereel_gwt_crysknife_client_injection_qualifiers_qualifierbeantwo;

    private QualifierConstructorInjection instance;

    public static QualifierConstructorInjection_Factory create() {
        return new QualifierConstructorInjection_Factory();
    }
}
