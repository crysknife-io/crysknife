package org.treblereel.injection.named;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 12/12/19
 */
@Singleton
public class NamedFieldInjection {

    @Inject
    @Named("NamedBeanOne")
    public NamedBean one;

    @Inject
    @Named("NamedBeanTwo")
    public NamedBean two;

    @Inject
    public NamedBean def;
}
