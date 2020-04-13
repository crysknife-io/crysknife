package org.treblereel;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/21/20
 */
@ApplicationScoped
public class SimpleBean {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
