package Cloth.Customer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author on
 */
public class Cloth {
    private String type, color, size, name;
    
    public Cloth( String name, String color, String type, String size){
        this.name=name;
        this.type=type;
        this.size=size;
        this.color=color;
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
}
