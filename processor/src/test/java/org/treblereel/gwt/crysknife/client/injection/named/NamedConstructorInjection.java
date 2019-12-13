package org.treblereel.gwt.crysknife.client.injection.named;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 12/12/19
 */
@ApplicationScoped
public class NamedConstructorInjection {

    public NamedBean one;

    public NamedBean two;

    public NamedBean def;

    @Inject
    public NamedConstructorInjection(@Named("NamedBeanOne") NamedBean one, @Named("NamedBeanTwo") NamedBean two, NamedBean def) {
        this.one = one;
        this.two = two;
        this.def = def;
    }
}
