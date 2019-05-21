package org.treblereel.gwt.crysknife.client.injection.applicationscoped;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 5/21/19
 */
@Singleton
public class ApplicationScopedConstructorInjection {

    public ApplicationScopedBean bean;

    @Inject
    public ApplicationScopedBean bean2;

    @Inject
    public ApplicationScopedConstructorInjection(ApplicationScopedBean bean) {
        this.bean = bean;
    }

}
