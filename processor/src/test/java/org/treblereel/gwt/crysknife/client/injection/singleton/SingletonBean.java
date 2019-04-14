package org.treblereel.gwt.crysknife.client.injection.singleton;

import javax.inject.Singleton;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/14/19
 */
@Singleton
public class SingletonBean {

    public String say() {
        return this.getClass().getCanonicalName();
    }
}
