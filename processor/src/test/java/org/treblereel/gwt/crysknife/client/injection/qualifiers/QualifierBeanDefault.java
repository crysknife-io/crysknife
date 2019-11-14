package org.treblereel.gwt.crysknife.client.injection.qualifiers;

import javax.enterprise.inject.Default;
import javax.inject.Singleton;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/13/19
 */
@Default
@Singleton
public class QualifierBeanDefault implements QualifierBean {

    @Override
    public String say() {
        return this.getClass().getCanonicalName();
    }
}
