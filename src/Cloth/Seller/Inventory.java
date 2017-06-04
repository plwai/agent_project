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
public class Inventory implements Serializable{
    private List<ItemProperties> itemSummary = new ArrayList<ItemProperties>();

    public Inventory() {
        try {  
            Class.forName("org.apache.derby.jdbc.ClientDriver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Inventory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<ItemProperties> getItemSummary() {
        return itemSummary;
    }
    
    public int getNextId() {
        return itemSummary.size() + 1;
    }
    
    public boolean checkId(int id) {
        return id <= itemSummary.size();
    }

    public void addItem(ItemProperties item) {
        itemSummary.add(item);
        
        try {  
                Connection con=DriverManager.getConnection(  "jdbc:derby://localhost:1527/sample","app","app");
                
                String query = " insert into INVENTORY (ID, NAME, TYPE, COLOR, SIZE, QUANTITY)"
                + " values (?, ?, ?, ?, ?, ?)";

                PreparedStatement preparedStmt = con.prepareStatement(query);
                preparedStmt.setInt (1, item.getId());
                preparedStmt.setString (2, item.getItemName());
                preparedStmt.setString (3, item.getItemType());
                preparedStmt.setString (4, item.getItemColor());
                preparedStmt.setString (5, item.getItemSize());
                preparedStmt.setInt    (6, item.getItemQuantity());

                preparedStmt.execute();
                
                con.close();  
        } catch (SQLException ex) {
            Logger.getLogger(Inventory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void restock(int id, int quantity) {
        itemSummary.get(id - 1).setItemQuantity(itemSummary.get(id - 1).getItemQuantity() + quantity);
        try {  
                Connection con=DriverManager.getConnection(  "jdbc:derby://localhost:1527/sample","app","app");
                
                String query = " update INVENTORY set QUANTITY = ? where id = ?";

                PreparedStatement preparedStmt = con.prepareStatement(query);
                
                preparedStmt.setInt (1, itemSummary.get(id - 1).getItemQuantity());
                preparedStmt.setInt (2, itemSummary.get(id - 1).getId());

                preparedStmt.executeUpdate();
                
                con.close();  
        } catch (SQLException ex) {
            Logger.getLogger(Inventory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void loadAllData() {
        try {  
                Connection con=DriverManager.getConnection(  "jdbc:derby://localhost:1527/sample","app","app");
                Statement stmt=con.createStatement(); 
                ResultSet rs=stmt.executeQuery("select * from INVENTORY");  
                
                while(rs.next()){  
                    itemSummary.add(new ItemProperties(rs.getString(2), rs.getString(3), rs.getString(5), rs.getString(4), rs.getInt(6), rs.getInt(1)));
                }  
                
                con.close();  
        } catch (SQLException ex) {
            Logger.getLogger(Inventory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
