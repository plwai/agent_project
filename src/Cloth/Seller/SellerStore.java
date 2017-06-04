/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cloth.Seller;

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

    public SellerStore() {
        try {  
            Class.forName("org.apache.derby.jdbc.ClientDriver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Inventory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

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
    
    public void loadCusReq() {
        try {  
            Connection con=DriverManager.getConnection(  "jdbc:derby://localhost:1527/sample","app","app");
            Statement stmt=con.createStatement(); 
            ResultSet rs=stmt.executeQuery("select * from RECEIPT");  

            while(rs.next()){  
                cusRequestList.add(new CustomerRequests(rs.getInt(1)));
            }  
                
            con.close();  
        } catch (SQLException ex) {
            Logger.getLogger(Inventory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
