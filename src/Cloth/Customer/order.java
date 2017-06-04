/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cloth.Customer;

import java.util.ArrayList;

/**
 *
 * @author on
 */
public class order {
    ArrayList<orderList> ProductCart;
    float   totalPrice; 
    
    public order(){
        ProductCart = new ArrayList();
        totalPrice=0;
    }
    public void addProduct(orderList product){
        this.ProductCart.add(product);
    }
    public void setProductCart(ArrayList<orderList> product){
        this.ProductCart=product;
    }
    public ArrayList<orderList> getProductList(){
        return ProductCart;
    }
    public void setTotalPrice(float totalPrice){
        this.totalPrice=totalPrice;
    }
    public float getTotalPrice(){
        return totalPrice;
    }
}
