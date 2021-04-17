package org.css.order.shelf;

import org.css.order.models.CookedOrder;
import org.css.order.models.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShelfManagerTest {

    @Test
    void testShelfManagerOrderFound() throws Exception {
        ShelfManager shelfManager = new ShelfManager(2,4);
        Order o1 = new Order();
        o1.setId("id1");
        o1.setTemp("hot");
        o1.setShelfLife(300);
        o1.setDecayRate(.35F);
        o1.setName("Test Order");
        CookedOrder cd  =  new CookedOrder(o1);
        shelfManager.putOrderInTheShelf(cd);
        CookedOrder expected = shelfManager.getOrderInShelf("id1");
        assertEquals(cd.getOrder().getId(),expected.getOrder().getId());
    }

    @Test()
    void testShelfManagerOrderNOtFound() throws Exception {
        ShelfManager shelfManager = new ShelfManager(2,4);
        Order o1 = new Order();
        o1.setId("id1");
        o1.setTemp("hot");
        o1.setShelfLife(300);
        o1.setDecayRate(.35F);
        o1.setName("Test Order");
        CookedOrder cd  =  new CookedOrder(o1);
        shelfManager.putOrderInTheShelf(cd);
//        CookedOrder expected = shelfManager.getOrderInShelf("id12");
        assertThrows(Exception.class,()->{
            shelfManager.getOrderInShelf("id12");
        });
//        assertEquals(cd.getOrder().getId(),expected.getOrder().getId());
    }

    @Test
    void getOrderInShelf() {
    }
}