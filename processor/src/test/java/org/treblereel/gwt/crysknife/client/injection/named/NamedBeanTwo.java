package org.treblereel.gwt.crysknife.client.injection.named;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 12/12/19
 */
@Named("NamedBeanTwo")
@Singleton
public class NamedBeanTwo implements NamedBean {

    @Override
    public String say() {
        return this.getClass().getCanonicalName();
    }
}
