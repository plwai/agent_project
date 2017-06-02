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
public class order {
    String name, color, size;
    float   price, total; 
    int     quantity;
    
    public void setName(String name){
        this.name=name;
    }
    public void setColor(String color){
        this.color=color;
    }
    public void setSize(String size){
        this.size=size;
    }
    public void setPrice(float price){
        this.price=price;
    }
    public void setName(int quantity){
        this.quantity=quantity;
    }
    public String getName(){
        return name;
    }
    public String getColor(){
        return color;
    }
    public String getSize(){
        return size;
    }
    public float getPrice(){
        return price;
    }
    public int getQuantity(){
        return quantity;
    }
}
