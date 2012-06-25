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
public class DataPoint implements Comparable<DataPoint> {
    private double x;
    private double y;
    private double z;
    
    //Coords found in the machine
    private double xFound;
    private double yFound;
    private boolean isFound;
    
    private int num;
    private boolean isReference; 
    
    
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
    
    /*
     * In order to keep the reference point ArrayList sorted, we have a compareTo.
     */
    @Override
    public int compareTo(DataPoint anotherPoint) {
        return this.getNum() - anotherPoint.getNum();
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
    
    public double getXFound() {
        return xFound;
    }
    
    public double getYFound() {
        return yFound;
    }
    
    /*
     * If this is false but there are values for xFound and yFound, xFound and yFound
     * hold the last loaded found coordinates. 
     */
    public boolean getIsFound() {
        return isFound;
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
    
    public void setXFound(double xcoord) {
        xFound = xcoord;
    }
    
    public void setYFound(double ycoord) {
        yFound = ycoord;
    }
    
    public void setIsReference(boolean ref) {
        isReference = ref;
    }
    
    public void setIsFound(boolean found) {
        isFound = found;
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