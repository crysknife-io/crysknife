package org.treblereel.gwt.crysknife.client.injection.qualifiers;

import javax.inject.Singleton;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/13/19
 */
@QualifierTwo
@Singleton
public class QualifierBeanTwo implements QualifierBean {

    @Override
    public String say() {
        return this.getClass().getCanonicalName();
    }

}
