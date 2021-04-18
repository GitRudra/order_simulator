# Order Simulator
## Requirement
Create a order simulator which will take the order and cooked the order and send the order for dispatch.

## Use cases
1. The order producer will run until there is a order left.
2. The order consumer will poll for the order once the order. Once the order recived 
   the consumer will cook the order instantly and send a message for pickup.
3. The pickup service will receive a message with a delay randomly between 2 and 6 sec
4. Once the pickup is done, the order will be delivered instantly.

## How to run the code ?
Run the main method in the class <code>src/main/java/org/css/order/launcher/OrderSimulatorLauncher.java</code>
The method <code>simulateOrder</code> takes the value of ingestion rate.

## Assumption
1. There are only single producer which will be feed by a list of Order during producer creation.
2. There might be multiple consumer and courier service

## Shelf manager
For managing the cooked order in the shelf following logic has been implemented.

### Logic to put order in shelf
1. When an order is cooked,  we associate a timestamp with the order to indicate <code>orderShelvedTime</code>
2. Check for the shelf availability depending on the <code>temp</code> field in the order.
3. If the shelf is full then it will check for overflow shelf.
4. If overflow shelf is also full, it will then check for any order which can be moved
   to corresponding shelf (if available) based on the <code>temp</code> in the order.
5. If no place found at all then trash a random order from the overflow shelf.

### Logic to get order from the shelf
1. Courier service after receiving an order search for the order in the shelf.
2. If the order not found then the <code>ShelfManager</code> throws exception. 
   Based on what courier service decide the order availability.
3. If the order is wasted based on the formula provided in challenge prompt will be considered
   as order not found.

## Overall design:
1. Created three different thread one for each Order Producer, Order Consumer, and Courier Service.
2. The message will be transmitted from one thread to other through blocking queue.
3. In microservice architecture each and every thread will be replaced by individual microservices.
4. Blocking queue will be replaced by distributed queue like kafka.
5. Shelf manager can be replaced by redis with persistent enable.
