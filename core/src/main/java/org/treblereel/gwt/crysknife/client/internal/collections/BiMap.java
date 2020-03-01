package org.treblereel.gwt.crysknife.client.internal.collections;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Temporary BiMap impl
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/22/20
 */
public class BiMap<K, V> extends HashMap<K, V> {

    public Map<V, K> inverse() {
        return inverse(this);
    }

    private <V, K> Map<V, K> inverse(Map<K, V> map) {
        return map.entrySet()
                .stream()
                .collect(Collectors.toMap(Entry::getValue, Entry::getKey));
    }
}
