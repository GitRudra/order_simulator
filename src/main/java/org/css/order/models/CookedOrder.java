package org.css.order.models;

/**
 * {@code CookedOrder} is the wrapper class of order. The class stores the inherentValue and order shelved value.
 * @author rudrapal
 */
public class CookedOrder {
    /* The Original Order*/
    private final Order o;

    private final Float inherentValue;
    private final Long orderShelvedTime;
    private Boolean keptSingleTemperatureShelf;

    public CookedOrder(Order o) {
        this.o = o;
        this.keptSingleTemperatureShelf = true;
        this.orderShelvedTime = System.currentTimeMillis();
        this.inherentValue = calculateOrderValue();
    }

    public Order getOrder(){
        return o;
    }

    public boolean isOrderWasted() {
        float orderCurrentValue = calculateOrderValue();
        //Added the condition (inherentValue != orderCurrentValue) to prevent the case when order is picked up
        //instantly after putting into shelf.
        return (inherentValue != orderCurrentValue) && (inherentValue - orderCurrentValue == 0);
    }

    public Float getInherentValue() {
        return inherentValue;
    }

    public Long getOrderShelvedTime() {
        return orderShelvedTime;
    }

    public Boolean getKeptSingleTemperatureShelf() {
        return keptSingleTemperatureShelf;
    }

    /**
     * Set the flag to check whether the order is in single temperature shelf or overflow shelve
     * @param keptSingleTemperatureShelf
     */
    public void setKeptSingleTemperatureShelf(Boolean keptSingleTemperatureShelf) {
        this.keptSingleTemperatureShelf = keptSingleTemperatureShelf;
    }

    /**
     * Calculate the order current value when invoked.
     * @return order value - {@code Long}
     */
    public Float calculateOrderValue() {
        long orderAge = (System.currentTimeMillis() - orderShelvedTime)/1000;
        int shelfDecayModifier = keptSingleTemperatureShelf ? 1 : 2;
        float value = (this.o.getShelfLife() - orderAge - (this.o.getDecayRate() * orderAge * shelfDecayModifier)) / this.o.getShelfLife();
        return value;
    }

    @Override
    public String toString() {
        return "CookedOrder{" +
                "o=" + o +
                ", inherentValue=" + inherentValue +
                ", orderShelvedTime=" + orderShelvedTime +
                ", orderValue=" + calculateOrderValue() +
                '}';
    }
}
