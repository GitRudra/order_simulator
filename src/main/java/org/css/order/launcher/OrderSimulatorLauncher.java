package org.css.order.launcher;

public class OrderSimulatorLauncher {
    public static void main(String[] args) throws InterruptedException {
        OrderSimulator simulator = new OrderSimulator();
        simulator.simulateOrder(5);
    }
}
