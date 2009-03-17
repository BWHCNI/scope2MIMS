/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package holder_data_mgmt;

import holder_transform.*;

/**
 * The class intended to run a transformation of the data points file into the .ref format.
 * @author bepstein
 */
public class DataPointFileProcessor {
    /* constructors */
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

    /* test method */
    public static void main(String[ ] args)
    {
        if (args.length != 3)
        {
            System.out.println("Error: sysntax: DataPointFileProcessor <coeff_file> <points_file> <output_file>");
            return;
        }

        String coeff_file_path = args[0];
        String points_file_path = args[1];
        String ref_output_file = args[2];

        DataPointFileProcessor dpfp = new DataPointFileProcessor(
                coeff_file_path,
                points_file_path,
                ref_output_file
                );

        dpfp.processTransform();

        HolderDataFile hdf = new HolderDataFile(
                dpfp.getHolderPointFilePath(),
                true,
                dpfp.getRefPointList()
                );

        

    }

    /* private variables and methods */
    private String coeff_file_path;
    private String stage_point_file_path;
    private String holder_point_file_path;
    private Transform point_trans;
    private RefPointList rpl;
}
