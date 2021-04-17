package org.css.order.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderConsumerTest {

    @Test
    void run() {
    }

    @Test
    void getRandomDelay() {
        OrderConsumer consumer = new OrderConsumer(null,null,null);
        System.out.println(consumer.getRandomDelay(2,6));
    }
}