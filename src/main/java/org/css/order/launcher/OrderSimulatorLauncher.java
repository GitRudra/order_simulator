package org.css.order.launcher;

import org.css.order.services.OrderDetailsJsonParser;

import java.io.FileNotFoundException;
import java.io.IOException;

public class OrderSimulatorLauncher {
    public static void main(String[] args) throws IOException, InterruptedException {
        OrderSimulator simulator = new OrderSimulator();
        simulator.simulateOrder(2);
//        Thread.currentThread().wait();
    }
}
