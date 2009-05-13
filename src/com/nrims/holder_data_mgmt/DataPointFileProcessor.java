/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nrims.holder_data_mgmt;

import com.nrims.holder_transform.*;

/**
 * The class intended to run a transformation of the data points file into the .ref format.
 * @author bepstein
 */
public class DataPointFileProcessor {
    /* private variables and methods */
    private String coeff_file_path;
    private String stage_point_file_path;
    private String holder_point_file_path;
    private Transform point_trans;
    private RefPointList rpl;

    private void printRefPoint(RefPoint rp)
    {
        System.out.println("X: " + rp.getXCoord());
        System.out.println("Y: " + rp.getYCoord());
        System.out.println("Z: " + rp.getZCoord());
        System.out.println("Comment: " + rp.getComment());
        System.out.println("Date: " + rp.getDateString());
    }

    /* constructors */
    public DataPointFileProcessor()
    {

    }
    
    public DataPointFileProcessor(
            String coeff_file,
            String stage_point_file,
            String holder_output_file)
    {
        point_trans = new Transform();
        setCoeffFilePath( coeff_file );
        setStagePointFilePath( stage_point_file );
        setHolderPointFilePath( holder_output_file );
        rpl = null;
    }

    /* public methods */
    public void setCoeffFilePath(String path)
    {
        coeff_file_path = path;
    }

    public String getCoeffFilePath()
    {
        return( coeff_file_path );
    }

    public void setStagePointFilePath(String path)
    {
        stage_point_file_path = path;
    }

    public String getPointFilePath()
    {
        return( stage_point_file_path );
    }

    public void setHolderPointFilePath(String path)
    {
        holder_point_file_path = path;
    }

    public String getHolderPointFilePath()
    {
        return( holder_point_file_path );
    }

    public void processTransform()
    {
        point_trans.readCoefficientsFile( getCoeffFilePath() );
        point_trans.readStagePointsFile( getPointFilePath() );
        point_trans.setTransformedPoints();
        rpl = point_trans.transformedPointsToRefPointList();
    }

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

    /*
     * This routine generates the holder ref file based upon the 
     * coefficients and point corrdintates file.
     */
    public void generateRefPointFile()
    {
        HolderDataFile hdf;

        if ( getCoeffFilePath() == null )
            return;

        if ( getPointFilePath() == null )
            return;

        if ( getHolderPointFilePath() == null )
            return;

        processTransform();

        hdf = new HolderDataFile(
                    getHolderPointFilePath(),
                    true,
                    getRefPointList()
                    );

        hdf.writeFileOut();
        hdf.close();
    }

    /* test method */
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

            /* Printing the point content (X,Y,Z) out for reference. */
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
    
}


