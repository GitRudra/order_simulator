package org.css.order.launcher;

import org.css.order.models.PickupRequestMessage;
import org.css.order.models.Order;
import org.css.order.services.CourierService;
import org.css.order.services.OrderConsumer;
import org.css.order.services.OrderDetailsJsonParser;
import org.css.order.services.OrderProducer;
import org.css.order.shelf.ShelfManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;

/**
 * The controller class to simulate the order.
 * Start {@link OrderProducer}, {@link OrderConsumer}, {@link CourierService}
 * All the producer, consumers, courier services runs in a thread. Communication between producers, consumers & courier
 * services between happens by {@link BlockingQueue}
 */
public class OrderSimulator {
    public static final Logger logger = LoggerFactory.getLogger(OrderSimulator.class.getName());
    private final ShelfManager shelfManager;
    private final BlockingQueue<Order> producerConsumerQueue;
    private final BlockingQueue<PickupRequestMessage> dispatchQueue;

    public OrderSimulator() {
        producerConsumerQueue = new ArrayBlockingQueue<>(1000);
        dispatchQueue = new DelayQueue<>();
        int singleTemperatureShelfCapacity = 10;
        int overflowShelfCapacity = 15;
        shelfManager  = new ShelfManager(singleTemperatureShelfCapacity, overflowShelfCapacity);

    }

    /**
     * Main method to run the order simulator. Starts the producer, consumer and courier service thread.
     * The number of order can be produce in a second can be controlled by the param {@code ingestionRate}
     * @param ingestionRate - specify the number of order can be created in a second
     * @throws Exception - if failed to load order json.
     */
    public void simulateOrder(int ingestionRate) throws Exception{
        logger.info("Order simulator starting...");
        //Reading order json file for testing
        OrderDetailsJsonParser parser = new OrderDetailsJsonParser();
        Queue<Order> orderQueue = null;
        try {
            logger.info("Fetching the order details");
            orderQueue = new ArrayDeque<>(parser.getOrders());
        } catch (IOException e) {
            logger.error("Failed to parse order json",e);
            throw new Exception("Failed to parse order json.",e);
        }

        //Create producer thread
        Thread producerThread = new Thread(new OrderProducer(producerConsumerQueue,orderQueue,ingestionRate));

        //Create consumer
        Thread consumerThread1 = new Thread(new OrderConsumer(producerConsumerQueue, dispatchQueue, shelfManager));

        //Courier consumer
        Thread courierThread1 = new Thread(new CourierService(dispatchQueue,shelfManager));

        /*Thread consumerThread2 = new Thread(new OrderConsumer(producerConsumerQueue, dispatchQueue, shelfManager));
        Thread courierThread2 = new Thread(new CourierService(dispatchQueue,shelfManager));*/

        courierThread1.setName("Courier 1");
        courierThread1.start();
        consumerThread1.setName("Consumer 1");
        consumerThread1.start();
       /* courierThread2.setName("Courier 2");
        courierThread2.start();
        consumerThread2.setName("Consumer 2");
        consumerThread2.start();*/

        producerThread.start();
    }
}
