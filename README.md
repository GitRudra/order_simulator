# Order Simulator
## Requirement
Create a order simulator which will take the order and cooked the order and send the order for dispatch.

##Use cases
1. The order producer will run until there is a order left.
2. The order consumer will poll for the order once the order. Once the order recived 
   the consumer will cook the order instantly and send a message for pickup.
3. The pickup service will receive a message with a delay randomly between 2 and 6 sec
4. Once the pickup is done the order will be delivered instantly.

