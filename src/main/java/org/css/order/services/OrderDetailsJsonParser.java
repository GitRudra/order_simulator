package org.css.order.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.css.order.models.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Class to parse the order json.
 */
public class OrderDetailsJsonParser implements OrderDetailsParser{
    public static final Logger logger = LoggerFactory.getLogger(OrderDetailsJsonParser.class);

    /**
     * Return the order file name
     * @return - order file name.
     */
    @Override
    public String getFileName() {
        return "order.json";
    }

    /**
     * Parse the order json and create a {@code ArrayList} of {@link Order}.
     * @return - Array list of Order
     * @throws IOException if the order file not found.
     */
    @Override
    public List<Order> getOrders() throws IOException {
        logger.info("Loading the orders from the file ....");
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Order>>() {}.getType();
        String jsonContent = loadFileFromClasspath(getFileName());
        List<Order> list = gson.fromJson(jsonContent,listType);
        logger.info("Loaded all the orders. order count = "+list.size());
        return list;
    }

    private String loadFileFromClasspath(String fileName) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(fileName)) {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }
    }
}
