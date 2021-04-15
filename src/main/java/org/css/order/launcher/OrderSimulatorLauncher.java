package org.css.order.launcher;

/**
 * Order simulator launcher
 */
public class OrderSimulatorLauncher {
    public static void main(String[] args) throws Exception {
        //Create a object of OrderSimulator
        OrderSimulator simulator = new OrderSimulator();
        //Ingestion rate
        int ingestionRate = 5;
        simulator.simulateOrder(ingestionRate);
    }
}
