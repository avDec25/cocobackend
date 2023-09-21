package com.cocosorority.cocobackend.drop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cocosorority.cocobackend.utils.ResponseService;

@Controller
@RequestMapping("drop")
public class DropController {
    
    @Autowired
    DropService dropService;

    @Autowired
    ResponseService responseService;

    @PostMapping("save")
    public ResponseEntity<?> saveDrop(@RequestBody DropSaveRequest dropSaveRequest) {
        return responseService.prepareResponse(
            dropService.saveDrop(dropSaveRequest)
        );
    }

}
