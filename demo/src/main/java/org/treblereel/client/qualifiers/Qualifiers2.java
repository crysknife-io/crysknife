package org.treblereel.client.qualifiers;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;

import elemental2.dom.HTMLDivElement;
import org.jboss.elemento.IsElement;
import org.treblereel.gwt.crysknife.navigation.client.local.Page;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/16/20
 */
@ApplicationScoped
//@Page
public class Qualifiers2 implements IsElement<HTMLDivElement>  {

    @Inject
    @QualifierOne
    public QualifierBean qualifierBeanOne;

    @Inject
    @QualifierTwo
    public QualifierBean qualifierBeanTwo;

    @Inject
    @Default
    public QualifierBean qualifierBeanDefault;

    @Override
    public HTMLDivElement element() {
        return null;
    }
}
