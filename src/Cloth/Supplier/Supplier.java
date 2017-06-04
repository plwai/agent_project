/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cloth.Supplier;

import java.io.Serializable;
/**
 *
 * @author Tiki
 */
public class Supplier implements Serializable{

    public String clothColor;
    public String clothSize;
    public String clothPrice;
    public int clothQuantity;
    public int pantQuantity;
    public int onepieceQuantity;
    public String pantSize;
    public String pantPrice;
    public String onepieceColor;
    public String onepieceSize;
    public String onepiecePrice;
    public String info;
    public boolean success = false;
   
//   public Supplier(String ClothColor, String ClothSize, String ClothPrice, int ClothQuantity, 
//                   String PantSize, String PantPrice, int PantQuantity, 
//                   String OnepieceColor, String OnepieceSize, String OnepiecePrice, int OnepieceQuantity){
//       
//       this.clothColor = ClothColor;
//       this.clothSize = ClothSize;
//       this.clothPrice = ClothPrice;
//       this.clothQuantity = ClothQuantity;
//       
//       this.pantSize = PantSize;
//       this.pantPrice = PantPrice;
//       this.pantQuantity = PantQuantity;
//       
//       this.onepieceColor = OnepieceColor;
//       this.onepieceSize = OnepieceSize;
//       this.onepiecePrice = OnepiecePrice;
//       this.onepieceQuantity = OnepieceQuantity;
//   }


    /**
     * @return the clothColor
     */
    public String getClothColor() {
        return clothColor;
    }

    /**
     * @param clothColor the clothColor to set
     */
    public void setClothColor(String clothColor) {
        this.clothColor = clothColor;
    }

    /**
     * @return the clothSize
     */
    public String getClothSize() {
        return clothSize;
    }

    /**
     * @param clothSize the clothSize to set
     */
    public void setClothSize(String clothSize) {
        this.clothSize = clothSize;
    }

    /**
     * @return the clothPrice
     */
    public String getClothPrice() {
        return clothPrice;
    }

    /**
     * @param clothPrice the clothPrice to set
     */
    public void setClothPrice(String clothPrice) {
        this.clothPrice = clothPrice;
    }

    /**
     * @return the clothQuantity
     */
    public int getClothQuantity() {
        return clothQuantity;
    }

    /**
     * @param clothQuantity the clothQuantity to set
     */
    public void setClothQuantity(int clothQuantity) {
        this.clothQuantity = clothQuantity;
    }

    /**
     * @return the pantQuantity
     */
    public int getPantQuantity() {
        return pantQuantity;
    }

    /**
     * @param pantQuantity the pantQuantity to set
     */
    public void setPantQuantity(int pantQuantity) {
        this.pantQuantity = pantQuantity;
    }

    /**
     * @return the onepieceQuantity
     */
    public int getOnepieceQuantity() {
        return onepieceQuantity;
    }

    /**
     * @param onepieceQuantity the onepieceQuantity to set
     */
    public void setOnepieceQuantity(int onepieceQuantity) {
        this.onepieceQuantity = onepieceQuantity;
    }

    /**
     * @return the pantSize
     */
    public String getPantSize() {
        return pantSize;
    }

    /**
     * @param pantSize the pantSize to set
     */
    public void setPantSize(String pantSize) {
        this.pantSize = pantSize;
    }

    /**
     * @return the pantPrice
     */
    public String getPantPrice() {
        return pantPrice;
    }

    /**
     * @param pantPrice the pantPrice to set
     */
    public void setPantPrice(String pantPrice) {
        this.pantPrice = pantPrice;
    }

    /**
     * @return the onepieceColor
     */
    public String getOnepieceColor() {
        return onepieceColor;
    }

    /**
     * @param onepieceColor the onepieceColor to set
     */
    public void setOnepieceColor(String onepieceColor) {
        this.onepieceColor = onepieceColor;
    }

    /**
     * @return the onepieceSize
     */
    public String getOnepieceSize() {
        return onepieceSize;
    }

    /**
     * @param onepieceSize the onepieceSize to set
     */
    public void setOnepieceSize(String onepieceSize) {
        this.onepieceSize = onepieceSize;
    }

    /**
     * @return the onepiecePrice
     */
    public String getOnepiecePrice() {
        return onepiecePrice;
    }

    /**
     * @param onepiecePrice the onepiecePrice to set
     */
    public void setOnepiecePrice(String onepiecePrice) {
        this.onepiecePrice = onepiecePrice;
    }

    /**
     * @return the info
     */
    public String getInfo() {
        return info;
    }

    /**
     * @param info the info to set
     */
    public void setInfo(String info) {
        this.info = info;
    }

    /**
     * @return the success
     */
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