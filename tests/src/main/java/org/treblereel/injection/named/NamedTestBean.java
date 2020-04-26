package org.treblereel.injection.named;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/14/20
 */
@ApplicationScoped
public class NamedTestBean {

    @Inject
    public NamedConstructorInjection namedConstructorInjection;

    @Inject
    public NamedFieldInjection namedFieldInjection;

}
