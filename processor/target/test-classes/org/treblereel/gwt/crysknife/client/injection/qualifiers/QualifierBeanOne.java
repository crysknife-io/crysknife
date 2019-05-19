package org.treblereel.gwt.crysknife.client.injection.qualifiers;

import javax.inject.Singleton;

import elemental2.dom.DomGlobal;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/13/19
 */
@QualifierOne
@Singleton
public class QualifierBeanOne implements QualifierBean {

    @Override
    public String say() {
        return this.getClass().getCanonicalName();
    }

}
