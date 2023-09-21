package com.cocosorority.cocobackend.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.cocosorority.cocobackend.utils.ResponseService;

@Controller
@RequestMapping("item")
public class ItemController {

    @Autowired
    ItemService itemService;

    @Autowired
    ResponseService responseService;

    @PostMapping(path = "save")
    public ResponseEntity<?> saveInventoryItem(
        @RequestPart("itemSaveRequest") ItemSaveRequest itemSaveRequest,
        @RequestPart MultipartFile image
    ) {
        itemSaveRequest.image = image;
        return responseService.prepareResponse(
            itemService.saveItem(itemSaveRequest)
        );
    }

}