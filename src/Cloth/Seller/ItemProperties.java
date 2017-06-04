/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cloth.Seller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Wai Pai Lee
 */
public class ItemProperties implements Serializable{
    private String itemName;
    private String itemType;
    private String itemSize;
    private String itemColor;
    private float itemPrice;
    private int itemQuantity;
    private String itemSells;
    private int id;

    public ItemProperties() {
        itemName = "";
        itemType = "";
        itemSize = "";
        itemColor = "";
        itemQuantity = 0;
        itemSells = "";
    }

    public ItemProperties(String itemName, String itemType, String itemSize, String itemColor, int itemQuantity, int id, float itemPrice) {
        this.itemName = itemName;
        this.itemType = itemType;
        this.itemSize = itemSize;
        this.itemColor = itemColor;
        this.itemQuantity = itemQuantity;
        this.itemPrice = itemPrice;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(float itemPrice) {
        this.itemPrice = itemPrice;
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

    public int getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public String getItemSells() {
        return itemSells;
    }

    public void setItemSells(String itemSells) {
        this.itemSells = itemSells;
    }
}
