package org.css.order.launcher;

import org.css.order.models.DelayedCourierPickup;
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
import java.util.concurrent.*;

public class OrderSimulatorExecutorService {
    public static final Logger logger = LoggerFactory.getLogger(OrderSimulatorExecutorService.class.getName());
    private ShelfManager shelfManager;
    BlockingQueue<Order> producerConsumerQueue;
    BlockingQueue<DelayedCourierPickup> dispatchQueue;

    public void initiateSimulator(){
        shelfManager  = new ShelfManager(10,15);
    }

    public OrderSimulatorExecutorService() {
        producerConsumerQueue = new ArrayBlockingQueue<>(1000);
        initiateSimulator();
        dispatchQueue = new DelayQueue<>();
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

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        /*Future orderFuture = executorService.submit(new OrderProducer(producerConsumerQueue,orderQueue,ingestionRate));
        Future consumeFuture = executorService.submit(new OrderConsumer(producerConsumerQueue, dispatchQueue,"Consumer 1"));
        Future courierFuture = executorService.submit(new CourierService(dispatchQueue,"Courier Service-1"));
*/
        /*if(orderFuture.isDone() && consumeFuture.isDone() && courierFuture.isDone()){
            executorService.shutdown();
        }*/

        executorService.awaitTermination(60,TimeUnit.SECONDS);
        executorService.shutdown();

        /*Thread producerThread = new Thread(new OrderProducer(producerConsumerQueue,orderQueue,ingestionRate));
        Thread consumerThread = new Thread(new OrderConsumer(producerConsumerQueue, dispatchQueue,"Consumer 1"));
        Thread courierThread = new Thread(new CourierService(dispatchQueue,"Courier Service-1"));

        courierThread.start();
        consumerThread.start();
        producerThread.start();

        consumerThread.join();
        consumerThread.join();
        producerThread.join();*/

        logger.info("Main thread is closing");
    }
}
