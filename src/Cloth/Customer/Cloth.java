package Cloth.Customer;

import java.io.Serializable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author on
 */
public class Cloth implements Serializable {
    private String type, color, size, name;
    private int id;
    float price;
    public Cloth(){
        
    }
    public Cloth( String name, String color, String type, String size, float price, int id){
        this.name=name;
        this.type=type;
        this.size=size;
        this.color=color;
        this.price=price;
        this.id=id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public void setType(String type){
        this.type=type;
    }
    public void setColor(String color){
        this.color=color;
    }
    public void setSize(String size){
        this.size=size;
    }
    public void setName(String name){
        this.name=name;
    }
    public void setPrice(float price){
        this.price=price;
    }
    public String getType(){
        return type;
    }
    public String getColor(){
        return color;
    }
    public String getSize(){
        return size;
    }
    public String getName(){
        return name;
    }
    public float getPrice(){
        return price;
    }
}
