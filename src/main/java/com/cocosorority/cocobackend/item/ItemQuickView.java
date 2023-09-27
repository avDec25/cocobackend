package com.cocosorority.cocobackend.item;

import lombok.Data;

@Data
public class ItemQuickView {
    public String itemId;
    public String name;
    public String dropId;
    public String image;
    public String costPrice;
    public String sellingPrice;
    public String shipping;
}
