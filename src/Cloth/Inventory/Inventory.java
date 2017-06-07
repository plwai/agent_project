/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cloth.Inventory;

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

    public List<ItemProperties> getItemSummary() {
        return itemSummary;
    }
    
    public void addItemSummary(ItemProperties item) {
        itemSummary.add(item);
    }
    
    public int getNextId() {
        return itemSummary.size() + 1;
    }
    
    public boolean checkId(int id) {
        return id <= itemSummary.size();
    }
}
