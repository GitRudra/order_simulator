package org.css.order.shelf;

import org.apache.commons.lang3.StringUtils;
import org.css.order.models.CookedOrder;
import org.css.order.models.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class ShelfManager {
    private static final Logger logger  = LoggerFactory.getLogger(ShelfManager.class.getName());
    ConcurrentHashMap<String, CookedOrder> hotShelve;
    ConcurrentHashMap<String, CookedOrder> coldShelve;
    ConcurrentHashMap<String, CookedOrder> frozenShelve;
    ConcurrentHashMap<String, CookedOrder> overflowShelve;
    private final int singleTemperatureSelfCapacity;
    private final int overflowSelfCapacity;


    public ShelfManager(int singleTemperatureSelfCapacity, int overflowSelfCapacity) {
        this.singleTemperatureSelfCapacity = singleTemperatureSelfCapacity;
        this.overflowSelfCapacity = overflowSelfCapacity;
        hotShelve = new ConcurrentHashMap<>(singleTemperatureSelfCapacity);
        coldShelve = new ConcurrentHashMap<>(singleTemperatureSelfCapacity);
        frozenShelve = new ConcurrentHashMap<>(singleTemperatureSelfCapacity);
        overflowShelve = new ConcurrentHashMap<>(overflowSelfCapacity);
    }

    public void putOrderInTheShelf(CookedOrder cookedOrder) {
        String temp = cookedOrder.getOrder().getTemp();
        Order o = cookedOrder.getOrder();
        if(StringUtils.equals(o.getTemp(),"hot")){
            logger.info("Attempting to put order in hot self Order Id {}",o.getId());
            insertSingleTemperatureShelve(hotShelve,cookedOrder);
        }else if(StringUtils.equals(o.getTemp(),"cold")){
            logger.info("Attempting to put order in cold self Order Id {}",o.getId());
            insertSingleTemperatureShelve(coldShelve,cookedOrder);
        }else if(StringUtils.equals(o.getTemp(),"frozen")){
            logger.info("Attempting to put order in frozen self Order Id {}",o.getId());
            insertSingleTemperatureShelve(frozenShelve,cookedOrder);
        }else{
            insertIntoOverflowShelve(overflowShelve,cookedOrder);
        }
        logger.info("successfully kept the order in the shelf. Order Id {}",o.getId());
    }

    private void insertSingleTemperatureShelve(ConcurrentHashMap<String,CookedOrder> target, CookedOrder cookedOrder){
        if (target.size() == singleTemperatureSelfCapacity){
            logger.info("Target shelf is full. Attempting to put the order in the overflow shelf.");
            insertIntoOverflowShelve(overflowShelve,cookedOrder);
        }else{
            target.put(cookedOrder.getOrder().getId(),cookedOrder);
            cookedOrder.setKeptSingleTemperatureShelf(true);
        }
    }

    private void insertIntoOverflowShelve(ConcurrentHashMap<String,CookedOrder> target, CookedOrder cookedOrder){
        boolean foundPlace = false;
        if(target.size() == overflowSelfCapacity){
            for(Map.Entry<String,CookedOrder> entry :target.entrySet()){
                Order order = entry.getValue().getOrder();
                if(StringUtils.equals(order.getTemp() ,"hot") && hotShelve.size() < singleTemperatureSelfCapacity){
                    logger.info("Moving hot order from overflow shelf to hot shelf. Order Id {}",order.getId());
                    cookedOrder.setKeptSingleTemperatureShelf(true);
                    hotShelve.put(order.getId(),cookedOrder);
                    target.remove(order.getId());
                    foundPlace = true;
                }else if(StringUtils.equals(order.getTemp() ,"cold") && coldShelve.size() < singleTemperatureSelfCapacity){
                    logger.info("Moving cold order from overflow shelf to cold shelf. Order Id {}",order.getId());
                    cookedOrder.setKeptSingleTemperatureShelf(true);
                    coldShelve.put(order.getId(),cookedOrder);
                    target.remove(order.getId());
                    foundPlace = true;
                }else if(StringUtils.equals(order.getTemp() ,"frozen") && frozenShelve.size() < singleTemperatureSelfCapacity){
                    logger.info("Moving frozen order from overflow shelf to frozen shelf. Order Id {}",order.getId());
                    cookedOrder.setKeptSingleTemperatureShelf(true);
                    frozenShelve.put(order.getId(),cookedOrder);
                    target.remove(order.getId());
                    foundPlace = true;
                }
            }
        }
        if(!foundPlace){
           removeOrderRandomlyFromOverflowShelve();
        }
        target.put(cookedOrder.getOrder().getId(),cookedOrder);
        cookedOrder.setKeptSingleTemperatureShelf(false);
    }


    private synchronized void removeOrderRandomlyFromOverflowShelve(){
        logger.info("[{}]removing random order from overflow shelf",Thread.currentThread().getName());
        if (overflowShelve.size() ==0){
            return;
        }
        List<String> keysAsArray = new ArrayList<>(overflowShelve.keySet());
        Random r = new Random();
        overflowShelve.remove(keysAsArray.get(r.nextInt(keysAsArray.size())));
    }

    public CookedOrder getOrderInShelf(String orderId) throws Exception {
        if(hotShelve.get(orderId) != null){
            CookedOrder o = hotShelve.remove(orderId);
            if(o.isOrderWasted()){
                throw new Exception("Order wasted");
            }
            return o;
        }else if(coldShelve.get(orderId) != null){
            CookedOrder o = coldShelve.remove(orderId);
            if(o.isOrderWasted()){
                throw new Exception("Order wasted");
            }
            return o;
        }else if(frozenShelve.get(orderId) != null){
            CookedOrder o = frozenShelve.remove(orderId);
            if(o.isOrderWasted()){
                throw new Exception("Order wasted");
            }
            return o;
        }else{
            if(overflowShelve.get(orderId) != null){
                CookedOrder o = overflowShelve.remove(orderId);
                if(o.isOrderWasted()){
                    throw new Exception("Order wasted");
                }
                return o;
            }else{
                throw new Exception("Order not found");
            }
        }

    }
}
