package org.treblereel.client;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import elemental2.dom.HTMLDivElement;
import org.jboss.elemento.IsElement;
import org.treblereel.gwt.crysknife.templates.client.annotation.DataField;
import org.treblereel.gwt.crysknife.templates.client.annotation.Templated;
import org.treblereel.gwt.crysknife.navigation.client.local.Navigation;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/12/20
 */
@Singleton
@Templated(value = "main.html")
public class Main implements IsElement<HTMLDivElement> {

    @Inject
    @DataField
    private HTMLDivElement root, container;

    @Inject
    private Navigation navigation;

    @PostConstruct
    public void init() {
        navigation.setNavigationContainer(container);
    }

    @Override
    public HTMLDivElement element() {
        return root;
    }
}
