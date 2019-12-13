package com.google.common.collect;

import java.util.Collection;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 11/23/19
 */
public final class Collections2 {

    private Collections2() {
    }

    static boolean safeContains(Collection<?> collection, Object object) {
        try {
            return collection.contains(object);
        } catch (ClassCastException | NullPointerException e) {
            return false;
        }
    }
}
