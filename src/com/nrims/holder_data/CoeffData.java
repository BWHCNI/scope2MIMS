/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nrims.holder_data;

import com.nrims.holder_data.DataPoint;
import java.util.ArrayList;

/**
 *
 * @author fkashem
 */
public class CoeffData {
    //We want to save the data that's used for the coefficients
    //Reference points, found points, coefficients, transformation method.
    private double[][] foundPoints;
    private ArrayList<double[]> calculatedPoints;
    private double[][] xCoefficients;
    private double[][] yCoefficients;
    private double[] error;
    private double avgError;
    private String transformationMethod = new String();
    private String DEFAULTCOEFF = "";
    
    //We're repeating some data here -- has to do with how the coefficient transforms are set up,
    //they only need the coords of the ref points, but we want all of the point data for saving later.
    private ArrayList<DataPoint> refPoints;
    private double[][] nikonRefPointCoords;
    
    public CoeffData() {
        
    }
    
    public CoeffData(double[][] xcoeff, double[][] ycoeff, ArrayList<DataPoint> pointList, ArrayList<double[]> calcPts, double[] err, double avgErr) {
        refPoints = pointList;
        calculatedPoints = calcPts;
        xCoefficients = xcoeff;
        yCoefficients = ycoeff;
        error = err;
        avgError = avgErr;
        transformationMethod = DEFAULTCOEFF;
    }
    
    
    // TO DO: If we want to load coeff data into this struct when we are 
    public CoeffData(String filepath) {
        
    }
    
    /* Getters and setters */
    
    protected void setNikonRefPointCoords(double[][] setPoints) {
        nikonRefPointCoords = setPoints;
    }
    
    protected void setFoundPoints(double[][] setPoints) {
        foundPoints = setPoints;
    }
    
    protected void setCalculatedPoints(ArrayList<double[]> setPoints) {
        calculatedPoints = setPoints;
    }
    
    protected void setXCoeff(double[][] xcoeffs){
        xCoefficients = xcoeffs;
    }
    
    protected void setYCoeff(double[][] ycoeffs) {
        yCoefficients = ycoeffs;
    }
    
    protected void setError(double[] setError) {
        error = setError;
    }
    
    protected void setAvgError(double avg) {
        avgError = avg;
    }
    
    protected void setRefPoints(ArrayList<DataPoint> list) {
        refPoints = list;
    }
    
    public double[][] getNikonRefPoints() {
        return nikonRefPointCoords;
    }
    
    public double[][] getFoundPoints() {
        return foundPoints;
    }
    
    public ArrayList<double[]> getCalculatedPoints() {
        return calculatedPoints;
    }
    
    public double[] getError() {
        return error;
    }
    
    public double getAvgError() {
        return avgError;
    }
    
    public double[][] getXCoefficients() {
        return xCoefficients;
    }
    
    public double[][] getYCoefficients() {
        return yCoefficients;
    }
    
    public ArrayList<DataPoint> getRefPoints() {
        return refPoints;
    }
    
    
}
