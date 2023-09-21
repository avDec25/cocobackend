package com.cocosorority.cocobackend.item;

import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ItemSaveRequest {
    public String name;
    public String dropId;
    public int cost;
    public int shipping;
    public MultipartFile image;
}
