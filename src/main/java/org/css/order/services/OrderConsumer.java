package org.css.order.services;

import org.css.order.models.DelayedCourierPickup;
import org.css.order.models.Order;
import org.css.order.shelf.ShelfManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Time;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class OrderConsumer implements Runnable {
    public static final Logger logger = LoggerFactory.getLogger(OrderConsumer.class.getName());
    BlockingQueue<Order> producerConsumerQueue;
    BlockingQueue<DelayedCourierPickup> courierQueue;
    ShelfManager shelfManager;
    private String name;

    public OrderConsumer(BlockingQueue<Order> producerConsumerQueue,
                         BlockingQueue<DelayedCourierPickup> courierQueue,
                         ShelfManager shelfManager,
                         String name) {
        this.producerConsumerQueue = producerConsumerQueue;
        this.name = name;
        this.courierQueue = courierQueue;
        this.shelfManager = shelfManager;
    }

    @Override
    public void run() {
        logger.info("[{}] Consumer thread started", name);
        try {
            while (true) {
                if(Thread.currentThread().isInterrupted()){
                    break;
                }
                Order o = producerConsumerQueue.poll(1000, TimeUnit.MILLISECONDS);
                if(o == null){
                    Thread.currentThread().interrupt();
                    continue;
                }
                DelayedCourierPickup cp = new DelayedCourierPickup(o, getRandomDelay(2, 6));
                courierQueue.put(cp);
                logger.info("[{}] New order received to cook. Order Id {}", name, o.getId());
                logger.info("[{}] Order cooked. Order Id {}", name, o.getId());
                shelfManager.putOrderInTheShelf(o);
            }
        } catch (InterruptedException e) {
            logger.error("[{}] Consumer thread interrupted", name, e);
        } finally {
            logger.info("[{}] No more message for consume. Consumer is closing", name);
        }
    }

    public Long getRandomDelay(int min, int max) {
        Random random = new Random();
        return (random.nextInt(max - min) + min) * 1000L;
    }


}
