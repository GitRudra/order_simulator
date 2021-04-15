package org.css.order.services;

import org.css.order.models.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;

/**
 * {@code OrderProducer} to produce the oder. It takes the order from the array containing order list.
 * @author rudrapal
 */
public class OrderProducer implements Runnable{
    public static final Logger logger = LoggerFactory.getLogger(OrderProducer.class.getName());
    BlockingQueue<Order> producerConsumerQueue;
    Queue<Order> orderSourceQueue;
    int ingestionRate;

    /**
     * Constructor to create a {@code OrderProducer} object
     * @param producerConsumerQueue - {@link BlockingQueue} to create order for the consumer
     * @param orderSourceQueue -
     * @param ingestionRate
     */
    public OrderProducer(BlockingQueue<Order> producerConsumerQueue, Queue<Order> orderSourceQueue,int ingestionRate) {
        this.producerConsumerQueue = producerConsumerQueue;
        this.orderSourceQueue = orderSourceQueue;
        this.ingestionRate = ingestionRate;
    }


    /**
     * Run method to start the order producer thread.
     * Takes the input from {@code orderSourceQueue}.
     * The run method is interrupt able code
     */
    @Override
    public void run() {
        try {
            while (true) {
                if(Thread.currentThread().isInterrupted()){
                    break;
                }
                for(int i=0;i<ingestionRate && !orderSourceQueue.isEmpty(); i++){
                    logger.info("Ingesting the message {}",orderSourceQueue.peek().getId());
                    producerConsumerQueue.add(orderSourceQueue.remove());
                }

                if (orderSourceQueue.isEmpty()) {
                    Thread.currentThread().interrupt();
                    continue;
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
