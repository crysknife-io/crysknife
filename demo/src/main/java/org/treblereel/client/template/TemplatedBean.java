package org.treblereel.client.template;

import javax.inject.Inject;
import javax.inject.Singleton;

import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import org.jboss.gwt.elemento.core.IsElement;
import org.treblereel.gwt.crysknife.annotation.DataField;
import org.treblereel.gwt.crysknife.annotation.Templated;

@Singleton
@Templated("templated.html")
public class TemplatedBean implements IsElement<HTMLDivElement> {

    @Inject
    HTMLDivElement info;

    @DataField
    HTMLButtonElement button;

    @DataField
    HTMLButtonElement button1;

    public HTMLDivElement element() {
        return info;
    }
}
