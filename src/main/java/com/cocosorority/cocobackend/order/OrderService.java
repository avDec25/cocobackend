package com.cocosorority.cocobackend.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import io.micrometer.common.util.StringUtils;

@Service
public class OrderService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    private final String SQL_CREATE_ORDER = "INSERT INTO orders (item_id, size, customer_id, adjusted_cost) VALUES (?, ?, ?, ?)";

    public String createOrder(CreateOrderRequest request) {
        int updatedRows = jdbcTemplate.update(
            SQL_CREATE_ORDER, 
            request.itemId, 
            request.size.toUpperCase(), 
            request.customerId,
            request.adjustedCost
        );
        return String.format("Updated %d rows", updatedRows);
    }
    
}
