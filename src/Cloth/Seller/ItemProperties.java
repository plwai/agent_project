/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cloth.Seller;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Wai Pai Lee
 */
public class ItemProperties {
    private String itemName;
    private String itemType;
    private String itemSize;
    private String itemColor;
    private String itemQuantity;
    private String itemSells;

    public ItemProperties() {
        itemName = "";
        itemType = "";
        itemSize = "";
        itemColor = "";
        itemQuantity = "";
        itemSells = "";
    }
    
    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    
    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getItemSize() {
        return itemSize;
    }

    public void setItemSize(String itemSize) {
        this.itemSize = itemSize;
    }

    public String getItemColor() {
        return itemColor;
    }

    public void setItemColor(String itemColor) {
        this.itemColor = itemColor;
    }

    public String getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(String itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public String getItemSells() {
        return itemSells;
    }

    public void setItemSells(String itemSells) {
        this.itemSells = itemSells;
    }
}
