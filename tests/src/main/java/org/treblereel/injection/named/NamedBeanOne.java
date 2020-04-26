package org.treblereel.injection.named;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 12/12/19
 */
@Named("NamedBeanOne")
@Singleton
public class NamedBeanOne implements NamedBean{

    @Override
    public String say() {
        return this.getClass().getCanonicalName();
    }
}
