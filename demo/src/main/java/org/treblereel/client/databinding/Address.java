package org.treblereel.client.databinding;

import org.treblereel.gwt.crysknife.databinding.client.api.Bindable;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 11/25/19
 */
@Bindable
public class Address {

    private String city;

    private Street street;

    public Street getStreet() {
        return street;
    }

    public void setStreet(Street street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "Address{" +
                "city='" + city + '\'' +
                ", street=" + street +
                '}';
    }
}
