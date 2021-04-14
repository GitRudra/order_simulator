package org.css.order.shelf;

import org.css.order.models.Order;

import java.util.concurrent.ConcurrentHashMap;

public class FrozenShelf implements Shelf{
    private ConcurrentHashMap<String, Order> racks;
    private int CAPACITY;
    private String shelfType = "frozen";

    public FrozenShelf(int capacity) {
        this.CAPACITY = capacity;
        racks = new ConcurrentHashMap<>(CAPACITY);
    }

    @Override
    public boolean putOrder(Order o) {
        if(o.getTemp() == shelfType){
            racks.put(o.getId(),o);
            return true;
        }
        return false;
    }

    @Override
    public Order getOrder(String orderId) {
        return racks.remove(orderId);
    }

    @Override
    public int getCapacity() {
        return CAPACITY;
    }

    @Override
    public boolean isEmpty() {
        return racks.isEmpty();
    }

    @Override
    public boolean isFull() {
        return racks.size() == CAPACITY;
    }

    @Override
    public String getShelfType() {
        return null;
    }

    @Override
    public boolean contains(String key) {
        return racks.contains(key);
    }

}
