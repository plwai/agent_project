/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cloth.Seller;

import Cloth.Inventory.CustomerRequests;
import Cloth.Inventory.ItemProperties;
import Cloth.Inventory.Inventory;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Wai Pai Lee
 */
public class SellerStore implements Serializable {
    private Inventory summary;
    private ItemProperties newItem;
    private int restockId, quantity;
    private String serviceType, info;
    private List<CustomerRequests> cusRequestList = new ArrayList<CustomerRequests>();
    private boolean isSuccess;

    public Inventory getSummary() {
        return summary;
    }

    public void setSummary(Inventory summary) {
        this.summary = summary;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public boolean getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public int getRestockId() {
        return restockId;
    }

    public void setRestockId(int restockId) {
        this.restockId = restockId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public ItemProperties getNewItem() {
        return newItem;
    }

    public void setNewItem(ItemProperties newItem) {
        this.newItem = newItem;
    }

    public List<CustomerRequests> getRequestList() {
        return cusRequestList;
    }

    public void setRequestList(List<CustomerRequests> cusRequestList) {
        this.cusRequestList = cusRequestList;
    }
}
