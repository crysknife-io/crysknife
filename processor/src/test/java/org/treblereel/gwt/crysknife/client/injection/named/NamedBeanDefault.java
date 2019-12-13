package org.treblereel.gwt.crysknife.client.injection.named;

import javax.enterprise.inject.Default;
import javax.inject.Singleton;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 12/12/19
 */
@Default
@Singleton
public class NamedBeanDefault implements NamedBean {

    @Override
    public String say() {
        return this.getClass().getCanonicalName();
    }
}

