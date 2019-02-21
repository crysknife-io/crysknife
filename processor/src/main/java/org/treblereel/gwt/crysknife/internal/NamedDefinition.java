package org.treblereel.gwt.crysknife.internal;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/21/19
 */
public class NamedDefinition {

    private final Map<String, String> implementations = new HashMap<>();

    private final String iface;

    public NamedDefinition(String iface){
        this.iface = iface;
    }

    public Map<String, String> getImplementations() {
        return implementations;
    }
}
