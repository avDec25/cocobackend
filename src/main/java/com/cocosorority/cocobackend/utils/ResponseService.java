package com.cocosorority.cocobackend.utils;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.cocosorority.cocobackend.customers.CustomerQuickView;
import com.cocosorority.cocobackend.item.ItemQuickView;
import com.cocosorority.cocobackend.order.OrderHistoryResponse;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

@Service
public class ResponseService {

    @Autowired
    Gson gson;

    public ResponseEntity<?> prepareResponse(String message) {
        JsonObject response = getBaseResponseObject();
        response.addProperty("message", message);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(gson.toJson(response));
    }

    public ResponseEntity<?> prepareResponse(List<ItemQuickView> quickItemViewList) {
        JsonObject response = getBaseResponseObject();
        JsonElement data = gson.toJsonTree(quickItemViewList, new TypeToken<List<ItemQuickView>>(){}.getType());
        response.add("message", data);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(gson.toJson(response));
    }

    private JsonObject getBaseResponseObject() {
        JsonObject response = new JsonObject();
        response.addProperty("timestamp", String.format("%s", new Date()));
        return response;
    }

    public ResponseEntity<?> prepareCustomerListResponse(List<CustomerQuickView> listCustomers) {
        JsonObject response = getBaseResponseObject();
        JsonElement data = gson.toJsonTree(listCustomers, new TypeToken<List<CustomerQuickView>>(){}.getType());
        response.add("message", data);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(gson.toJson(response));
    }

    public ResponseEntity<?> prepareOrderHistoryResponse(List<OrderHistoryResponse> orderHistory) {
        JsonObject response = getBaseResponseObject();
        JsonElement data = gson.toJsonTree(orderHistory, new TypeToken<List<OrderHistoryResponse>>(){}.getType());
        response.add("message", data);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(gson.toJson(response));
    }

    public ResponseEntity<?> prepareResponse(JsonObject customerDetail) {
        JsonObject response = getBaseResponseObject();
        response.add("message", customerDetail);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(gson.toJson(response));
    }
}
