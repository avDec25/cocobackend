package com.cocosorority.cocobackend.item;

import org.springframework.web.multipart.MultipartFile;

public class ItemSaveRequest {
    public String name;
    public String dropId;
    public String costPrice;
    public String sellingPrice;
    public String shipping;
    public MultipartFile image;
}
