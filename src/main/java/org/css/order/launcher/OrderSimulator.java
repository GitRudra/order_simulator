package org.css.order.launcher;

import org.css.order.models.CourierPickupMessage;
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
 */
public class OrderSimulator {
    public static final Logger logger = LoggerFactory.getLogger(OrderSimulator.class.getName());
    private ShelfManager shelfManager;
    BlockingQueue<Order> producerConsumerQueue;
    BlockingQueue<CourierPickupMessage> dispatchQueue;

    public OrderSimulator() {
        producerConsumerQueue = new ArrayBlockingQueue<>(1000);
        dispatchQueue = new DelayQueue<>();
        shelfManager  = new ShelfManager(10,15);

    }

    public void simulateOrder(int ingestionRate) throws InterruptedException {
        logger.info("Order simulator starting");
        OrderDetailsJsonParser parser = new OrderDetailsJsonParser();
        Queue<Order> orderQueue = null;
        try {
            logger.info("Fetching the order details");
            orderQueue = new ArrayDeque<>(parser.getOrders());
        } catch (IOException e) {
            logger.error("Failed to parse order json",e);
            e.printStackTrace();
        }

        Thread producerThread = new Thread(new OrderProducer(producerConsumerQueue,orderQueue,ingestionRate));
        Thread consumerThread1 = new Thread(new OrderConsumer(producerConsumerQueue, dispatchQueue, shelfManager));
        Thread courierThread1 = new Thread(new CourierService(dispatchQueue,shelfManager));

        courierThread1.setName("Courier 1");
        courierThread1.start();
        consumerThread1.setName("Consumer 1");
        consumerThread1.start();
        producerThread.start();

        logger.info("Main thread is closing");
    }
}
