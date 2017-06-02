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
public class Summary {
    private List<ItemProperties> itemSummary = new ArrayList<ItemProperties>();
    private String serviceType, info;
    private boolean isSuccess;

    public List<ItemProperties> getItemSummary() {
        return itemSummary;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public boolean getIsSuccess() {
        return isSuccess;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public void addItem(ItemProperties item) {
        itemSummary.add(item);
    }
}
