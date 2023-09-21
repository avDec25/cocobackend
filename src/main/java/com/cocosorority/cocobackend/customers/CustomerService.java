package com.cocosorority.cocobackend.customers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
    @Autowired
    JdbcTemplate jdbcTemplate;

    private final String SQL_INSERT_CUSTOMER = "INSERT INTO customers (name, phone, address, email, social_id, pincode) VALUES (?, ?, ?, ?, ?, ?)";

    public String saveCustomer(CustomerSaveRequest request) {
        int updatedRows = jdbcTemplate.update(SQL_INSERT_CUSTOMER, 
            request.name, 
            request.phone, 
            request.address, 
            request.email, 
            request.socialId,
            request.pincode
        );
        return String.format("Updated %d rows", updatedRows);
    }
    
}
