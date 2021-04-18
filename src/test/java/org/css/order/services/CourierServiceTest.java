package org.css.order.services;

import org.css.order.models.Order;
import org.css.order.models.PickupRequestMessage;
import org.css.order.shelf.ShelfManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;

import static org.junit.jupiter.api.Assertions.*;

class CourierServiceTest {
    BlockingQueue<Order> producerConsumerQueue;
    ShelfManager s;
    OrderConsumer oc;
    CourierService cs;
    DelayQueue<PickupRequestMessage> courierQueue;

    @BeforeEach
    void setUp() {
        producerConsumerQueue = new ArrayBlockingQueue(5);
        s = new ShelfManager(1,1);
        courierQueue = new DelayQueue<>();
        oc = new OrderConsumer(producerConsumerQueue,courierQueue,s);
        cs = new CourierService(courierQueue,s);

    }

    @Test
    @DisplayName("Testing single order for the order consumer")
    void testCourierServiceWithOneOrderInQueue() throws Exception {
        Order o1 = createAOrder("id1","hot",100,.35F,"Order 1");
        producerConsumerQueue.add(o1);
        Thread t1 =  new Thread(oc);
        Thread t2 = new Thread(cs);
        t2.run();
        t1.run();

        assertTrue(cs.courierQueue.isEmpty());
    }

    @Test
    @DisplayName("Testing two order for the order consumer with shelf capacity 1")
    void testCourierServiceWithTwoOrderInQueue() throws Exception {
        Order o1 = createAOrder("id1","hot",100,.35F,"Order 1");
        Order o2 = createAOrder("id2","hot",50,.35F,"Order 2");
        producerConsumerQueue.add(o1);
        producerConsumerQueue.add(o2);
        Thread t1 =  new Thread(oc);
        Thread t2 = new Thread(cs);
        t2.run();
        t1.run();
        assertTrue(cs.courierQueue.isEmpty());
    }

    @Test
    @DisplayName("Testing three order for the order consumer with shelf capacity 1: Non deterministic about retrival")
    void testCourierServiceWhenThreeOrderInQueue() throws Exception {
        Order o1 = createAOrder("id1","hot",100,.35F,"Order 1");
        Order o2 = createAOrder("id2","hot",50,.35F,"Order 2");
        Order o3 = createAOrder("id3","hot",75,.35F,"Order 3");

        producerConsumerQueue.add(o1);
        producerConsumerQueue.add(o2);
        producerConsumerQueue.add(o3);
        Thread t1 =  new Thread(oc);
        Thread t2 = new Thread(cs);
        t2.run();
        t1.run();

        assertTrue(cs.courierQueue.isEmpty());
        assertThrows(Exception.class,()->{
            oc.shelfManager.getOrderInShelf(o3.getId());
        });
    }

    @Test
    void getRandomDelay() {
        OrderConsumer consumer = new OrderConsumer(null,null,null);
        System.out.println(consumer.getRandomDelay(2,6));
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