package org.css.order.shelf;

import org.css.order.models.Order;

public interface Shelf {
    boolean putOrder(Order o);
    Order getOrder(String orderId);
    int getCapacity();
    boolean isEmpty();
    boolean isFull();
    boolean contains(String key);
    String getShelfType();
}
