package org.treblereel.gwt.crysknife.client.injection.qualifiers;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/14/19
 */
@Singleton
public class QualifierConstructorInjection {

    public QualifierBeanOne qualifierBeanOne;

    public QualifierBeanTwo qualifierBeanTwo;

    @Inject
    public QualifierConstructorInjection(@QualifierTwo QualifierBeanOne qualifierBeanOne,
                                         @QualifierOne QualifierBeanTwo qualifierBeanTwo) {
        this.qualifierBeanOne = qualifierBeanOne;
        this.qualifierBeanTwo = qualifierBeanTwo;
    }
}
