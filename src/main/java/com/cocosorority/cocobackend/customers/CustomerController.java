package com.cocosorority.cocobackend.customers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cocosorority.cocobackend.utils.ResponseService;

@Controller
@RequestMapping("customer")
public class CustomerController {
    @Autowired
    CustomerService customerService;

    @Autowired
    ResponseService responseService;

    @PostMapping("save")
    public ResponseEntity<?> saveCustomer(@RequestBody CustomerSaveRequest customerSaveRequest) {
        return responseService.prepareResponse(
            customerService.saveCustomer(customerSaveRequest)
        );
    }

    @PostMapping("import")
    public ResponseEntity<?> importCustomers(@RequestBody CustomerSheetImportRequest request) {
        return responseService.prepareResponse(
            customerService.importCustomersFromGoogleSheet(
                request.sheetId,
                request.range
            )
        );
    }
}
