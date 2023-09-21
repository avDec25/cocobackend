package com.cocosorority.cocobackend.drop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class DropService {
    
    @Autowired
    JdbcTemplate jdbcTemplate;

    public final String SQL_SAVE_DROP = "INSERT INTO drops (drop_id, name, description) VALUES (?, ?, ?)";

    public String saveDrop(DropSaveRequest request) {
        int updatedRows = jdbcTemplate.update(SQL_SAVE_DROP, request.dropId, request.name, request.description);
        return String.format("Updated %d rows", updatedRows);
    }

}
