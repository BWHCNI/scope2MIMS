/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nrims.holder_ref_data;

import com.nrims.holder_transform.Transform;
import java.util.ArrayList;

/**
 *
 * @author fkashem
 */
public class CoeffCalculator {
    private double accuracy;
   
    // Coefficients formatted as 2D double array in Transform.java, using that
    private double[][] xCoefficients;
    private double[][] yCoefficients;
    
    
    
    
    
    public CoeffCalculator() {
        
    }
    
    /*
     * Pass an arraylist in which each node has a double of length 4
     * [ ( scope xCoord) , (scope yCoord) , (found xCoord) , (found yCoord) ]
     */
    public void calculateCoeffs(ArrayList<double[]> refFound) {
        
    }
    
    public void calculateAccuracy() {
        
    }
    
    
    public double getAccuracy() {
        return accuracy;
    }
    
    
    
}
