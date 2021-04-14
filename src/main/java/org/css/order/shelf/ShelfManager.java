package org.css.order.shelf;

import org.apache.commons.lang3.StringUtils;
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
    ConcurrentHashMap<String, Shelf> shelves;
    ConcurrentHashMap<String, Order> hotShelve;
    ConcurrentHashMap<String, Order> coldShelve;
    ConcurrentHashMap<String, Order> frozenShelve;
    ConcurrentHashMap<String, Order> overflowShelve;
    private final int singleTemperatureSelfCapacity;
    private final int overflowSelfCapacity;


    public ShelfManager(int singleTemperatureSelfCapacity, int overflowSelfCapacity) {
        /*this.shelves = new ConcurrentHashMap<>();
        shelves.put("hot",new HotShelf(singleTemperatureSelfCapacity));
        shelves.put("cold",new ColdShelf(singleTemperatureSelfCapacity));
        shelves.put("frozen",new FrozenShelf(singleTemperatureSelfCapacity));
        shelves.put("overflow",new OverflowShelf(overflowSelfCapacity));*/
        this.singleTemperatureSelfCapacity = singleTemperatureSelfCapacity;
        this.overflowSelfCapacity = overflowSelfCapacity;
        hotShelve = new ConcurrentHashMap<>(singleTemperatureSelfCapacity);
        coldShelve = new ConcurrentHashMap<>(singleTemperatureSelfCapacity);
        frozenShelve = new ConcurrentHashMap<>(singleTemperatureSelfCapacity);
        overflowShelve = new ConcurrentHashMap<>(overflowSelfCapacity);
    }

    public void putOrderInTheShelf(Order o) {
        String temp = o.getTemp();
        if(StringUtils.equals(o.getTemp(),"hot")){
            logger.info("Attempting to put order in hot self Order Id {}",o.getId());
            insertSingleTemperatureShelve(hotShelve,o);
        }else if(StringUtils.equals(o.getTemp(),"cold")){
            logger.info("Attempting to put order in cold self Order Id {}",o.getId());
            insertSingleTemperatureShelve(coldShelve,o);
        }else if(StringUtils.equals(o.getTemp(),"frozen")){
            logger.info("Attempting to put order in frozen self Order Id {}",o.getId());
            insertSingleTemperatureShelve(frozenShelve,o);
        }else{
            insertIntoOverflowShelve(overflowShelve,o);
        }
    }

    private void insertSingleTemperatureShelve(ConcurrentHashMap<String,Order> target, Order o){
        if (target.size() == singleTemperatureSelfCapacity){
            insertIntoOverflowShelve(overflowShelve,o);
        }else{
            target.put(o.getId(),o);
        }
    }

    private void insertIntoOverflowShelve(ConcurrentHashMap<String,Order> target, Order o){
        boolean foundPlace = false;
        if(target.size() == overflowSelfCapacity){
            for(Map.Entry<String,Order> entry :target.entrySet()){
                if(StringUtils.equals(entry.getValue().getTemp() ,"hot") && hotShelve.size() < singleTemperatureSelfCapacity){
                    hotShelve.put(entry.getValue().getId(),entry.getValue());
                    target.remove(entry.getKey());
                    foundPlace = true;
                }else if(StringUtils.equals(entry.getValue().getTemp() ,"cold") && coldShelve.size() < singleTemperatureSelfCapacity){
                    coldShelve.put(entry.getValue().getId(),entry.getValue());
                    target.remove(entry.getKey());
                    foundPlace = true;
                }else if(StringUtils.equals(entry.getValue().getTemp() ,"frozen") && frozenShelve.size() < singleTemperatureSelfCapacity){
                    frozenShelve.put(entry.getValue().getId(),entry.getValue());
                    target.remove(entry.getKey());
                    foundPlace = true;
                }
            }
        }
        if(!foundPlace){
           removeOrderRandomlyFromOverflowShelve();
        }
        target.put(o.getId(),o);
    }


    private synchronized void removeOrderRandomlyFromOverflowShelve(){
        if (overflowShelve.size() ==0){
            return;
        }
        List<String> keysAsArray = new ArrayList<>(overflowShelve.keySet());
        Random r = new Random();
        overflowShelve.remove(keysAsArray.get(r.nextInt(keysAsArray.size())));
    }

    public Order getOrderInShelf(String orderId) throws Exception {
        if(hotShelve.contains(orderId)){
            return hotShelve.get(orderId);
        }else if(coldShelve.contains(orderId)){
            return coldShelve.get(orderId);
        }else if(frozenShelve.contains(orderId)){
            return frozenShelve.get(orderId);
        }else{
            if(overflowShelve.contains(orderId)){
                return overflowShelve.get(orderId);
            }else{
                throw new Exception("Order not found");
            }
        }

    }
}
