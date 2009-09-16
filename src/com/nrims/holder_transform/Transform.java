/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nrims.holder_transform;

import java.util.*;
import java.io.*;
import com.nrims.holder_data_mgmt.*;

/**
 *
 * @author cpoczatek
 */
public class Transform {
    /* class private constants */
    
    /* Z setting in microns, prior to algorithmic calculation */
    private final double Z_Const_Coord = 4500;

    /* private variables */
    private double[][] XCoefficients;
    private double[][] YCoefficients;
    private ArrayList<double[]> StagePoints;
    private ArrayList<double[]> TransformedPoints;
    private MicroInstrReferencePoints mirp;
    private RefPointList rpl = null;

    /* private methods */
    private RefPoint coordsToRefPoint(double[] coords)
    {
        RefPoint ret_value= new RefPoint();

        if (coords.length == 3)
        {
            ret_value.setXCoord(coords[0]);
            ret_value.setYCoord(coords[1]);
            ret_value.setZCoord(coords[2]);
        }

        return( ret_value );
    }

    private void setCoordsInRefPoint(
        RefPoint rf,
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
        StagePoints = new ArrayList<double[]>();
        TransformedPoints = new ArrayList<double[]>();

        /* Setting stage/instr point storage to nothing */
        mirp = null;
    }

    /* constructors */
    public Transform() {
        init_private_vars();
    }

    public Transform(RefPointList rpl_in)
    {
        init_private_vars();
        setRefPointList( rpl_in );
    }

    public Transform(MicroInstrReferencePoints mi)
    {
        init_private_vars();
        mirp = mi;
    }

    /* public methods */

    public void setCoeffiecents(double[][] xcoeff, double[][] ycoeff) {
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

    public void computeCoefficients(double[][] nikonPoints, double[][] mimsPoints) {

        if (nikonPoints.length != mimsPoints.length) {
            return;
        }

        double[][] XC = new double[4][4];
        double[][] YC = new double[4][4];

        //??? constants from C code
        int NP = 16;
        int MP = 1000;
        int max_power = 1;
        boolean forward = true;

        int xpower, ypower, row, column;
        double[] yvector, xvector;
        double x, y, xval, yval;
        int n_coefficients = 0;
        double[] cxwork = new double[NP];
        double[] cywork = new double[NP];
        double max_x, max_y;

        //tie_point_2_list * tp;
        int nrows = 0;
        double[][] datatable = new double[MP][NP];
        double[][] q = new double[MP][NP];
        double[][] u = new double[NP][NP];
        double[] hold = new double[NP];

        //nrows = list_count((list2 *)pts_list);
        nrows = nikonPoints.length;
        n_coefficients = 0;


        for (ypower = 0; ypower < 4; ypower++) {
            for (xpower = 0; xpower < 4; xpower++) {
                if (ypower + xpower <= max_power) {
                    n_coefficients++;
                }
                XC[xpower][ypower] = 0.0;
                YC[xpower][ypower] = 0.0;
            }
        }

        if (nrows < n_coefficients) {
            return;
        }

        //xvector = (double *) calloc(nrows,sizeof(double));
        //yvector = (double *) calloc(nrows,sizeof(double));
        xvector = new double[nrows];
        yvector = new double[nrows];

        row = 0;

        max_x = 0.0;
        max_y = 0.0;

        for (int i = 0; i < nikonPoints.length; i++) {
            /* zero order term */
            if (forward) {
                xvector[row] = nikonPoints[i][0];
                yvector[row] = nikonPoints[i][1];
                x = mimsPoints[i][0];
                y = mimsPoints[i][1];
            } else {
                xvector[row] = mimsPoints[i][0];
                yvector[row] = mimsPoints[i][1];
                x = nikonPoints[i][0];
                y = nikonPoints[i][1];
            }

            max_x = java.lang.Math.max(x, max_x);
            max_y = java.lang.Math.max(y, max_y);

            yval = 1.0;
            column = 0;

            for (ypower = 0; ypower < 4; ypower++) {
                xval = 1.0;
                for (xpower = 0; xpower < 4; xpower++) {
                    if (ypower + xpower <= max_power) {
                        datatable[row][column++] = (double) xval * yval;
                    }
                    xval *= x;
                }
                yval *= y;
            }
            row++;
        }

        gs_orthogonalize(nrows, n_coefficients, datatable, q, u);
        tmavec(xvector, nrows, n_coefficients, q, hold);
        back_substitute_array(cxwork, n_coefficients, u, hold);
        tmavec(yvector, nrows, n_coefficients, q, hold);
        back_substitute_array(cywork, n_coefficients, u, hold);

        column = 0;
        y = max_y;
        for (ypower = 0; ypower < 4; ypower++) {
            x = max_x * y;
            for (xpower = 0; xpower < 4; xpower++) {
                if (xpower + ypower <= max_power) {
                    XC[ypower][xpower] = cxwork[column];
                    YC[ypower][xpower] = cywork[column];
                    column++;
                } else {
                    XC[ypower][xpower] = 0.0;
                    YC[ypower][xpower] = 0.0;
                }
                x *= max_x;
            }
            y *= max_y;
        }

        y = 1.0;
        for (ypower = 0; ypower < 4; ypower++) {
            x = y;
            for (xpower = 0; xpower < 4; xpower++) {
                if (xpower + ypower <= max_power) {
                    xval = XC[ypower][xpower];
                    if (xval < 0.0) {
                        xval = xval * -1.0;
                    }
                    if (0.001 > xval * x) {
                        YC[ypower][xpower] = 0.0;
                    }
                    yval = YC[ypower][xpower];
                    if (yval < 0.0) {
                        yval = yval * -1.0;
                    }
                    if (0.001 > yval * x) {
                        YC[ypower][xpower] = 0.0;
                    }
                }
                x *= max_x;
            }
            y *= max_y;
        }

        //Print coeffs
        System.out.println("Transform: ");
        System.out.println("X Coefficients: ");
        for(int i = 0; i<4; i++) {
            for(int j = 0; j<4; j++) {
                System.out.print(XC[i][j]+" ");
            }
            System.out.println("");
        }
        System.out.println("Y Coefficients: ");
        for(int i = 0; i<4; i++) {
            for(int j = 0; j<4; j++) {
                System.out.print(YC[i][j]+" ");
            }
            System.out.println("");
        }
        System.out.println("");

    }



    public double[][] gs_orthogonalize(	int  npoints, int  ncolumns, double datatable[][], double q[][], double u[][]) {
	//register 	i,j,column;
	double		product;
	double		size;
    double[][] returnarray = new double[q.length][q[0].length];

	for (int j=0; j<ncolumns; j++) {
		for (int i=0; i<ncolumns; i++) u[i][j]=0.0;
		for (int i=0; i<npoints; i++) q[i][j]=datatable[i][j];
	}

	for (int column=0; column<ncolumns; column++) {

        /*
        * first normalize column
        */

		size=0.0;
		for (int i=0; i<npoints; i++) size += q[i][column]*q[i][column];
		size=java.lang.Math.sqrt(size);
		u[column][column]= size;
		//if (!size) return;
		for (int i=0; i<npoints; i++) q[i][column]=q[i][column]/u[column][column];
		if (column < ncolumns-1) {
			 for (int j=column+1; j<ncolumns; j++) {
				 product=0.0;
				 for (int i=0; i<npoints; i++) product += q[i][column]*q[i][j];
				 u[column][j]=product;
				 for (int i=0; i<npoints; i++) q[i][j] -= product*q[i][column];
				 }
			 }
		}

        return returnarray;
    }


    public void tmavec(double[] x, int ncolumns, int ip, double[][] q, double[] hold) {
        //register	i,j;
        for (int j = 0; j < ip; j++) {
            hold[j] = 0.0;
            for (int i = 0; i < ncolumns; i++) {
                hold[j] += q[i][j] * x[i];
            }
        }
        return;
    }

    public void back_substitute_array(double[] x, int ncolumns, double[][] u, double[] hold) {
        //register int	i,j;
        double sum;
        x[ncolumns - 1] = hold[ncolumns - 1] / u[ncolumns - 1][ncolumns - 1];
        for (int i = ncolumns - 2; i >= 0; i--) {
            sum = 0.0;
            for (int j = i + 1; j < ncolumns; j++) {
                sum += x[j] * u[i][j];
            }
            x[i] = (hold[i] - sum) / u[i][i];
        }
        return;
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
    
    public void setStagePoints(double[][] pts) {
        for(int i = 0; i < pts.length; i++) {
            StagePoints.add(pts[i]);
        }
    }
    
    public void clearStagePoints() {
        StagePoints.clear();
    }
    
    public  void printStagePoints() {
        System.out.println("Stage Points:");
        double[] pt = new double[3];
        for(int i = 0; i < StagePoints.size(); i++) {
            pt = TransformedPoints.get(i);
            System.out.println(pt[0] + ", " + pt[1] + ", " + pt[2]);
        }
        System.out.println("");
    }
    
    public void readStagePointsFile(String filename) {
        
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
                StagePoints.add(pts);
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
        TransformedPoints.clear();
    }

    public ArrayList getTransformedPoints()
    {
        return( TransformedPoints );
    }

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
        
        for (i = 0; i < TransformedPoints.size(); i++)
        {
            point_coords = TransformedPoints.get(i);
            rp = coordsToRefPoint(point_coords);
            rp.setComment( ret_value.getDefaultRefPointComment() );
            ret_value.addRefPoint( rp );
        }

        return ( ret_value );
    }

    public void setRefPointList(RefPointList rpl_in)
    {
        rpl = rpl_in;
    }

    public RefPointList getRefPointList()
    {
        return( rpl );
    }

    public void setTransformedPoints() {
        double[] temppoint = new double[3];
        
        clearTransformedPoints();
        for (int p = 0; p < StagePoints.size(); p++) {
            temppoint = this.transformPoint(StagePoints.get(p));
            TransformedPoints.add(temppoint);
        }
    }
    
    public void printTransformedPoints() {
        System.out.println("Transformed Points:");
        double[] pt = new double[3];
        for (int i = 0; i < TransformedPoints.size(); i++) {
            pt = TransformedPoints.get(i);
            System.out.println(pt[0] + ", " + pt[1] + ", " + pt[2]);
        }
        System.out.println("");
    }

    public void setMicroInstrPoints(MicroInstrReferencePoints mp)
    {
        mirp = mp;
    }

    public MicroInstrReferencePoints getMicroInstrPoints()
    {
        return( mirp );
    }

    public void setTransformCoefficients()
    {
        setTransformCoefficients( getMicroInstrPoints() );
    }

    public void setTransformCoefficients(MicroInstrReferencePoints mp)
    {

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

        test.computeCoefficients(nikon, mims);


    }


}
