/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nrims.holder_data;

import com.nrims.holder_ref_data.UI;
import com.nrims.holder_transform.*;
import java.util.ArrayList;

/**
 * 
 */
public class DataPointFileProcessor {
    private String coeffFilePath;
    private String machineFilePath;
    private Transform point_trans = new Transform();
    private UI appWindow;
    private DataIO io;
    
    //Maintain three lists of points: machine, scope, and reference points from scope
    
    private ArrayList<REFPoint> machinePoints = new ArrayList<REFPoint>();
    private ArrayList<DataPoint> scopePoints = new ArrayList<DataPoint>();
    private ArrayList<DataPoint> referencePoints = new ArrayList<DataPoint>();
    
    // Data for coefficient computation
    private CoeffData coeffData = new CoeffData();
    
    private ArrayList<DataPoint> foundPoints;
    
    //this will be replaced later: boolean to say if coeffs is from file or computed
    //false = file, true = computed
    private boolean coeffComputed = false;
   

    /* constructors */

    //Farah: I'm not sure why we want a blank constructor, but here it is. 
    /**
     * Basic constructor.
     */
    public DataPointFileProcessor() {
    }
    
    public DataPointFileProcessor(UI window) {
        appWindow = window;
        io = new DataIO(this);
    }
    

    /* getters and setters */

    public void setCoeffData(CoeffData coeffs) {
        coeffComputed = true;
        coeffData = coeffs;
        
        //Whenever this is set, generate transformed points
        this.processTransform();
        
    }
    

    /**
     * Sets the filesystem path to coefficients file.
     * @param path Path to the coefficients file
     */
    public void setCoeffFilePath(String path)
    {
        coeffFilePath = path;
        coeffComputed = false;
        
        this.processTransform();
        
    }

    /**
     * The path to the coefficients file.
     * @return the filesystem path to coefficients file
     */
    public String getCoeffFilePath()
    {
        return( coeffFilePath );
    }
    
    /**
     * Set the source points read from a .points file.
     *
     * @param pointsSrc ArrayList of all points to add
     */
    public void setSrcPoints(ArrayList<DataPoint> pointsSrc)
    {
        scopePoints = pointsSrc;
        
    }
    
    /**
     * Returns srcPoints array
     */
    public ArrayList<DataPoint> getScopePoints() {
        return scopePoints;
    }

    
    /* Get list of reference points */
    public ArrayList<DataPoint> getReferencePoints() {
        return referencePoints;
    }

    public ArrayList<REFPoint> getMachinePoints() {
        return (machinePoints);
    }
    
    public void clearMachinePoints() {
        machinePoints.clear();
    }
    
    public void clearReferencePoints() {
        referencePoints.clear();
    }
    
    public void clearSrcPoints() {
        scopePoints.clear();
    }
    
    public void clearData() {
        clearMachinePoints();
        clearReferencePoints();
        clearSrcPoints();
        coeffFilePath = "";
        coeffData = new CoeffData();
    }
    
    public void setMachinePoints(ArrayList<REFPoint> newDest) {
        machinePoints = newDest;
    }
    
    public void printDestPoints() {
        //TODO
    }
    
    public void printDestPoints(ArrayList<REFPoint> printList) {
        //TODO
    }
    
    public void processTransform()   {
        //Do some safety checks in here to make sure these things are set.
        
        if(!coeffComputed) {
            double [] coeffIn = io.readCoeff(getCoeffFilePath());
            if(coeffIn.length == 0) {
                appWindow.updateStatus("Coefficients file could not be read. Is it in the proper format?");
            } else { 
                point_trans.setCoefficientsFromList(coeffIn);
            }
        } else {
            point_trans.setTransformCoeffs(coeffData.getXCoefficients(), coeffData.getYCoefficients());
        }
        
        //This should be changed so that IO is done in io class, pass values to transform class.
        point_trans.readStagePointsFile( io.getSrcFilePath() );
        point_trans.setTransformedPoints();

        //This is very weird. Comment out for now. Shallow copies, why?
        //if ( rpl != null)
        //point_trans.setRefPointList(rpl);

        machinePoints = point_trans.transformedPointsToRefPoints();
        
        //Send some messages to the GUI.
        appWindow.destTableRefresh(UI.TableCode.REF);
        appWindow.updateStatus("Points transformed.");
    }
    
    public String save(String location, String extension) {
        String output = new String();
        //TODO: Do some checks for saving.
        
        //Save destination files according to selected file filter.
        if(extension.equals("*.ref")) {
            output = output.concat(io.saveREF(location, machinePoints, coeffComputed));
        } else if(extension.equals("*.prs")) {
            output = output.concat(io.savePRS(location, machinePoints, coeffComputed));
        } else {
            return "Please select a file extension.";
        }
        
        return output;
        
    }
    
    public DataIO getIO() {
        return io;
    }
    
    public CoeffData getCoeffData() {
        return coeffData;
    }
    
    /*
     * Used to toggle the reference point flag in a point within the dpfp
     * list. Returns the new value of the point. 
     */
    public boolean toggleReferenceFlag(int i) {
        DataPoint selectedPt = scopePoints.get(i);
        boolean newVal = selectedPt.toggleIsReference();
        
        if(newVal) {
            referencePoints.add(selectedPt);
            return true;
        } else {
            referencePoints.remove(selectedPt);
            return false;
        }
        
        
    }
    
    public String openSrc(String location) {
        //First clear things before opening a new source file.
        clearData();
        return io.readSource(location);
        
    }
    
    public void updateLog(String line) {
        appWindow.updateStatus(line);
    }
}

