/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cloth.Customer;

/**
 *
 * @author on
 */
public class CustomerRequest {
    private String action;
    private order order=new order();
    private orderList product;
    private boolean success = false;
    private int removeProduct=0;
    
    public CustomerRequest(){
        action=new String();
        product=new orderList();
    }
    public CustomerRequest(String action, orderList NewOrder){
        this.action=action;
        this.product=NewOrder;
    }
    public void setOrder(order order){
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
    public void setNewOrder(orderList productList){
        this.product=productList;
    }
    public String getAction(){
        return action;
    }
    public order getOrder(){
        return order;
    }
    public orderList getNewOrder(){
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
