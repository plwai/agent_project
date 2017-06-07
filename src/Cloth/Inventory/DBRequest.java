/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cloth.Inventory;

import Cloth.Customer.CustomerRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Wai Pai Lee
 */
public class DBRequest implements Serializable{
    private String request;
    private Inventory inventory;
    private int id, quantity;
    private boolean success;
    private ItemProperties item;
    private List<CustomerRequests> cusRequestList = new ArrayList<CustomerRequests>();
    private CustomerRequest cusReq;

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ItemProperties getItem() {
        return item;
    }

    public void setItem(ItemProperties item) {
        this.item = item;
    }

    public List<CustomerRequests> getCusRequestList() {
        return cusRequestList;
    }

    public void setCusRequestList(List<CustomerRequests> cusRequestList) {
        this.cusRequestList = cusRequestList;
    }
    
    public void addCusReq(CustomerRequests req) {
        this.cusRequestList.add(req);
    }
    
    public void addReq(CustomerRequest cusReq) {
        this.cusReq = cusReq;
    }

    public CustomerRequest getCusReq() {
        return cusReq;
    }
}
