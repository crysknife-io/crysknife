package org.treblereel.client.qualifiers;

import javax.enterprise.inject.Default;
import javax.inject.Singleton;

import elemental2.dom.DomGlobal;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/13/19
 */
@Default
@Singleton
public class QualifierBeanDefault implements QualifierBean {

    @Override
    public String say() {
        return this.getClass().getSimpleName();
    }
}
