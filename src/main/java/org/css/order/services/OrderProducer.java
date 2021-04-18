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
    private BlockingQueue<Order> producerConsumerQueue;
    private Queue<Order> orderSourceQueue;
    private int ingestionRate;

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
            checkInitialisation();
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
        } catch (Exception e) {
            logger.error("Failed to run the producer. {}", e.getMessage(),e);
        } finally {
            logger.info("No more message for ingestion. Order producer closed");
        }
    }

    private void checkInitialisation()throws Exception{
        if(producerConsumerQueue == null || orderSourceQueue == null){
            throw new Exception("All the necessary component has not initialized");
        }
    }

    /**
     * Helper method to get the count of remaining order in the order source queue
     * @return int
     */
    public boolean hasMoreOrder(){
        return !orderSourceQueue.isEmpty();
    }

    /**
     * Helper method to get the pending message in the {@code producerConsumerQueue}
     * @return count of messages in producerConsumer queue.
     */
    public int getMessagesCount(){
        return producerConsumerQueue.size();
    }
}
