package org.css.order.services;

import org.css.order.models.Order;
import org.css.order.models.PickupRequestMessage;
import org.css.order.shelf.ShelfManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;

import static org.junit.jupiter.api.Assertions.*;

class OrderProducerTest {
    BlockingQueue<Order> producerConsumerQueue;
    Queue<Order> orderList;

    @BeforeEach
    void setUp() {
        producerConsumerQueue = new ArrayBlockingQueue(5);
        orderList = new ArrayDeque<>();
    }

    @Test
    @DisplayName("Single order with single ingestion rate")
    void testOrderProducerServiceOne() throws Exception {
        Order o1 = createAOrder("id1","hot",100,.35F,"Order 1");
        orderList.add(o1);
        int ingestionRate = 1;
        OrderProducer op = new OrderProducer(producerConsumerQueue,orderList,ingestionRate);
        Thread t1 =  new Thread(op);
        t1.start();
        assertTrue(op.hasMoreOrder());
    }

    @Test
    @DisplayName("Test order producer with number of order less than ingestion rate")
    void testOrderProducerServiceTwo() throws Exception {
        Order o1 = createAOrder("id1","hot",100,.35F,"Order 1");
        Order o2 = createAOrder("id2","hot",50,.35F,"Order 2");
        orderList.add(o1);
        orderList.add(o2);
        int ingestionRate = 5;
        OrderProducer op = new OrderProducer(producerConsumerQueue,orderList,ingestionRate);
        Thread t1 =  new Thread(op);
        t1.start();
        assertTrue(op.hasMoreOrder());
    }

    private Order createAOrder(String id,String temp, Integer shelfLife, Float decayRate, String name){
        Order o1 = new Order();
        o1.setId(id);
        o1.setTemp(temp);
        o1.setShelfLife(shelfLife);
        o1.setDecayRate(decayRate);
        o1.setName(name);
        return o1;
    }
}