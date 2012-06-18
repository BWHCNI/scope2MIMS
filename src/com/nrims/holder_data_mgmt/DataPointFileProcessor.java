/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nrims.holder_data_mgmt;

import com.nrims.holder_transform.*;
import java.util.ArrayList;

/**
 * 
 */
public class DataPointFileProcessor {
    private String coeffFilePath;
    private String scopeFilePath;
    private String machineFilePath;
    private Transform point_trans = new Transform();
    
    //Maintain three lists of points: machine, scope, and reference points from scope
    
    protected ArrayList<REFPoint> machinePoints = new ArrayList<REFPoint>();
    private ArrayList<DataPoint> scopePoints = new ArrayList<DataPoint>();
    private ArrayList<DataPoint> referencePoints = new ArrayList<DataPoint>();

    /* constructors */

    //Farah: I'm not sure why we want a blank constructor, but here it is. 
    /**
     * Basic constructor.
     */
    public DataPointFileProcessor() {
    }
    
    /**
     * Constructor
     * @param coeffFile
     * @param srcPointsfile
     */
    public DataPointFileProcessor(
            String coeffFile,
            String srcFile)
    {
        coeffFilePath = coeffFile;
        scopeFilePath = srcFile;
        scopePoints = DataIO.readPoints(srcFile);
    }

    /* getters and setters */

    /**
     * Sets the filesystem path to coefficients file.
     * @param path Path to the coefficients file
     */
    public void setCoeffFilePath(String path)
    {
        coeffFilePath = path;
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
     * Sets the filesystem path to the stage points file.
     *
     * @param path Filesystem path to stage points file
     */
    public void setScopeFilePath(String path)
    {
        scopeFilePath = path;
        scopePoints = DataIO.readPoints(path);
    }
    
    /**
     * Returns srcPoints array
     */
    public ArrayList<DataPoint> getScopePoints() {
        return scopePoints;
    }

    /**
     * Returns filesystem path to stage points file.
     */
    public String getScopeFilePath()
    {
        return( scopeFilePath );
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
    
    public void setMachinePoints(ArrayList<REFPoint> newDest) {
        machinePoints = newDest;
    }
    
    public void printDestPoints() {
        //TODO
    }
    
    public void printDestPoints(ArrayList<REFPoint> printList) {
        //TODO
    }
    
    public void processTransform()
    {
        point_trans.readCoefficientsFile( getCoeffFilePath() );
        point_trans.readStagePointsFile( getScopeFilePath() );
        point_trans.setTransformedPoints();

        //This is very weird. Comment out for now. Shallow copies, why?
        //if ( rpl != null)
        //point_trans.setRefPointList(rpl);

        machinePoints = point_trans.transformedPointsToRefPoints();
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
}


