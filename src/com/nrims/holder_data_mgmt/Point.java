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
public class Point {
    private double x;
    private double y;
    private double z;
    
    public Point() {
    }
    
    public Point(double xcoord, double ycoord, double zcoord) {
        x = xcoord;
        y = ycoord;
        z = zcoord;
    }
    
    public double getXCoord() {
        return x;
    }
    
    public double getYCoord() {
        return y;
    }
    
    public double getZCoord() {
        return z;
    }
    
    private void setXCoord(double xcoord) {
        x = xcoord;
    }
    
    private void setYCoord(double ycoord) {
        y = ycoord;
    }
    
    private void setZCoord(double zcoord) {
        z = zcoord;
    }
}
