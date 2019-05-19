package org.treblereel.gwt.crysknife.client.injection.qualifiers;

import org.treblereel.gwt.crysknife.client.internal.Factory;
import javax.inject.Provider;
import org.treblereel.gwt.crysknife.client.injection.qualifiers.QualifierBeanOne;

public class QualifierBeanOne_Factory implements Factory<QualifierBeanOne> {

    @Override()
    public QualifierBeanOne get() {
        if (this.instance == null)
            this.instance = new QualifierBeanOne();
        return this.instance;
    }

    private QualifierBeanOne instance;

    private QualifierBeanOne_Factory() {
    }

    public static QualifierBeanOne_Factory create() {
        return new QualifierBeanOne_Factory();
    }
}
