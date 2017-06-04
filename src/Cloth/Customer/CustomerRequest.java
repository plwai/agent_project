/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cloth.Customer;

import java.io.Serializable;

/**
 *
 * @author on
 */
public class CustomerRequest implements Serializable {
    private String action;
    private OrderList order=new OrderList();
    private Order product;
    private ClothList clothes;
    private String clothType;
    private boolean success = false;
    private int removeProduct=0;
    
    public CustomerRequest(){
        action=new String();
        product=new Order();
    }
    public CustomerRequest(String action, Order NewOrder){
        this.action=action;
        this.product=NewOrder;
    }

    public String getClothType() {
        return clothType;
    }

    public void setClothType(String clothType) {
        this.clothType = clothType;
    }

    public ClothList getClothes() {
        return clothes;
    }

    public void setClothes(ClothList clothes) {
        this.clothes = clothes;
    }
    
    public void setOrder(OrderList order){
        this.order=order;
    }
    public void setRemoveProduct(int removeProduct){
        this.removeProduct=removeProduct;
    }
    public int getRemoveProduct(){
        return removeProduct;
    }
    public void setAction(String action){
        this.action=action;
    }
    public void setNewOrder(Order productList){
        this.product=productList;
    }
    public String getAction(){
        return action;
    }
    public OrderList getOrder(){
        return order;
    }
    public Order getNewOrder(){
        return product;
    }
    public boolean isSuccess() {
        return success;
    }

    /**
     * @param success the success to set
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }
}
