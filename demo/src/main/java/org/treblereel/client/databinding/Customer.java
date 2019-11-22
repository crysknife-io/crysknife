package org.treblereel.client.databinding;

import org.treblereel.gwt.crysknife.databinding.client.api.Bindable;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 11/18/19
 */
@Bindable
public class Customer {

    private String name;

    private String city;

    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {

        this.name = name;

    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", age=" + age +
                '}';
    }
}
