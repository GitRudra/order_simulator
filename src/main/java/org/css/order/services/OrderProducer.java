package org.css.order.services;

import org.css.order.models.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public class OrderProducer implements Runnable{
    public static final Logger logger = LoggerFactory.getLogger(OrderProducer.class.getName());
    BlockingQueue<Order> producerConsumerQueue;
    Queue<Order> orderSourceQueue;
    CountDownLatch latch;
    int ingestionRate;

    public OrderProducer(BlockingQueue<Order> producerConsumerQueue, Queue<Order> orderSourceQueue,int ingestionRate) {
        this.producerConsumerQueue = producerConsumerQueue;
        this.orderSourceQueue = orderSourceQueue;
        this.ingestionRate = ingestionRate;
    }


    @Override
    public void run() {
        try {
            while (!orderSourceQueue.isEmpty()) {
                logger.info("Ingesting the message {}",orderSourceQueue.peek().getId());
                producerConsumerQueue.add(orderSourceQueue.remove());
                if (!orderSourceQueue.isEmpty()) {
                    logger.info("Ingesting the message {}",orderSourceQueue.peek().getId());
                    producerConsumerQueue.add(orderSourceQueue.remove());
                }
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            logger.warn("Producer thread interrupted", e);
        } finally {
            logger.info("No more message for ingestion. Producer is closing");
        }
    }
}
