package org.css.order.services;

import org.css.order.models.Order;
import org.css.order.models.CookedOrder;
import org.css.order.models.PickupRequestMessage;
import org.css.order.shelf.ShelfManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class OrderConsumer implements Runnable {
    public static final Logger logger = LoggerFactory.getLogger(OrderConsumer.class.getName());
    BlockingQueue<Order> producerConsumerQueue;
    BlockingQueue<PickupRequestMessage> courierQueue;
    ShelfManager shelfManager;

    public OrderConsumer(BlockingQueue<Order> producerConsumerQueue,
                         BlockingQueue<PickupRequestMessage> courierQueue,
                         ShelfManager shelfManager) {
        this.producerConsumerQueue = producerConsumerQueue;
        this.courierQueue = courierQueue;
        this.shelfManager = shelfManager;
    }

    @Override
    public void run() {
        logger.info("Consumer thread started");
        try {
            while (true) {
                if(Thread.currentThread().isInterrupted()){
                    break;
                }
                Order o = producerConsumerQueue.poll(2000, TimeUnit.MILLISECONDS);
                if(o == null){
                    Thread.currentThread().interrupt();
                    continue;
                }
                PickupRequestMessage cp = new PickupRequestMessage(o.getId(), getRandomDelay(2, 6));
                courierQueue.put(cp);
                logger.info("New order received to cook. Order Id {}", o.getId());
                CookedOrder cookedOrder = createCookedOrder(o);
                logger.info("Order cooked. Order Id {}", cookedOrder);
                shelfManager.putOrderInTheShelf(cookedOrder);
            }
        } catch (InterruptedException e) {
            logger.error("Consumer thread interrupted", e);
        } finally {
            logger.info("No more message for consume. Order Consumer closed");
        }
    }

    public Long getRandomDelay(int min, int max) {
        Random random = new Random();
        return (random.nextInt(max - min) + min) * 1000L;
    }

    private CookedOrder createCookedOrder(Order o){
        CookedOrder cookedOrder = new CookedOrder(o);
        return cookedOrder;
    }


}
