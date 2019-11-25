package org.treblereel.client.databinding;

import org.treblereel.gwt.crysknife.databinding.client.api.Bindable;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 11/18/19
 */
@Bindable
public class Customer {

    private String name;

    private Address address;

    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {

        this.name = name;

    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
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
                ", address='" + address + '\'' +
                ", age=" + age +
                '}';
    }
}
