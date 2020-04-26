package org.treblereel;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/21/20
 */
@ApplicationScoped
public class SimpleBean {

    public String getName() {
        return this.getClass().getSimpleName();
    }

}
