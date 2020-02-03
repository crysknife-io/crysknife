package org.treblereel.gwt.crysknife.client.internal.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/3/20
 */
public class Multimap<K, V> {

    private HashMap<K, List<V>> holder = new HashMap<>();


    public void put(K key, V value) {
        if(!holder.containsKey(key)) {
            holder.put(key, new ArrayList<>());
        }
        holder.get(key).add(value);
    }

    public Collection<V> values() {
        Set<V> result = new HashSet<>();
        for (List<V> values : holder.values()) {
            for (V value : values) {
                result.add(value);
            }
        }
        return result;
    }

    public List<V> get(K key) {
        if(!holder.containsKey(key)) {
            return Collections.EMPTY_LIST;
        }
        return holder.get(key);
    }

    public boolean isEmpty() {
        return holder.isEmpty();
    }

    public void remove(K key, V value) {
       if(holder.containsKey(key)) {
           holder.get(key).remove(value);
       }
    }

    public boolean containsKey(K key) {
        return holder.containsKey(key);
    }

    public Collection<K> keys() {
        return holder.keySet();
    }

    public Set<K> keySet() {
        return holder.keySet();
    }

    public boolean containsEntry(K key, V value) {
        return holder.get(key).contains(value);
    }

    public void clear() {
        holder.clear();
    }

    public void removeAll(K key) {
        holder.get(key).clear();
    }

}
