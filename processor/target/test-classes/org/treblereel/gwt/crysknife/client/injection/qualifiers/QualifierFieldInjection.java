package org.treblereel.gwt.crysknife.client.injection.qualifiers;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/14/19
 */
@Singleton
public class QualifierFieldInjection {

    @Inject
    @QualifierOne
    public QualifierBeanOne qualifierBeanOne;

    @Inject
    @QualifierTwo
    public QualifierBeanTwo qualifierBeanTwo;

    @Inject
    public QualifierBean qualifierBeanDefault;
}
