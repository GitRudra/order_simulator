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

/**
 * {@code OrderConsumer} class consume the order created by the {@link OrderProducer}
 * After consuming the order it placed the order in the respective shelf and
 * put a message for the {@link CourierService} to pickup the cooked order.
 */
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

    /**
     * The run method of {@link OrderConsumer} class poll the queue {@code producerConsumerQueue} with time out of 2000
     * milliseconds. If the polls timed out and found no message then it set the <code>interrupt</code> flag on.
     * So in the next execution of the loop the thread will stop execution.
     */
    @Override
    public void run() {
        logger.info("Consumer thread started");
        try {
            checkInitialisation();
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
        } catch(Exception e){
            logger.error("Failed to run the consumer. {}", e.getMessage(),e);
        }finally {
            logger.info("No more message for consume. Order Consumer closed");
        }
    }

    /**
     * The method will create a delay so that the pickup service will picked the
     * order within a random delay of 2 to 6 sec.
     * @param min - minumum delay
     * @param max - maximum delay
     * @return random delay between {@code min} and {@code max}
     */
    public Long getRandomDelay(int min, int max) {
        Random random = new Random();
        return (random.nextInt(max - min) + min) * 1000L;
    }

    private void checkInitialisation()throws Exception{
        if(producerConsumerQueue == null || courierQueue == null || shelfManager == null){
            throw new Exception("All the necessary component has not initialized");
        }
    }

    private CookedOrder createCookedOrder(Order o){
        CookedOrder cookedOrder = new CookedOrder(o);
        return cookedOrder;
    }


}
