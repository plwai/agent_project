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
public class Order implements Serializable {
    private int quantity;
    private Cloth baju;
    public Order(){
    }
    public Order(int q, Cloth b){
        this.quantity=q;
        this.baju=b;
    }
    public void setQuantity(int q){
        this.quantity=q;
    }
    public void setBaju(Cloth b){
        this.baju=b;
    }
    public int getQuantity(){
        return quantity;
    }
    public Cloth getBaju(){
        return baju;
    }
}
