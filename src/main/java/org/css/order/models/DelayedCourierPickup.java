package org.css.order.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class DelayedCourierPickup implements Delayed {
    private static final Logger logger = LoggerFactory.getLogger(DelayedCourierPickup.class.getName());
    private Order order;
    private Long dequeueTime;

    public DelayedCourierPickup(Order order, Long delay) {
        this.order = order;
        dequeueTime = System.currentTimeMillis()+delay;
    }

    public Order getOrder() {
        return order;
    }

    public Long getDequeueTime() {
        return dequeueTime;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        Long diff = dequeueTime - System.currentTimeMillis();
        return unit.convert(diff,TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        DelayedCourierPickup p = (DelayedCourierPickup)o;
        return Long.compare(this.dequeueTime,p.dequeueTime);
    }

    @Override
    public String toString() {
        return "DelayedCourierPickup{" +
                "order=" + order.getId() +
                ", dequeueTime=" + dequeueTime +
                '}';
    }
}
