package com.cocosorority.cocobackend.order;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cocosorority.cocobackend.utils.ResponseService;

@Controller
@RequestMapping("order")
public class OrderController {
    @Autowired
    OrderService orderService;
    
    @Autowired
    ResponseService responseService;

    @PostMapping("create")
    public ResponseEntity<?> createOrder(@RequestBody CustomerOrderRequest orderRequest) {
        return responseService.prepareResponse(
            orderService.createOrder(orderRequest)
        );
    }

    @PostMapping("history")
    public ResponseEntity<?> createOrder(@RequestBody CustomerOrderHistoryRequest orderHistoryRequest) {
        if (StringUtils.isBlank(orderHistoryRequest.dropId)) {
            orderHistoryRequest.dropId = null;
        }
        return responseService.prepareOrderHistoryResponse(
            orderService.getOrderHistory(orderHistoryRequest)
        );
    }
}
