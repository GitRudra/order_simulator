package org.css.order.shelf;

/*
import org.css.order.models.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class SelfLifeSelector {
    public static final Logger logger = LoggerFactory.getLogger(SelfLifeSelector.class.getName())
    private ConcurrentHashMap<String,Order> hotShelf;
    private ConcurrentHashMap<String,Order> coldShelf;
    private ConcurrentHashMap<String,Order> frozenShelf;
    private ConcurrentHashMap<String,Order> overflowShelf;

    private int singleTemperatureSelfCapacity;
    private int overflowSelfCapacity;

    public SelfLifeSelector(int singleTemperatureSelfCapacity, int overflowSelfCapacity) {
        this.singleTemperatureSelfCapacity = singleTemperatureSelfCapacity;
        this.overflowSelfCapacity = overflowSelfCapacity;

        hotShelf = new ConcurrentHashMap<>(this.singleTemperatureSelfCapacity);
        coldShelf = new ConcurrentHashMap<>(this.singleTemperatureSelfCapacity);
        frozenShelf = new ConcurrentHashMap<>(this.singleTemperatureSelfCapacity);
        overflowShelf = new ConcurrentHashMap<>(this.overflowSelfCapacity);
    }

    public void putOrderInSelf(Order o){
        logger.info("received order to kept in self. Order Id {} order temp {}",o.getId(),o.getTemp());
        switch (o.getTemp()){
            case "hot":
                putOrderInHotSelf(o);
                break;
            case "cold":
                putOrderInColdSelf(o);
                break;
            case "frozen":
                putOrderInFrozenSelf(o);
                break;
        }

    }

    private void putOrderInHotSelf(Order o){
        if (hotShelf.size() == singleTemperatureSelfCapacity){
            //Put order in overflow self
        }else{
            hotShelf.put(o.getId(),o);
        }
    }

    private void putOrderInColdSelf(Order o){

    }

    private void putOrderInFrozenSelf(Order o){

    }

    private void putOrderInOverflowSelf(Order o){
        if(overflowShelf.size() == overflowSelfCapacity){

        }

    }

    private boolean isAllSingleTemperatureSelfFull{
        return hotShelf.size()==singleTemperatureSelfCapacity &&
                coldShelf.size() == singleTemperatureSelfCapacity &&
                frozenShelf.size() == singleTemperatureSelfCapacity
    }

}
*/
