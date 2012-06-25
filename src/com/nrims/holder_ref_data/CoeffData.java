/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nrims.holder_ref_data;

import com.nrims.holder_data_structures.DataPoint;
import java.util.ArrayList;

/**
 *
 * @author fkashem
 */
public class CoeffData {
    //We want to save the data that's used for the coefficients
    //Reference points, found points, coefficients, transformation method.
    private double[][] nikonRefPoints;
    private double[][] foundPoints;
    private ArrayList<DataPoint> calculatedPoints;
    private double[][] xCoefficients;
    private double[][] yCoefficients;
    private double[] error;
    private double avgError;
    private String transformationMethod = new String();
    private String DEFAULTCOEFF = "";
    
    public CoeffData() {
        
    }
    
    public CoeffData(double[][] xcoeff, double[][] ycoeff, double[][] nikonPts, double[][] foundPts, ArrayList<DataPoint> calcPts, double[] err, double avgErr) {
        nikonRefPoints = nikonPts;
        foundPoints = foundPts;
        calculatedPoints = calcPts;
        xCoefficients = xcoeff;
        yCoefficients = ycoeff;
        error = err;
        avgError = avgErr;
        transformationMethod = DEFAULTCOEFF;
    }
    
    public CoeffData(String filepath) {
        
    }
    
    /* Getters and setters */
    
    protected void setNikonRefPoints(double[][] setPoints) {
        nikonRefPoints = setPoints;
    }
    
    protected void setFoundPoints(double[][] setPoints) {
        foundPoints = setPoints;
    }
    
    protected void setCalculatedPoints(ArrayList<DataPoint> setPoints) {
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
    
    public double[][] getNikonRefPoints() {
        return nikonRefPoints;
    }
    
    public double[][] getFoundPoints() {
        return foundPoints;
    }
    
    public ArrayList<DataPoint> getCalculatedPoints() {
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
    
    
}
