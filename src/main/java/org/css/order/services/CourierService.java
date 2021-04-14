package org.css.order.services;

import org.css.order.models.DelayedCourierPickup;
import org.css.order.shelf.ShelfManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CourierService implements Runnable {
    public static final Logger logger = LoggerFactory.getLogger(CourierService.class.getName());
    BlockingQueue<DelayedCourierPickup> courierQueue;
    ShelfManager shelfManager;
    private String name;

    public CourierService(BlockingQueue<DelayedCourierPickup> courierQueue, ShelfManager shelfManager,String name) {
        this.name = name;
        this.courierQueue = courierQueue;
        this.shelfManager = shelfManager;
    }

    @Override
    public void run() {
        logger.info("[{}] Courier service thread started", name);
        try {
            while (true) {
                if(!Thread.currentThread().isInterrupted()){
                    DelayedCourierPickup cp = courierQueue.poll(1000, TimeUnit.MILLISECONDS);
                    if(cp == null){
                        Thread.currentThread().interrupt();
                        continue;
                    }
                    logger.info("[{}] New pickup request received to cook. Order Id {}", name, cp.getOrder().getId());

                    try {
                        shelfManager.getOrderInShelf(cp.getOrder().getId());
                    } catch (Exception e) {
                        logger.info("[{}] Order not found !!! :(",name);
                    }
                }else{
                    break;
                }
            }
        } catch (InterruptedException e) {
            logger.error("[{}] Courier service thread interrupted", name, e);
        } finally {
            logger.info("[{}] No more message for consume. Courier pickup is closing",name);
        }
    }
}
