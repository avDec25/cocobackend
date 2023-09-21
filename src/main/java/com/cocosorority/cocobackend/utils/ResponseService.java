package com.cocosorority.cocobackend.utils;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@Service
public class ResponseService {
    
    @Autowired
    Gson gson;

    public ResponseEntity<?> prepareResponse(String message) {
        JsonObject response = new JsonObject();
        response.addProperty("timestamp", String.format("%s", new Date()));
        response.addProperty("message", message);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        
        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(gson.toJson(response));
    }
}
