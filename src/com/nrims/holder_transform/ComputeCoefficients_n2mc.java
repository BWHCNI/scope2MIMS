/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nrims.holder_transform;

/**
 * The methods in this class are a direct port to java (with syntax fixes) from the
 * c script n2mc
 * @author fkashem
 */
public class ComputeCoefficients_n2mc {
    double[][] XC = new double[4][4];
    double[][] YC = new double[4][4];
    double[][] nikonPts;
    double[][] mimsPts;
    
    /* Constructor */
    public ComputeCoefficients_n2mc() {
        
    }
    
    /* Getters */
    
    public double[][] getXCoeff() {
        return XC;
    }
    
    public double[][] getYCoeff() {
        return YC;
    }
    
    public double[][] getNikonPts() {
        return nikonPts;
    }
    
    public double[][] getMimsPts() {
        return mimsPts;
    }
    
    
    
    //Below is ported from the c code
    public void computeCoefficients(double[][] nikonPoints, double[][] mimsPoints) {

        if (nikonPoints.length != mimsPoints.length) {
            return;
        }
        
        // Copy these to global variables so they can be queried from this object.
        nikonPts = nikonPoints;
        mimsPts = mimsPoints;
        
        /* Moved to global variables */
        //double[][] XC = new double[4][4];
        //double[][] YC = new double[4][4];
        
       
        System.out.println("Nikon points being passed properly?");
        for(int i = 0; i < nikonPoints.length; i++){
        System.out.println(nikonPoints[i][0] + " " + nikonPoints[i][1]);
        }
        System.out.println("What about the mims points?");
        
        for(int i = 0; i < mimsPoints.length; i++) {
        System.out.println(mimsPoints[i][0] + " " + mimsPoints[i][1]);
        }
        
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
                xvector[row] = mimsPoints[i][0];
                yvector[row] = mimsPoints[i][1];
                x = nikonPoints[i][0];
                y = nikonPoints[i][1];
            } else {
                xvector[row] = nikonPoints[i][0];
                yvector[row] = nikonPoints[i][1];
                x = mimsPoints[i][0];
                y = mimsPoints[i][1];
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
                        XC[ypower][xpower] = 0.0;
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
                /*Note from Farah: why is the following line commented out? */
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
}
