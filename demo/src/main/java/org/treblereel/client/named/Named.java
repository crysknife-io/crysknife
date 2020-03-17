package org.treblereel.client.named;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.dom.HTMLDivElement;
import org.jboss.elemento.IsElement;
import org.treblereel.gwt.crysknife.navigation.client.local.Page;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/12/20
 */
@ApplicationScoped
@Page
public class Named implements IsElement<HTMLDivElement> {

    @Inject
    private NamedBeanConstructorInjectionPanel constructor;

    @Inject
    private NamedBeanFieldInjectionPanel field;

    @Inject
    private HTMLDivElement root;

    @PostConstruct
    public void init() {
        root.appendChild(constructor.element());
        root.appendChild(field.element());

    }

    @Override
    public HTMLDivElement element() {
        return root;
    }
}
