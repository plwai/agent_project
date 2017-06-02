/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cloth.Seller;

import java.io.Serializable;

/**
 *
 * @author Wai Pai Lee
 */
public class SellerStore implements Serializable {
    private Inventory summary;
    private int restockId;
    private String serviceType, info;
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
    
    
}
