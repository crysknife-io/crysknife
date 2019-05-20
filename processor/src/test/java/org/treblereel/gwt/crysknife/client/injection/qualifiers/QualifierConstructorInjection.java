package org.treblereel.gwt.crysknife.client.injection.qualifiers;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/14/19
 */
@Singleton
public class QualifierConstructorInjection {

    public QualifierBean qualifierBeanOne;

    public QualifierBean qualifierBeanTwo;

    @Inject
    public QualifierBean qualifier;


    @Inject
    public QualifierConstructorInjection(@QualifierTwo QualifierBean qualifierBeanTwo,
                                         @QualifierOne QualifierBean qualifierBeanOne) {
        this.qualifierBeanOne = qualifierBeanOne;
        this.qualifierBeanTwo = qualifierBeanTwo;
    }
}
