package org.treblereel.gwt.crysknife.client.injection.qualifiers;

import org.treblereel.gwt.crysknife.client.internal.Factory;
import javax.inject.Provider;
import org.treblereel.gwt.crysknife.client.injection.qualifiers.QualifierBeanTwo;

public class QualifierBeanTwo_Factory implements Factory<QualifierBeanTwo> {

    @Override()
    public QualifierBeanTwo get() {
        if (this.instance == null)
            this.instance = new QualifierBeanTwo();
        return this.instance;
    }

    private QualifierBeanTwo instance;

    private QualifierBeanTwo_Factory() {
    }

    public static QualifierBeanTwo_Factory create() {
        return new QualifierBeanTwo_Factory();
    }
}
