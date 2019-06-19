package org.treblereel.gwt.crysknife.client;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/29/19
 */
public interface Instance<T> {

    <T> T get();

    void destroy(T var1);

    void destroyAll();

}
