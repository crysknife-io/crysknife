package org.treblereel.client.databinding;

import org.treblereel.gwt.crysknife.databinding.client.api.Bindable;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 11/25/19
 */
@Bindable
public class Street {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Street{" +
                "name='" + name + '\'' +
                '}';
    }
}
