package org.css.order.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * The class {@PickupRequestMessage} is an immutable class which represents an pickup message.
 * It implements {@link Delayed} to hold some delay before deliver to the courier service.
 */
public class PickupRequestMessage implements Delayed {
    private static final Logger logger = LoggerFactory.getLogger(PickupRequestMessage.class.getName());
    private final String orderId;
    private final Long dequeueTime;

    public PickupRequestMessage(String orderId, Long delay) {
        this.orderId = orderId;
        dequeueTime = System.currentTimeMillis()+delay;
    }

    public String getOrderId() {
        return orderId;
    }

    public Long getDequeueTime() {
        return dequeueTime;
    }

    /**
     * Returns the remaining delay associated with the object.
     * @param unit - {@link TimeUnit}
     * @return remaining delay {@link Long}
     */
    @Override
    public long getDelay(TimeUnit unit) {
        long diff = dequeueTime - System.currentTimeMillis();
        return unit.convert(diff,TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        PickupRequestMessage p = (PickupRequestMessage)o;
        return Long.compare(this.dequeueTime,p.dequeueTime);
    }

    @Override
    public String toString() {
        return "DelayedCourierPickup{" +
                "order=" + orderId +
                ", dequeueTime=" + dequeueTime +
                '}';
    }
}
