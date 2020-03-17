package org.treblereel.client.about;

import javax.inject.Inject;
import javax.inject.Singleton;

import elemental2.dom.HTMLDivElement;
import org.jboss.elemento.IsElement;
import org.treblereel.gwt.crysknife.annotation.DataField;
import org.treblereel.gwt.crysknife.annotation.Templated;
import org.treblereel.gwt.crysknife.navigation.client.local.DefaultPage;
import org.treblereel.gwt.crysknife.navigation.client.local.Page;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/12/20
 */
@Singleton
@Page(role = DefaultPage.class)
@Templated(value = "about.html")
public class About implements IsElement<HTMLDivElement> {

    @Inject
    @DataField
    HTMLDivElement root;

    @Override
    public HTMLDivElement element() {
        return root;
    }
}
