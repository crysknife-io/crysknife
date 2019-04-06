package org.treblereel.client.events;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/4/19
 */
public class Address {

    private int id;

    private String name;

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
