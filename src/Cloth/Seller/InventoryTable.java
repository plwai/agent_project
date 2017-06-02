/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cloth.Seller;

import java.awt.Container;
import java.awt.FlowLayout;
import java.util.Vector;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTable;

/**
 *
 * @author Wai Pai Lee
 */
public class InventoryTable extends JDialog {
    Vector col = new Vector(), row = new Vector();
    
    public InventoryTable( JFrame frame ) {
        super( frame, "Item Summary", true );
        
        col.add( "Id" );
        col.add( "Name" );
        col.add( "Type" );
        col.add( "Size" );
        col.add( "Color" );
        col.add( "Current Quantity" );
        
        Vector newRow = new Vector();
        
        newRow.add( "Id" );
        newRow.add( "Name" );
        newRow.add( "Type" );
        newRow.add( "Size" );
        newRow.add( "Color" );
        newRow.add( "Current Quantity" );
        row.add(newRow);
    }
    
    public void packTable() {
        JTable table = new JTable( row, col );
        table.setEnabled(false);
        Container c = getContentPane();
        c.setLayout( new FlowLayout() );
        c.add( table );
        this.pack();
    }
    
    public void addRow(ItemProperties itemProp){
        Vector newRow = new Vector();
        
        newRow.add(itemProp.getId());
        newRow.add(itemProp.getItemName());
        newRow.add(itemProp.getItemType());
        newRow.add(itemProp.getItemSize());
        newRow.add(itemProp.getItemColor());
        newRow.add(itemProp.getItemQuantity());
        //newRow.add(itemProp.getItemSells());
        
        row.add(newRow);
    }
}

