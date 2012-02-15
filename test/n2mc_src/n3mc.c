

#include <stdio.h>
#include <math.h>
#include <string.h>
#include "calc.h"

#if 0
int
compute_tr_points ( int *nx,		/* Source Nikon tiepoints */
					int *ny,
					int *mx,		/* Source MIMS tiepoints */
					int *my, 
					int *fromNikonX,	/* Nikon points to transform */
					int *fromNikonY,
					int *toMimsX,	/* Mims points to return */
					int *toMimsY,
					int   npts
				)
{
	double d12, d22, d32 ;
	double dx, dy, sx, sy ;
	double Ax12,Ay12,Ax32,Ay32,Bx12,By12,Bx32,By32 ;
	double xden, y1, y2 ;
	int i ;

	if(npts == 0) return 0 ;

	if(	 fromNikonX == NULL
	  || fromNikonY == NULL
	  || toMimsX == NULL
	  || toMimsY == NULL ) return -1 ;

/*  We only do these once .. */

/*
 * Calculating from Mims corner points, these are MIMS positions for the three corner points.
 * Ax12 = x2 - x1
 * Ay12 = y2 - y1
 * Ax32 = x3 - x2
 * Ay32 = y3 - y2
 * Bx12 = x2 + x1
 * By12 = y2 + y1 
 * Bx32 = x2 + x3
 * By32 = y2 + y3
 */

	Ax12 = mx[1] - mx[0] ;
	Ay12 = my[1] - my[0] ;
	Ax32 = mx[2] - mx[1] ;
	Ay32 = my[2] - my[1] ;
	Bx12 = mx[1] + mx[0] ;
	By12 = my[1] + my[0] ;
	Bx32 = mx[2] + mx[1] ;
	By32 = my[2] + my[1] ;

/*
 * Calculated from Nikon points (xa, ya),
 * these are Nikon positions for the three corner points.
 * d12 = (x - x1) 2 + (y - y1) 2
 * d22 = (x - x2) 2 + (y - y2) 2
 * d32 = (x - x3) 2 + (y - y3) 2
 * Point inside triangle that we are trying to place M(x,y)
 */

	for(i=0;i<npts;i++){

		sx = fromNikonX[i] ;
		sy = fromNikonY[i] ;

		dx = (sx - nx[0]) ; 
		dy = (sy - ny[0]) ;
		d12 =  dx*dx + dy*dy ;

		dx = (sx - nx[1]) ; 
		dy = (sy - ny[1]) ;
		d22 =  dx*dx + dy*dy ;

		dx = (sx - nx[2]) ; 
		dy = (sy - ny[2]) ;
		d32 =  dx*dx + dy*dy ;


/*
 *  Solve for X.
 *  X =  ( - Ay32 (d12 - d22) - Ay12 (d32 - d22) - Ay32 Ay12 By12 
 *	   	 + By32 Ay32 Ay12 - Ay32 Ax12 Bx12 + Bx32 Ay12 Ax32   )
 *	   / ( - 2 (Ay32 Ax12 - Ay12 Ax32) )
 */

		xden = -2 * ( Ay32 * Ax12 - Ay12 * Ax32 ) ;

		if(xden == 0.0)  toMimsX[i] = 0.0 ;
		else
			toMimsX[i] = (
				- ( Ay32 * (d12 - d22) )
				- ( Ay12 * (d32 - d22) )
				- ( Ay32 * Ay12 * By12 )
				+ ( By32 * Ay32 * Ay12 )
				- ( Ay32 * Ax12 * Bx12 )
				+ ( Bx32 * Ay12 * Ax32 ) )
				/ xden ;

/*
 * Use either of the two following equation to solve for Y.
 */


/*
 * Solve for Y, all x values refer to Mims coordinates.
 * (d12 - d22 - Ax12 (2x - Bx12))      +    By12      =         y
 *                        2Ay12		                  2
 */

 		if(Ay12 == 0.0)  y1 = 0.0 ;
		else
			y1 = 
				( d12 - d22 - Ax12 * ( 2 * toMimsX[i] - Bx12 ) ) / ( 2 * Ay12 )
				+ By12 / 2 ;


/*
 * Solve for Y.
 * (d22 - d32 - Ax32 (2x - Bx32))      +    By32      =         y
 *                        2Ay32		                 2
 */ 

 		if(Ay32 == 0.0)  y2 = 0 ;
		else
			y2 = 
				( d22 - d32 - Ax32 * ( 2 * toMimsX[i] - Bx32 ) ) / ( 2 * Ay32 )
				+ By32 / 2 ;
/*
 * Try both ways..
 */

		if(y1 == 0) toMimsY[i] = y2 ;
		else if(y2 == 0) toMimsY[i] = y1 ;
		else if(y1 != y2) toMimsY[i] = (y1+y2)/2.0 ;
		else toMimsY[i] = y1 ;

		fprintf(stderr,"X %d  y1 %g y2 %g -> %d\n",
			toMimsX[i], y1,y2, toMimsY[i]);
	}
}


int
read_mims_tiepoints(char *name, int nx[3], int ny[3], int mx[3], int my[3] )
{
	register int i, rc = 0 ;
	char value[128];
	char *anx, *any, *amx, *amy ;
	FILE *f;
	if ((f = fopen(name,"r")) == NULL) {
		return -1;
	}
	for (i=0;i<3;i++) {
		fgets(value,127,f);
		anx = strtok(value," \t\n,;");
		any = strtok(NULL," \t\n,;");
		amx = strtok(NULL," \t\n,;");
		amy = strtok(NULL," \t\n,;");
		if(anx && any && amx && amy ){
			nx[i] = atoi(anx);
			ny[i] = atoi(any);
			mx[i] = atoi(amx);
			my[i] = atoi(amy);
		}else rc = -1 ;
	}
	fclose(f);
	return rc;
}


int
write_mims_tiepoints( char *name, int nx[3], int ny[3], int mx[3], int my[3] )
{
	register int i,j;
	FILE *f;

	if(name){
		if ((f = fopen(name,"w")) == NULL) 
			return -1;
	}else f = stdout ;

	for (i=0;i<3;i++) {
		fprintf(f,"%d %d %d %d\n",nx[i],ny[i],mx[i],my[i]);
	}
	if(name) fclose(f);
	return 0;
}

#endif


helpmsg()
{
	printf("\n");
	printf("Commands\n");
	printf("a    Prompts to append a sequence of Nikon/MIMS tiepoints\n");
	printf("w    Prompts for a filename to save the tiepoints\n");
	printf("r    Prompts for a filename to read tiepoints\n");
	printf("d    Prompts to delete a pair of tiepoints\n");
	printf("t    Prompts a Nikon x,y and computes MIMS X,Y\n");
	printf("l    List the existing tiepoints\n");
	printf("q	 Quit\n");
}


main(int argc, char **argv)
{
	char instr[80], *xs, *ys ;
	int i, j, reading = 1 ;
	double atof();
	int nx[3], ny[3], mx[3], my[3] ;
	int tnx[2], tny[2], tmx[2], tmy[2] ;
	int dx1, dy1, dx2, dy2 ;
	int n = 0 ;

	while(reading){
		printf("> ");
		fgets(instr,79,stdin);
		switch(instr[0]){
			default: helpmsg(); break ;
			case 'q': reading = 0 ; break ;
			break ;

			case 'a':
				if(n < 3) {
					printf("Enter Nikon TiePoint[%d] X,Y> ", n+1 );
					fgets(instr,79,stdin);
					xs = strtok(instr," ,\t\n");
					ys = strtok(NULL," ,\t\n");
					if(xs && ys){
						dx1 = atoi(xs) ;
						dy1 = atoi(ys) ;
					}else break ;
					printf("Enter MIM's X,Y> ");
					fgets(instr,79,stdin);
					xs = strtok(instr," ,\t\n");
					ys = strtok(NULL," ,\t\n");
					if(xs && ys){
						dx2 = atoi(xs) ;
						dy2 = atoi(ys) ;
					}else break ;
					nx[n] = dx1 ;
					ny[n] = dy1 ;
					mx[n] = dx2 ;
					my[n] = dy2 ;
					n++ ;
					printf("Point[%d] Nikon %d,%d Mims %d,%d\n",
						n, dx1,dy1, dx2,dy2);
				}else fprintf(stderr,"You have 3 points. Use 'd' to delete one first\n");
			break ;

			case 't':
				if(n < 3) {
					fprintf(stderr,"You have %d Tiepoints points.\n"
						"Use 'a' to append new points or\n"
						"    'r' to read a Tiepoints file.\n", n);
				}else{
					printf("Enter Nikon test point X,Y> ");
					fgets(instr,79,stdin);
					xs = strtok(instr," ,\t\n");
					ys = strtok(NULL," ,\t\n");
					if(xs && ys){
						dx1 = atoi(xs) ;
						dy1 = atoi(ys) ;
					}else break ;

					tnx[0] = dx1 ;
					tny[0] = dy1 ;
					compute_tr_points ( nx, ny, mx, my, tnx, tny, tmx, tmy, 1) ;
					printf("Point Nikon X,Y = %d, %d   Mims X,Y = %d,%d\n",
						dx1,dy1, tmx[0],tmy[0]);
				}
			break ;

			case 'r':
				printf("Enter a filename to read a set of Tiepoints > ");

				fgets(instr,79,stdin);

				if(strlen(instr) > 2) {
					instr[strlen(instr)-1] = '\0';

					if ( read_mims_tiepoints(instr,nx,ny,mx,my ) ){
						fprintf(stderr,"Reading %s Failed\n", instr);

					}else{
						printf("1: Nikon %d,%d - MIMS %d,%d\n", nx[0],ny[0], mx[0],my[0]);
						printf("2: Nikon %d,%d - MIMS %d,%d\n", nx[1],ny[1], mx[1],my[1]);
						printf("3: Nikon %d,%d - MIMS %d,%d\n", nx[2],ny[2], mx[2],my[2]);
					}
				}
			break;

			case 'w':
				if(n < 3){
					fprintf(stderr,"Need 3 tiepoints to write!\n");
					break ;
				}
				printf("Enter a filename to save the coefficients > ");
				fgets(instr,79,stdin);
				if(strlen(instr) > 2){
					instr[strlen(instr)-1] = '\0';
					if( write_mims_tiepoints(instr,nx,ny,mx,my) ) {
						fprintf(stderr,"Writing %s Failed\n", instr);
					}
				}
			break ;
				
			case 'l':
				for(i=0;i<n;i++){
					printf("Point[%d] Nikon %d,%d Mims %d,%d\n",
						i+1, nx[i],ny[i], mx[i],my[i]);
				}
			break ;

			case 'd':
				if(n == 0) break ;
				for(i=0;i<n;i++){
					printf("Point[%d] Nikon %d,%d Mims %d,%d\n",
					i+1, nx[i],ny[i], mx[i],my[i]);
				}
				printf("\nEnter point to delete [a for all]> ");
				fgets(instr,79,stdin);
				if(instr[0] == 'a'){
					n = 0 ;
				}else{
					i = atoi(instr);
					if( i > 0 && i <= n){
						int j,k;
						for(j=i;j<n;j++){
							nx[j-1] = nx[j];
							ny[j-1] = ny[j];
							mx[j-1] = mx[j];
							my[j-1] = my[j];
						}
						n-- ;
					}
					for(i=0;i<n;i++){
						printf("Point[%d] Nikon %d,%d Mims %d,%d\n",
						i+1, nx[i],ny[i], mx[i],my[i]);
					}
				}
			break ;
		}
	}
	return 0 ;
}
