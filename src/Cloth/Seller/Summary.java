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

    public List<ItemProperties> getItemSummary() {
        return itemSummary;
    }

    public void addItem(ItemProperties item) {
        itemSummary.add(item);
    }
}
