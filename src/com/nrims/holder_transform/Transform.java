/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nrims.holder_transform;

import com.nrims.holder_data.REFPoint;
import java.util.*;
import java.io.*;

/**
 * Coordinate point transformation calculation class.
 * Each instance incorporates the necessary set of incoming and resulting data.
 * @author cpoczatek
 */
public class Transform {
    /* class private constants */
    
    /* Z setting in microns, prior to algorithmic calculation */
    private final double Z_Const_Coord = 4500;

    /* private variables */
    private double[][] XCoefficients;
    private double[][] YCoefficients;
    private ArrayList<double[]> srcPoints;
    private ArrayList<double[]> transformedPoints;
    //private RefPointList rpl = null;

    /* private methods */
    private REFPoint coordsToRefPoint(double[] coords)
    {
        REFPoint ret_value= new REFPoint();

        if (coords.length == 3)
        {
            ret_value.setXCoord(coords[0]);
            ret_value.setYCoord(coords[1]);
            ret_value.setZCoord(coords[2]);
        }

        return( ret_value );
    }

    private void setCoordsInRefPoint(
        REFPoint rf,
        double[] coords
        )
    {
        if ( (rf != null) &&
             (coords != null) &&
             (coords.length == 3) )
        {
            rf.setXCoord(coords[0]);
            rf.setYCoord(coords[1]);
            rf.setZCoord(coords[2]);
        }

    }

    private void init_private_vars()
    {
        //initialize coefficient matrix to 0
        XCoefficients = new double[4][4];
        YCoefficients = new double[4][4];
        srcPoints = new ArrayList<double[]>();
        transformedPoints = new ArrayList<double[]>();

    }

    /* constructors */

    /**
     * Basic constructor
     */
    public Transform() {
        init_private_vars();
    }


    /* public methods */

    public void setCoefficients(double[][] xcoeff, double[][] ycoeff) {
        if(xcoeff==null || ycoeff==null)
            return;
        if( (xcoeff.length!=4) || (ycoeff.length!=4) )
            return;
        if( (xcoeff[0].length!=4) || (ycoeff[0].length!=4) )
            return;
        for(int i = 0; i<4; i++) {
            for(int j = 0; j<4; j++) {
                XCoefficients[i][j] = xcoeff[i][j];
                YCoefficients[i][j] = ycoeff[i][j];
            }
        }
    }
    
    public void setCoefficientsFromList(double[] list)
    {
        if( list.length != (16*2) )
            return;

        int pos = 0;
        
        //note flipped indices/for loops
        //might be due to Cameca's flipped coordinate system
        for (int j=0; j<4 ; j++)
        {
            for (int i=0; i<4; i++)
            {
                XCoefficients[i][j] = list[pos];
                pos = pos +1;
            }
        }

        for (int j=0; j<4; j++)
        {
            for (int i=0; i<4; i++)
            {
                YCoefficients[i][j] = list[pos];
                pos = pos +1;
            }
        }
    }




    public double[][] getXCoefficients() {
        return XCoefficients;
    }

    public double[][] getYCoefficients() {
        return YCoefficients;
    }
    
    public void printCoefficients() {
        System.out.println("Transform: ");
        System.out.println("X Coefficients: ");
        for(int i = 0; i<4; i++) {
            for(int j = 0; j<4; j++) {
                System.out.print(XCoefficients[i][j]+" ");
            }
            System.out.println("");
        }
        System.out.println("Y Coefficients: ");
        for(int i = 0; i<4; i++) {
            for(int j = 0; j<4; j++) {
                System.out.print(YCoefficients[i][j]+" ");
            }
            System.out.println("");
        }
        System.out.println("");
    }
    
    
    //Move this to DataIO. Shouldn't read files with tranform class. 
    
    public void readCoefficientsFile(String filename) {
        double[] coeff = new double[32];
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line;
            for(int i =0; i<32; i++) {
                if ((line = br.readLine()) != null) {
                    coeff[i] = Double.parseDouble(line);
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        
        this.setCoefficientsFromList(coeff);
    }
    
    
    public void setSrcPoints(double[][] pts) {
        for(int i = 0; i < pts.length; i++) {
            srcPoints.add(pts[i]);
        }
    }
    
    public void clearSrcPoints() {
        srcPoints.clear();
    }
    
    public  void printSrcPoints() {
        System.out.println("Source Points:");
        double[] pt = new double[3];
        for(int i = 0; i < srcPoints.size(); i++) {
            pt = transformedPoints.get(i);
            System.out.println(pt[0] + ", " + pt[1] + ", " + pt[2]);
        }
        System.out.println("");
    }
    
    
    // Farah: this should go in DataIO...
    public void readStagePointsFile(String filename) {
        this.clearSrcPoints();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line;
            
            while( ( line = br.readLine() ) != null) {
                if(line.equals("STAGE_LIST") || line.equals("UNITS_UM") || line.equals("") ) {
                    continue;
                }
                String[] stringpts = line.split(" ");
                double[] pts = new double[3];
                if(stringpts.length != 3) {
                    continue;
                }
                for(int i =0; i< pts.length; i++) {
                    pts[i] = Double.parseDouble(stringpts[i]);
                }
                srcPoints.add(pts);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    public double[] transformPoint(double[] stagept) {
        double[] temppoint = new double[3];
        
        temppoint[0] = XCoefficients[0][0] + (XCoefficients[0][1] * stagept[0]) + (XCoefficients[1][0] * stagept[1]);
        temppoint[1] = YCoefficients[0][0] + (YCoefficients[0][1] * stagept[0]) + (YCoefficients[1][0] * stagept[1]);
        temppoint[2] = Z_Const_Coord;
        
        return temppoint;
    }
//Original, from Doug's code
/*        
void
rotate_nikon_to_mims(int spts,
	double *stgX, double *stgY,
	double cx[4][4], double cy[4][4] )
{
        printf("\nInside rotate_nikon_to_mims()\n");
        fflush(stdout);
	int i ;
	double sx, sy ;

	for(i=0;i<spts;i++){
		sx = stgX[i] ;
		sy = stgY[i] ;
		stgX[i] = cx[0][0] + cx[0][1] * sx + cx[1][0] * sy ;
		stgY[i] = cy[0][0] + cy[0][1] * sx + cy[1][0] * sy ;
	}
}
        
*/        
    
    public void clearTransformedPoints() {
        transformedPoints.clear();
    }

    public ArrayList getTransformedPoints()
    {
        return( transformedPoints );
    }

    public ArrayList<REFPoint> transformedPointsToRefPoints() {
        ArrayList<REFPoint> refList = new ArrayList<REFPoint>();
        REFPoint rp;
        double[] point_coords;
        
        for (int i = 0; i < transformedPoints.size(); i++) {
            point_coords = transformedPoints.get(i);
            rp = coordsToRefPoint(point_coords);
            rp.setComment(i + " " + REFPoint.getDefaultComment());
            refList.add(rp);
        }
        
        return refList;
    }

    public void setTransformedPoints() {
        double[] temppoint = new double[3];
        
        clearTransformedPoints();
        for (int p = 0; p < srcPoints.size(); p++) {
            temppoint = this.transformPoint(srcPoints.get(p));
            transformedPoints.add(temppoint);
        }
    }
    
    public void printTransformedPoints() {
        System.out.println("Transformed Points:");
        double[] pt = new double[3];
        for (int i = 0; i < transformedPoints.size(); i++) {
            pt = transformedPoints.get(i);
            System.out.println(pt[0] + ", " + pt[1] + ", " + pt[2]);
        }
        System.out.println("");
    }
    
    //Pass the coefficient arrays directly.
    public void setTransformCoeffs(double[][] x, double[][] y) {
        XCoefficients = x;
        YCoefficients = y;
    }

    /* Test method.
     * There to run a quick verification test.
     */
    public static void main(String[ ] args) {
        Transform test = new Transform();
        
        //test values
        //double[][] cx = { {-11464,0.00502499,0,0}, {-0.999791,0,0,0}, {0,0,0,0}, {0,0,0,0} };
        //double[][] cy = { {-16620,1.00084,0,0}, {0.00527921,0,0,0}, {0,0,0,0}, {0,0,0,0} };
        //Above don't work, coeff's read in "backwards" in original...
        //ie, for j= 0 to 3 { for i = 0 to 3 { cx[i][j] = number from file } }
        
         /*test example, works 
        double[] foo = {-11464, 0.00502499, 0, 0, -0.999791, 0, 0 ,0 ,0 ,0, 0, 0, 0, 0, 0, 0, -16620, 1.00084, 0, 0, 0.00527921, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        
        double[][] pts = {{0, 0, 0},
            {-22087.6, -475.2, 0},
            {5424.6, 6414.2, 0},
            {5210.4, 19684, 0},
            {-16897.3, 19522.1, 0},
            {-22297.6, 12811.1, 0},
            {-16757.7, 6097.5, 0},
            {-13612.1, 491.4, 0},
            {-13612.1, 2222.3, 0},
            {-24841.6, 2242.5, 0},
            {-24253.2, 2688.9, 0},
            {-24253, 636, 0},
            {-25172.1, 635.6, 0},
            {3371.8, 8313.9, 0},
            {2553.5, 8273.8, 0},
            {3218.5, 6626.8, 0},
            {3763.4, 6627.2, 0},
            {-8750.2, 7632.9, 0},
            {-7689.2, 7896.4, 0},
            {-7814.7, 9714.9, 0},
            {-8709.4, 9713.3, 0},
            {-19380.8, 8179, 0},
            {-18980.9, 6286.1, 0},
            {-4194, 14176.6, 0},
            {-4089.8, 15440.8, 0},
            {-2128.6, 15545.5, 0},
            {-2128.7, 14174.1, 0},
            {-24444.5, 14149, 0},
            {-26408.8, 14767.9, 0},
            {3151, 22623.4, 0},
            {3574.1, 21474.9, 0},
            {857.3, 22093.3, 0},
            {1370, 20969, 0},
            {-9377.2, 22376.8, 0},
            {-9001.6, 20673.5, 0},
            {-7170.5, 21282.4, 0},
            {-7325.8, 22925.9, 0},
            {-19824, 22087.2, 0}
        };
        */
        /*
        test.readCoefficientsFile("./src/holder_transform/coeff.txt");
        test.readStagePointsFile("./src/holder_transform/xy.points");
        
        test.setTransformedPoints();
        
        test.printStagePoints();
        test.printCoefficients();
        test.printTransformedPoints();
        */

        double[][] nikon =  {{0, 0},
            {-22005, -226},
            {5333, 6729},
            {-16248, 6999}};

        double[][] mims = {{-13096, -16154},
            {8899, -16331},
            {-18487, -9438},
            {-3107, -9141}};
        ComputeCoefficients_n2mc compute = new ComputeCoefficients_n2mc();
        compute.computeCoefficients(nikon, mims);


    }
    

    // Stuff I don't think needs to be in this class:
    
    /**
     * Sets the internal RefPointList to rpl_in
     * @param rpl_in
     
    public Transform(RefPointList rpl_in)
    {
        init_private_vars();
        setRefPointList( rpl_in );
    }
    * 
        /**
     * Sets the RefPointList for the object.
     * @param rpl_in RefPointList to set
    
    public void setRefPointList(RefPointList rpl_in)
    {
        rpl = rpl_in;
    }

    public RefPointList getRefPointList()
    {
        return( rpl );
    }
    * 
    * 
    public RefPointList transformedPointsToRefPointList()
    {
        RefPointList ret_value;
        RefPoint rp;

        if ( rpl == null )
            ret_value = new RefPointList();
        else
            ret_value = rpl;

        double[] point_coords;
        int i;
        
        for (i = 0; i < transformedPoints.size(); i++)
        {
            point_coords = transformedPoints.get(i);
            rp = coordsToRefPoint(point_coords);
            rp.setComment( ret_value.getDefaultRefPointComment() );
            ret_value.addRefPoint( rp );
        }

        return ( ret_value );
    }
    * 
    */
}
