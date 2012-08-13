/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nrims.holder_transform;

import com.nrims.holder_data.CoeffData;
import com.nrims.holder_data.DataPoint;
import com.nrims.holder_data.DataPointFileProcessor;
import com.nrims.holder_ref_data.CoeffCalcWindow;
import com.nrims.holder_ref_data.UI;
import com.nrims.holder_transform.ComputeCoefficients_n2mc;
import com.nrims.holder_transform.Transform;
import java.util.ArrayList;

/**
 *
 * @author fkashem
 */
public class CoeffCalculator {
    private double[] error;
    private double averageError;
    private double[][] xCoefficients;
    private double[][] yCoefficients;
    private double[][] nikonPts;
    private double[][] mimsPts;
    private ArrayList<double[]> calculatedPoints;
    private CoeffData coeffData;
    
    
    private ComputeCoefficients_n2mc n2mc = new ComputeCoefficients_n2mc(); 
    private CoeffCalcWindow window;
    private DataPointFileProcessor data;
    private UI parent;
    
    public CoeffCalculator() {
        
    }
    
    public CoeffCalculator(DataPointFileProcessor dpfp, UI calledFrom) {
        data = dpfp;
        parent = calledFrom;
        window = new CoeffCalcWindow(data.getReferencePoints(), calledFrom, this);
    }
    
    /*
     * Sends coeffData structure to the dpfp. 
     * @param usedRefPoints is the list of points used for calcuating these coefficients
     * points object contains nikon coords and mims coords.
     */
    public void exportData(ArrayList<DataPoint> usedRefPoints) {
        coeffData = new CoeffData(xCoefficients, yCoefficients, usedRefPoints, calculatedPoints, error, averageError);
        data.setCoeffData(coeffData);
    }
    
    
    /*
     * Pass calculator two double[][] arrays. Used internally by subsets calc.
     */
    private ArrayList<double[][]> calculateCoeffs(double[][] nikonPoints, double[][] mimsPoints) {
        n2mc.computeCoefficients(nikonPoints, mimsPoints);
        ArrayList<double[][]> returnCoeffs = new ArrayList<double[][]>();
        returnCoeffs.add(n2mc.getXCoeff());
        returnCoeffs.add(n2mc.getYCoeff());
        
        xCoefficients = n2mc.getXCoeff();
        yCoefficients = n2mc.getYCoeff();
        
        return returnCoeffs;
    }
    
    /* 
     * Calculate using the locally stored points.
     */
    
    public void calculateCoeffs() {
        if(nikonPts != null && mimsPts != null) {
            n2mc.computeCoefficients(nikonPts, mimsPts);
            xCoefficients = n2mc.getXCoeff();
            yCoefficients = n2mc.getYCoeff();
        }
        
    }
    
    /*
     * Calculates error by reference point and average error.
     */
    public boolean calculateError() {
        boolean success = false;
        if(xCoefficients != null && yCoefficients != null) {
            
            //Do transformation with coefficients generated
            Transform transformer = new Transform();
            transformer.setSrcPoints(n2mc.getNikonPts());
            transformer.setCoefficients(xCoefficients, yCoefficients);
            transformer.setTransformedPoints();
            
            calculatedPoints = transformer.getTransformedPoints();
            double[][] foundPoints = n2mc.getMimsPts();
            
            /*Testing. Print out the coords of the transformed points
            for(int i = 0; i < calculatedPoints.size(); i++) {
                System.out.println("X: " + calculatedPoints.get(i)[0] + " Y: " + calculatedPoints.get(i)[1]);
            }
            * 
            */
            
            //Make sure the world didn't explode.
            if(calculatedPoints.size() != foundPoints.length) {
                System.out.println("Something strange has occured.");
                return success;
            }
            
            //Do error calculation
            error = new double[foundPoints.length];
            double diffX;
            double diffY;
            for(int i = 0; i < error.length; i++) {
                if(foundPoints[i][0] == 0) {
                    error[i] = 0;
                } else {
                    diffX = foundPoints[i][0] - calculatedPoints.get(i)[0];
                    diffY = foundPoints[i][1] - calculatedPoints.get(i)[1];
                    error[i] = Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY, 2));
                }
            }
            
            
            //Now get average error.
            double sum = 0;
            for(double i : error) {
                sum += i;
            }
            
            averageError = sum / error.length;
            
            success = true;
        } 
        
        return success;
        
    }
    
    
    /*
     * Gives a list (string) of the point errors then average error.
     */
    public String printError() {
        String output = "Error by point (um): \n";
        for(int i = 0; i < error.length; i++) {
            output = output.concat(error[i] + "\n");
        }
        
        output = output.concat("Average error (um): " + averageError);
        
        return output;
    }
    
    /* 
     * Opens up window for the calculator.
     */
    public void showWindow() {
        window.setVisible(true);
    }
    
    public void setData(DataPointFileProcessor dpfp) {
        data = dpfp;
        window = new CoeffCalcWindow(data.getReferencePoints(), parent, this);
    }
    
    public DataPointFileProcessor getData() {
        return data;
    }
    
    public double[] getError() {
        return error;
    } 
    
    public double getAverageError() {
        return averageError;
    }
    
    public double[][] getXCoefficients() {
        return xCoefficients;
    }
    
    public double[][] getYCoefficients() {
        return yCoefficients;
    }
    
    public void setMimsPts(double[][] mimsPoints) {
        mimsPts = mimsPoints;
    }
    
    public void setNikonPts(double[][] nikonPoints) {
        nikonPts = nikonPoints;
    }
    
    //Note: for subset generation, make sure that error calculated uses all found points.
    /*
     * Global variable declarations for subset Selection
     */
    
    int[][] combinations;
    ArrayList<Double> allSubsetError = new ArrayList<Double>();
    ArrayList<int[]> allSubsetPointsUsed = new ArrayList<int[]>();
    int rowNum;
    protected void subsetSelection(int exclude) {
        System.out.println("EXCLUDING MAXIMUM: " + exclude);
        int n = nikonPts.length;
        
        //min is them minimum amount of points we want in our subset: n - exclude
        int min = n - exclude;
        
        int totalCombinations = 0;
        ArrayList<Integer> kSubsetTotals = new ArrayList<Integer>();
        for (int k = min; k <=n; k++) {
            int nChooseK = factorial(n) / (factorial(n-k) * factorial(k));
            kSubsetTotals.add(nChooseK);
            totalCombinations += nChooseK;
        }
        
        int whichSubset = 0;
        for(int k = min; k <= n; k++) {
            //Array to hold combinations
            combinations = new int[kSubsetTotals.get(whichSubset)][k];
            rowNum = 0;
            //Now find populate combinations array
            int[] row = new int[k];
            subsetGenerate(row, 0, 0, k, n);
            
            //Go through each row in combinations array
            for(int[] kCombination : combinations) {
                //select corresponding rows from nikonpts and mimsPts
                double[][] nikonSubset = new double[k][2];
                double[][] mimsSubset = new double[k][2];
                
                for(int i = 0; i < kCombination.length; i++) {
                    nikonSubset[i][0] = nikonPts[kCombination[i]][0];
                    nikonSubset[i][1] = nikonPts[kCombination[i]][1];
                    
                    mimsSubset[i][0] = mimsPts[kCombination[i]][0];
                    mimsSubset[i][1] = mimsPts[kCombination[i]][1];
                    
                }
                
                //compute with this subset
                ArrayList<double[][]> subsetCoeffs = calculateCoeffs(nikonSubset, mimsSubset);
                
               //Store points used for coeff calculation and corresponding error.
                allSubsetPointsUsed.add(kCombination);
                allSubsetError.add(calculateError(subsetCoeffs.get(0), subsetCoeffs.get(1)));
            }
            
            whichSubset++;
        }
        
        //Grab min error, figure out which combination it is.
        double minError = Double.MAX_VALUE;

        //Initializing to 0 so compiler doesn't complain later.
        int indexOfMin = 0;
        for(int i = 0; i < allSubsetError.size(); i++) {
            if(allSubsetError.get(i) < minError) {
                minError = allSubsetError.get(i);
                indexOfMin = i;
            }
        }

        System.out.println("Subset yielding the lowest error is: " );
        for(int i : allSubsetPointsUsed.get(indexOfMin)) {
            System.out.print(i + " ");
        }
        System.out.println(" with error: " + allSubsetError.get(indexOfMin));
        
        //Calculate coefficients using only these points.
        //Send these coefficients back as the ones to use. 
    }
    
    /*
     * Helper function for subset selection. Gives all k choose n combinations
     */
    private void subsetGenerate(int[] s, int position, int nextInt, int k, int n) {
        if (position == k) {
            for(int i = 0; i < k; i++){
                combinations[rowNum][i] = s[i];
            }
            rowNum++;
            return;
        }
        for (int i = nextInt; i < n; i++) {
            s[position] = i;
            subsetGenerate(s, position + 1, i + 1, k, n);
            
        }
    }
    
    
    /*
     * Factorial function for computing number of combinations 
     */
    private int factorial(int f) {
        return ((f == 0) ? 1 : f * factorial(f - 1));
    }
    
       //Private method for use within subset calculation. Doesn't touch global error variables
    private double calculateError(double[][] xcoeffs, double[][] ycoeffs) {
        Transform transformer = new Transform();
        
        //Source points will be all reference points, not just the subset used to calculate
        transformer.setSrcPoints(nikonPts);
        transformer.setCoefficients(xcoeffs, ycoeffs);
        transformer.setTransformedPoints();
        
        ArrayList<double[]> calculatedPoints = transformer.getTransformedPoints();
        double[][] foundPoints = mimsPts;
        
            //Testing. Print out the coords of the transformed points
            for(int i = 0; i < calculatedPoints.size(); i++) {
                System.out.println("X: " + calculatedPoints.get(i)[0] + " Y: " + calculatedPoints.get(i)[1]);
            }
            
            //Make sure the world didn't explode.
            if(calculatedPoints.size() != foundPoints.length) {
                System.out.println("Something strange has occured.");
                return -1.0;
            }
            
            //Do error calculation
            double[] subsetError = new double[foundPoints.length];
            double diffX;
            double diffY;
            for(int i = 0; i < subsetError.length; i++) {
                if(foundPoints[i][0] == 0) {
                    subsetError[i] = 0;
                } else {
                    diffX = foundPoints[i][0] - calculatedPoints.get(i)[0];
                    diffY = foundPoints[i][1] - calculatedPoints.get(i)[1];
                    subsetError[i] = Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY, 2));
                }
            }
            
            
            //Now get average error.
            double sum = 0;
            for(double i : subsetError) {
                sum += i;
            }
            
            double subsetAverage = sum / subsetError.length;
            
            return subsetAverage;
    }
}
