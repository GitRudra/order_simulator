package org.css.order.models;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Order {
    private String id;
    private String name;
    private String temp;
    private Integer shelfLife;
    private Float decayRate;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTemp() {
        return temp;
    }

    public int getShelfLife() {
        return shelfLife;
    }

    public float getDecayRate() {
        return decayRate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public void setShelfLife(Integer shelfLife) {
        this.shelfLife = shelfLife;
    }

    public void setDecayRate(Float decayRate) {
        this.decayRate = decayRate;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
