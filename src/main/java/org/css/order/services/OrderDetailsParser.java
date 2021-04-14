package org.css.order.services;

import org.css.order.models.Order;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface OrderDetailsParser {
    public String getFileName();
    public List<Order> getOrders() throws IOException;
}
