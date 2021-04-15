package org.css.order.services;

import org.css.order.models.CookedOrder;
import org.css.order.models.CourierPickupMessage;
import org.css.order.models.Order;
import org.css.order.shelf.ShelfManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class CourierService implements Runnable {
    public static final Logger logger = LoggerFactory.getLogger(CourierService.class.getName());
    BlockingQueue<CourierPickupMessage> courierQueue;
    ShelfManager shelfManager;
    public CourierService(BlockingQueue<CourierPickupMessage> courierQueue, ShelfManager shelfManager) {
        this.courierQueue = courierQueue;
        this.shelfManager = shelfManager;
    }

    @Override
    public void run() {
        logger.info("Courier service thread started");
        try {
            while (true) {
                if(Thread.currentThread().isInterrupted()){
                  break;
                }
                CourierPickupMessage cp = courierQueue.poll(1000, TimeUnit.MILLISECONDS);
                if(cp == null){
                    continue;
                }
                logger.info("New pickup request received to cook. Order Id {}", cp.getOrderId());
                try {
                    CookedOrder o = shelfManager.getOrderInShelf(cp.getOrderId());
                    logger.info("Order dispatched. Order Id {}", cp.getOrderId());
                    logger.info("Ordered delivered {}",o.getOrder().getId());
                } catch (Exception e) {
                    logger.info("Order not found !!! :( {}", e.getMessage());
                }
                if(courierQueue.size() == 0){
                    Thread.currentThread().interrupt();
                    continue;
                }
            }
        } catch (InterruptedException e) {
            logger.error("Courier service thread interrupted", e);
        } finally {
            logger.info("No more message for consume. Courier pickup is closing");
        }
    }
}
