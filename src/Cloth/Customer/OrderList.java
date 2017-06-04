/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cloth.Customer;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author on
 */
public class OrderList implements Serializable {
    ArrayList<Order> ProductCart;
    float   totalPrice; 
    
    public OrderList(){
        ProductCart = new ArrayList();
        totalPrice=0;
    }
    public void addProduct(Order product){
        this.ProductCart.add(product);
    }
    public void setProductCart(ArrayList<Order> product){
        this.ProductCart=product;
    }
    public ArrayList<Order> getProductList(){
        return ProductCart;
    }
    public void setTotalPrice(float totalPrice){
        this.totalPrice=totalPrice;
    }
    public float getTotalPrice(){
        return totalPrice;
    }
}
