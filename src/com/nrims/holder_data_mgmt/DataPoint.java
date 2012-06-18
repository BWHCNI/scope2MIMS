/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nrims.holder_data_mgmt;

/**
 * Class to represent a point object. Can be extended for more specifics.
 * Is this useful or should we just use a double[3] ?
 * @author fkashem
 */
public class DataPoint {
    private double x;
    private double y;
    private double z;
    private int num;
    boolean isReference; 
    
    /* Constructors */
    
    public DataPoint() {
    }
    
    public DataPoint(double xcoord, double ycoord, double zcoord) {
        x = xcoord;
        y = ycoord;
        z = zcoord;
    }
    
    public DataPoint(double xcoord, double ycoord, double zcoord, int pointNum) {
        x = xcoord;
        y = ycoord;
        z = zcoord;
        num = pointNum;
    }
    
    /* Getters and Setters */
    
    public double getXCoord() {
        return x;
    }
    
    public double getYCoord() {
        return y;
    }
    
    public double getZCoord() {
        return z;
    }
    
    public int getNum() {
        return num;
    }
    
    public boolean getIsReference() {
        return isReference;
    }
    
    public void setXCoord(double xcoord) {
        x = xcoord;
    }
    
    public void setYCoord(double ycoord) {
        y = ycoord;
    }
    
    public void setZCoord(double zcoord) {
        z = zcoord;
    }
    
    public void setIsReference(boolean ref) {
        isReference = ref;
    }
    
    /*
     * Toggles wheather the point is a reference 
     * Returns the resulting isReference state
     */
    public boolean toggleIsReference() {
        isReference = !isReference;
        return isReference;
    }
}
