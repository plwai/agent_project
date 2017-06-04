/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cloth.Seller;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
public class CustomerRequests implements Serializable{
    private int receiptId;
    private List<Request> requestList = new ArrayList<Request>();

    public CustomerRequests(int receiptId) {
        this.receiptId = receiptId;
        
        try {  
            Class.forName("org.apache.derby.jdbc.ClientDriver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Inventory.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.loadRequests();
    }
    
    public void loadRequests() {
        try {  
            Connection con=DriverManager.getConnection(  "jdbc:derby://localhost:1527/sample","app","app");

            Statement stmt=con.createStatement(); 
            ResultSet rs=stmt.executeQuery("select * from ORDERS WHERE RECEIPTID="+Integer.toString(this.receiptId));  


            while(rs.next()){  
                requestList.add(new Request(rs.getInt(2), rs.getInt(3)));
            }  
                
            con.close();  
        } catch (SQLException ex) {
            Logger.getLogger(Inventory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(int receiptId) {
        this.receiptId = receiptId;
    }

    public List<Request> getRequestList() {
        return requestList;
    }

    public void setRequestList(List<Request> requestList) {
        this.requestList = requestList;
    }
    
    
}
