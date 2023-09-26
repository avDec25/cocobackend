package com.cocosorority.cocobackend.order;

import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    private final String SQL_CREATE_ORDER = "INSERT INTO orders (item_id, size, customer_id, adjusted_cost) VALUES %s";

    public String createOrder(CustomerOrderRequest customerOrder) {
        StringJoiner valuesJoiner = new StringJoiner(",");
        for (ItemInOrder item: customerOrder.items) {
            StringJoiner combiner = new StringJoiner("','", "('", "')");
            combiner.add(item.itemId);
            combiner.add(item.size.toUpperCase());
            combiner.add(customerOrder.customerId);
            combiner.add(item.adjustedCost);
            valuesJoiner.add(combiner.toString());
        }
        int updatedRows = jdbcTemplate.update(
            String.format(SQL_CREATE_ORDER, valuesJoiner.toString())
        );
        return String.format("Updated %d rows", updatedRows);
    }
    
}
