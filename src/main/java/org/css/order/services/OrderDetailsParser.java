package org.css.order.services;

import org.css.order.models.Order;

import java.io.IOException;
import java.util.List;

public interface OrderDetailsParser {
    String getFileName();
    List<Order> getOrders() throws IOException;
}
