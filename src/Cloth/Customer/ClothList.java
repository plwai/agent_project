/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cloth.Customer;

import Cloth.Seller.Inventory;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Wai Pai Lee
 */
public class ClothList implements Serializable{
    private ArrayList<Cloth> Baju = new ArrayList();

    public ClothList() {
        try {  
            Class.forName("org.apache.derby.jdbc.ClientDriver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Inventory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList<Cloth> getBaju() {
        return Baju;
    }

    public void setBaju(ArrayList<Cloth> Baju) {
        this.Baju = Baju;
    }
    
    public void loadData() {
        Connection con;

        try {
            con = DriverManager.getConnection(  "jdbc:derby://localhost:1527/sample","app","app");

            Statement stmt=con.createStatement(); 
            ResultSet rs=stmt.executeQuery("select * from INVENTORY");  

            while(rs.next()){  
                Baju.add(new Cloth(rs.getString(2), rs.getString(4), rs.getString(3), rs.getString(5), rs.getInt(6), rs.getInt(1)));
            }  

            con.close();  

        } catch (SQLException ex) {
            Logger.getLogger(CustomerAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void loadData(String type) {
        Connection con;

        try {
            con = DriverManager.getConnection(  "jdbc:derby://localhost:1527/sample","app","app");

            Statement stmt=con.createStatement(); 
            ResultSet rs=stmt.executeQuery("select * from INVENTORY");  

            while(rs.next()){  
                Baju.add(new Cloth(rs.getString(2), rs.getString(4), rs.getString(3), rs.getString(5), rs.getInt(6), rs.getInt(1)));
            }  

            con.close();  

        } catch (SQLException ex) {
            Logger.getLogger(CustomerAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
