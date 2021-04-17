package org.css.order.services;

import org.css.order.models.Order;

import java.io.IOException;
import java.util.List;

/**
 * Interface for order parsing
 */
public interface OrderDetailsParser {
    String getFileName();
    List<Order> getOrders() throws IOException;
}
