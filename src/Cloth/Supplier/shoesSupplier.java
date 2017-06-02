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
public class shoesSupplier implements Serializable{
    private String slipperSize,slipperColor, ballerinaColor, ballerinaSize, bootSize, bootColor;
    private int slipperQty, ballerinaQty, bootQty;
    private int slipperPrice, ballerinaPrice, bootPrice;
    private String info;
    private boolean success = false;

    /**
     * @return the slipperSize
     */
    public String getSlipperSize() {
        return slipperSize;
    }

    /**
     * @param slipperSize the slipperSize to set
     */
    public void setSlipperSize(String slipperSize) {
        this.slipperSize = slipperSize;
    }

    /**
     * @return the slipperColor
     */
    public String getSlipperColor() {
        return slipperColor;
    }

    /**
     * @param slipperColor the slipperColor to set
     */
    public void setSlipperColor(String slipperColor) {
        this.slipperColor = slipperColor;
    }

    /**
     * @return the ballerinaColor
     */
    public String getBallerinaColor() {
        return ballerinaColor;
    }

    /**
     * @param ballerinaColor the ballerinaColor to set
     */
    public void setBallerinaColor(String ballerinaColor) {
        this.ballerinaColor = ballerinaColor;
    }

    /**
     * @return the ballerinaSize
     */
    public String getBallerinaSize() {
        return ballerinaSize;
    }

    /**
     * @param ballerinaSize the ballerinaSize to set
     */
    public void setBallerinaSize(String ballerinaSize) {
        this.ballerinaSize = ballerinaSize;
    }

    /**
     * @return the bootSize
     */
    public String getBootSize() {
        return bootSize;
    }

    /**
     * @param bootSize the bootSize to set
     */
    public void setBootSize(String bootSize) {
        this.bootSize = bootSize;
    }

    /**
     * @return the bootColor
     */
    public String getBootColor() {
        return bootColor;
    }

    /**
     * @param bootColor the bootColor to set
     */
    public void setBootColor(String bootColor) {
        this.bootColor = bootColor;
    }

    /**
     * @return the slipperQty
     */
    public int getSlipperQty() {
        return slipperQty;
    }

    /**
     * @param slipperQty the slipperQty to set
     */
    public void setSlipperQty(int slipperQty) {
        this.slipperQty = slipperQty;
    }

    /**
     * @return the ballerinaQty
     */
    public int getBallerinaQty() {
        return ballerinaQty;
    }

    /**
     * @param ballerinaQty the ballerinaQty to set
     */
    public void setBallerinaQty(int ballerinaQty) {
        this.ballerinaQty = ballerinaQty;
    }

    /**
     * @return the bootQty
     */
    public int getBootQty() {
        return bootQty;
    }

    /**
     * @param bootQty the bootQty to set
     */
    public void setBootQty(int bootQty) {
        this.bootQty = bootQty;
    }

    /**
     * @return the slipperPrice
     */
    public int getSlipperPrice() {
        return slipperPrice;
    }

    /**
     * @param slipperPrice the slipperPrice to set
     */
    public void setSlipperPrice(int slipperPrice) {
        this.slipperPrice = slipperPrice;
    }

    /**
     * @return the ballerinaPrice
     */
    public int getBallerinaPrice() {
        return ballerinaPrice;
    }

    /**
     * @param ballerinaPrice the ballerinaPrice to set
     */
    public void setBallerinaPrice(int ballerinaPrice) {
        this.ballerinaPrice = ballerinaPrice;
    }

    /**
     * @return the bootPrice
     */
    public int getBootPrice() {
        return bootPrice;
    }

    /**
     * @param bootPrice the bootPrice to set
     */
    public void setBootPrice(int bootPrice) {
        this.bootPrice = bootPrice;
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
