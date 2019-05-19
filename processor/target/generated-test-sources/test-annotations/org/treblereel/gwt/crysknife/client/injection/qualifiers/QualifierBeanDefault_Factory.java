package org.treblereel.gwt.crysknife.client.injection.qualifiers;

import org.treblereel.gwt.crysknife.client.internal.Factory;
import javax.inject.Provider;
import org.treblereel.gwt.crysknife.client.injection.qualifiers.QualifierBeanDefault;

public class QualifierBeanDefault_Factory implements Factory<QualifierBeanDefault> {

    @Override()
    public QualifierBeanDefault get() {
        if (this.instance == null)
            this.instance = new QualifierBeanDefault();
        return this.instance;
    }

    private QualifierBeanDefault instance;

    private QualifierBeanDefault_Factory() {
    }

    public static QualifierBeanDefault_Factory create() {
        return new QualifierBeanDefault_Factory();
    }
}
