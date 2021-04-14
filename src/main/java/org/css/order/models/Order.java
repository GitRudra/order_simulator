package org.css.order.models;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Order {
    private String id;
    private String name;
    private String temp;
    private int shelfLife;
    private float decayRate;

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
}
