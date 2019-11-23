package com.google.common.base;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 11/20/19
 */
public class Objects {

    public static boolean equal(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }


}
