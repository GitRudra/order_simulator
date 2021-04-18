package org.css.order.services;

import org.css.order.models.CookedOrder;
import org.css.order.models.Order;
import org.css.order.models.PickupRequestMessage;
import org.css.order.shelf.ShelfManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;

import static org.junit.jupiter.api.Assertions.*;

class OrderConsumerTest {

    @Test
    @DisplayName("Testing single order for the order consumer")
    void testOrderConsumeWithOneOrderInQueue() throws Exception {
        BlockingQueue<Order> producerConsumerQueue = new ArrayBlockingQueue(5);
        Order o1 = createAOrder("id1","hot",100,.35F,"Order 1");
        producerConsumerQueue.add(o1);
        DelayQueue<PickupRequestMessage> courierQueue = new DelayQueue<>();
        ShelfManager s = new ShelfManager(1,1);
        OrderConsumer oc = new OrderConsumer(producerConsumerQueue,courierQueue,s);
        oc.run();
        assertTrue(oc.producerConsumerQueue.isEmpty());
        assertEquals(1,oc.courierQueue.size(),"Size mismatch in courier queue");
        assertEquals(o1.getId(),oc.shelfManager.getOrderInShelf(o1.getId()).getOrder().getId(),"Received different order from the shelf");

    }

    @Test
    @DisplayName("Testing two order for the order consumer with shelf capacity 1")
    void testOrderConsumerWhenNoOrderInQueue() throws Exception {
        BlockingQueue<Order> producerConsumerQueue = new ArrayBlockingQueue(5);
        Order o1 = createAOrder("id1","hot",100,.35F,"Order 1");
        Order o2 = createAOrder("id2","hot",50,.35F,"Order 2");
        producerConsumerQueue.add(o1);
        producerConsumerQueue.add(o2);
        DelayQueue<PickupRequestMessage> courierQueue = new DelayQueue<>();
        ShelfManager s = new ShelfManager(1,1);
        OrderConsumer oc = new OrderConsumer(producerConsumerQueue,courierQueue,s);
        oc.run();
        assertTrue(oc.producerConsumerQueue.isEmpty());
        assertEquals(2,oc.courierQueue.size(),"Size mismatch in courier queue");
        assertEquals(o1.getId(),oc.shelfManager.getOrderInShelf(o1.getId()).getOrder().getId(),"Received different order from the shelf");
        assertEquals(o2.getId(),oc.shelfManager.getOrderInShelf(o2.getId()).getOrder().getId(),"Received different order from the shelf");
    }

    @Test
    @DisplayName("Testing three order for the order consumer with shelf capacity 1: Non deterministic about retrival")
    void testOrderConsumerWhen3OrderInQueue() throws Exception {
        BlockingQueue<Order> producerConsumerQueue = new ArrayBlockingQueue(5);
        Order o1 = createAOrder("id1","hot",100,.35F,"Order 1");
        Order o2 = createAOrder("id2","hot",50,.35F,"Order 2");
        Order o3 = createAOrder("id3","hot",75,.35F,"Order 3");
        producerConsumerQueue.add(o1);
        producerConsumerQueue.add(o2);
        producerConsumerQueue.add(o3);
        DelayQueue<PickupRequestMessage> courierQueue = new DelayQueue<>();
        ShelfManager s = new ShelfManager(1,1);
        OrderConsumer oc = new OrderConsumer(producerConsumerQueue,courierQueue,s);
        oc.run();
        assertTrue(oc.producerConsumerQueue.isEmpty());
        assertEquals(3,oc.courierQueue.size(),"Size mismatch in courier queue");
        assertEquals(o1.getId(),oc.shelfManager.getOrderInShelf(o1.getId()).getOrder().getId(),"Received different order from the shelf");
        assertEquals(o3.getId(),oc.shelfManager.getOrderInShelf(o3.getId()).getOrder().getId(),"Received different order from the shelf");
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