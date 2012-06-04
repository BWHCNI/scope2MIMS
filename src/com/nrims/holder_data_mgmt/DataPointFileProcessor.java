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
    private String srcFilePath;
    private Transform point_trans = new Transform();
    protected ArrayList<RefPoint> destPoints = new ArrayList<RefPoint>();

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
        srcFilePath = srcFile;
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
    public void setSrcFilePath(String path)
    {
        srcFilePath = path;
    }

    /**
     * Returns filesystem path to stage points file.
     * @return filesystem path to stage points file
     */
    public String getSrcFilePath()
    {
        return( srcFilePath );
    }

    
    public ArrayList<RefPoint> getDestPoints() {
        return (destPoints);
    }
    
    public void setDestPoints(ArrayList<RefPoint> newDest) {
        destPoints = newDest;
    }
    
    public void printDestPoints() {
        //TODO
    }
    
    public void printDestPoints(ArrayList<RefPoint> printList) {
        //TODO
    }
    
    public void processTransform()
    {
        point_trans.readCoefficientsFile( getCoeffFilePath() );
        point_trans.readStagePointsFile( getSrcFilePath() );
        point_trans.setTransformedPoints();

        //This is very weird. Comment out for now. Shallow copies, why?
        //if ( rpl != null)
        //point_trans.setRefPointList(rpl);

        destPoints = point_trans.transformedPointsToRefPoints();
    }
    
    /* This should be in the RefPoint.java Copying, leaving a copy here for 
    
    private void printRefPoint(RefPoint rp)
            
    {
        System.out.println("X: " + rp.getXCoord());
        System.out.println("Y: " + rp.getYCoord());
        System.out.println("Z: " + rp.getZCoord());
        System.out.println("Comment: " + rp.getComment());
        System.out.println("Date: " + rp.getDateString());
    }
    * 
    */
    
    //Other methods I don't think we need anymore.

    /*
    public RefPointList getRefPointList()
    {
        return( rpl );
    }

    public void setRefPointList(RefPointList r_p_l)
    {
        rpl = r_p_l;
    }

    public void printRefPointList(RefPointList r_p_l)
    {
        int i, list_size;

        if (r_p_l == null)
            return;

        list_size = r_p_l.getNumRefPoints();
        System.out.println("Total points: " + list_size );

        for (i = 0; i < list_size; i++)
        {
            System.out.println("");
            System.out.println("Point #" + ( i+1));
            printRefPoint( r_p_l.getRefPoint(i) );
            System.out.println("");
        }

    }

    public void printRefPointList()
    {
        printRefPointList( getRefPointList() );
    }
    * 
    */
    
    //Get rid of generateRefPointFile method... redundant, not used.

    /*
     * This routine generates the holder ref file based upon the 
     * coefficients and point corrdintates file.
     */
    /*
    public void generateRefPointFile()
    {
        HolderDataFile hdf;

        if ( getCoeffFilePath() == null )
            return;

        if ( getSrcFilePath() == null )
            return;

        //if ( getHolderPointFilePath() == null )
        //    return;

        processTransform();

        hdf = new HolderDataFile(
                    getHolderPointFilePath(),
                    true,
                    getRefPointList()
                    );

        hdf.writeFileOut();
        hdf.close();
    }

    //Farah: ignoring errors in this method for now since it's a test...
    
    /* test method */
    /*
    public static void main(String[ ] args)
    {
        final String write_action = "write";
        final String read_action = "read";

        String action = null;
        String coeff_file_path = null;
        String points_file_path = null;
        String ref_output_file = null;
        String ref_input_file = null;
        HolderDataFile hdf = null;
        DataPointFileProcessor dpfp = null;

        int i;

        
        if ( (args.length == 2) && args[0].equals(read_action) )
        {
            ref_input_file = args[1];
        } else if ((args.length == 4) && args[0].equals(write_action) )
        {
            coeff_file_path = args[1];
            points_file_path = args[2];
            ref_output_file = args[3];
        } else {
            System.out.println("Error: sysntax: DataPointFileProcessor write <coeff_file> <points_file> <output_file>");
            System.out.println("OR: DataPointFileProcessor read <ref_input_file>");
            return;
        }

        action = args[0];

        if ( action.equals( write_action ) )
        {
            dpfp = new DataPointFileProcessor(
                    coeff_file_path,
                    points_file_path,
                    ref_output_file
                    );

            dpfp.processTransform();

            // Printing the point content (X,Y,Z) out for reference. 
            dpfp.point_trans.printTransformedPoints();

            hdf = new HolderDataFile(
                    dpfp.getHolderPointFilePath(),
                    true,
                    dpfp.getRefPointList()
                    );

            hdf.writeFileOut();
            hdf.close();
            return;
        }

        if ( action.equals( read_action)  )
        {
            dpfp = new DataPointFileProcessor();

            hdf = new HolderDataFile(
                    ref_input_file,
                    false
                    );

            hdf.readFileIn();

            dpfp.setRefPointList( hdf.getRefPointList() );
            dpfp.printRefPointList();

            hdf.close();
            return;
        }
    }
    * 
    */
    
}


