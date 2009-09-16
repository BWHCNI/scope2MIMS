

#include <stdio.h>
#include <math.h>
#include <string.h>

#include "calc.h"

void
delete_list(list2 *list)
{
	list2 *l, *r ;
	if(list == NULL) return ;
	l = list->next ;
	while( l != list ){
		r = l ;
		l = l->next ;
		free(r->item);
		free(r);
	}
	if(list->item) free(list->item);
	free(list);
}

list2 *
add_to_list_rear(list2 *list, void * item)
{
	list2 *l ;
	if(!(l = (list2 *) calloc(1,sizeof(list2)))){
		return list ;
	}
	if (!list) {
		list = (list2 *) calloc(1,sizeof(list2));
		if(!list) return list ;
		list->last = list->next = list;
	}
	l->last = l->next = l;
	l->item = item;
	l->last = list->last;
	l->next = list;
	list->last->next = l;
	list->last = l;
	return list;
}

tie_point_2_list *
add_point_to_list(tie_point_2_list * l,int x,int y,int tox,int toy)
{
	tie_point_2 * p = (tie_point_2 *) calloc(1,sizeof(tie_point_2));
	if(!p) return l ;
	p->tiePtsXY.x = x;			
	p->tiePtsXY.y = y;			
	p->tieSrcXY.x = tox;		
	p->tieSrcXY.y = toy;		
	l = (tie_point_2_list *) add_to_list_rear((list2 *)l,(void *)p);
	return l;
}

int
list_count(list2 *list)
{
	int count = 0 ;
	list2 *lp ;
	if(list == NULL) return count ;
	for(lp = list->next ; lp != list ; lp = lp->next) count++ ;
	return count ;
}


#define NP 16
#define MP 1000

static int
gs_orthogonalize (
	int  npoints,
	int  ncolumns,
	double datatable[][NP],
	double q[][NP],
	double u[NP][NP]
	)
{
	register 	i,j,column;
	double		product;
	double		size;
	for (j=0; j<ncolumns; j++) {
		for (i=0; i<ncolumns; i++) u[i][j]=0.0;
		for (i=0; i<npoints; i++) q[i][j]=datatable[i][j];
	}
	for (column=0; column<ncolumns; column++) {

/*
 * first normalize column
 */

		size=0.0;
		for (i=0; i<npoints; i++) size += q[i][column]*q[i][column];
		size=sqrt(size);
		u[column][column]= size;
		if (!size) return 0;
		for (i=0; i<npoints; i++) q[i][column]=q[i][column]/u[column][column];
		if (column < ncolumns-1) {
			 for (j=column+1; j<ncolumns; j++) {
				 product=0.0;
				 for (i=0; i<npoints; i++) product += q[i][column]*q[i][j];
				 u[column][j]=product;
				 for (i=0; i<npoints; i++) q[i][j] -= product*q[i][column];
				 }
			 }
		}
	return 1;
}

/*
 * y(jd) = transpose[a(id,jd)] * x(id)
 */

static void
tmavec(
	double x[],
	int	ncolumns,
	int	ip,
	double q[][NP],
	double hold[]
	)
{
	register	i,j;
	for (j=0; j<ip; j++) {
		hold[j]=0.0;
		for (i=0; i<ncolumns; i++) hold[j] += q[i][j]*x[i];
	}
	return;
}

static void 
back_substitute_array(
	double x[],
	int	ncolumns,
	double u[NP][NP],
	double hold[]
	)
{
	register int	i,j;
	double			sum;
	x[ncolumns-1]=hold[ncolumns-1]/u[ncolumns-1][ncolumns-1];
	for (i=ncolumns-2; i>=0; i--) {
		sum=0.0;
		for (j=i+1; j<ncolumns; j++) sum += x[j]*u[i][j];
		x[i]=(hold[i]-sum)/u[i][i];
	}
	return;
}

//actual computation of coefficients

int
calcgc(
		tie_point_2_list * pts_list,
		double cx[4][4],
		double cy[4][4],
		int max_power,
		int forward)
{
	register int 		xpower, ypower,row,column;
	double		*yvector,*xvector;
	double		x,y,xval,yval;
	int n_coefficients = 0;
	double cxwork[NP], cywork[NP];
	double max_x, max_y;

	tie_point_2_list * tp;
	int nrows = 0;
	double		datatable[MP][NP],q[MP][NP],u[NP][NP],hold[NP];

	nrows = list_count((list2 *)pts_list);
	n_coefficients=0;


 	for (ypower=0; ypower<4; ypower++) {
	 	for (xpower=0; xpower<4; xpower++) {
			if (ypower+xpower<=max_power) {
	 			n_coefficients++;
			}
			cx[xpower][ypower] = 0.0 ;
			cy[xpower][ypower] = 0.0 ;
 		}
	}

	if (nrows < n_coefficients) return -1;

	xvector = (double *) calloc(nrows,sizeof(double));
	yvector = (double *) calloc(nrows,sizeof(double));
	row = 0;

	max_x = 0.0;
	max_y = 0.0;

	for(tp = pts_list->next ; tp != pts_list ; tp = tp->next ){
		/* zero order term */
	 	if (forward) {
			xvector[row]= (double) tp->tie_point_2P->tieSrcXY.x;
	 		yvector[row]= (double) tp->tie_point_2P->tieSrcXY.y;
			x			= (double) tp->tie_point_2P->tiePtsXY.x;
	 		y			= (double) tp->tie_point_2P->tiePtsXY.y;
		} else {
			xvector[row]= (double) tp->tie_point_2P->tiePtsXY.x;
	 		yvector[row]= (double) tp->tie_point_2P->tiePtsXY.y;
			x			= (double) tp->tie_point_2P->tieSrcXY.x;
	 		y			= (double) tp->tie_point_2P->tieSrcXY.y;
		}

		max_x = max(x,max_x);
		max_y = max(y,max_y);
	 	
		yval = 1.0;
		column = 0;

		for (ypower = 0; ypower < 4; ypower++) {
			xval = 1.0;
			for (xpower = 0; xpower < 4; xpower++) {
				if (ypower + xpower <= max_power) {
	 				datatable[row][column++]= (double) xval*yval;
				}
				xval *= x;
			}
			yval *= y;
		}
		row++;
	}

 	gs_orthogonalize(nrows,n_coefficients,datatable,q,u);
 	tmavec(xvector,nrows,n_coefficients,q,hold);
 	back_substitute_array(cxwork,n_coefficients,u,hold);
 	tmavec(yvector,nrows,n_coefficients,q,hold);
 	back_substitute_array(cywork,n_coefficients,u,hold);

	column = 0;
	y = max_y;
	for (ypower = 0; ypower < 4; ypower++) {
		x = max_x * y;
		for (xpower = 0; xpower < 4; xpower++) {
			if (xpower + ypower <= max_power) {
	 			cx[ypower][xpower] = cxwork[column];
	 			cy[ypower][xpower] = cywork[column];
				column++;
			} else {
	 			cx[ypower][xpower] = 0.0;
	 			cy[ypower][xpower] = 0.0;
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
				xval =  cx[ypower][xpower] ;
				if(xval < 0.0) xval = xval * -1.0 ;
				if (0.001 > xval * x) cx[ypower][xpower] = 0.0;
				yval =  cy[ypower][xpower] ;
				if(yval < 0.0) yval = yval * -1.0 ;
				if (0.001 > yval * x) cy[ypower][xpower] = 0.0;
			}
			x *= max_x;
		}
		y *= max_y;
	}

	free(xvector);
	free(yvector);

	return 0;

}

int
readcoeff(char *name, double cx[4][4], double cy[4][4])
{
	register int i, j;
	char value[64];
	double atof();
	FILE *f;
	if ((f = fopen(name,"r")) == NULL) {
		return -1;
	}
	for (j=0;j<4;j++) {
		for (i=0;i<4;i++) {
			fgets(value,63,f);
			cx[i][j] = atof(value);
		}
	}
	for (j=0;j<4;j++) {
		for (i=0;i<4;i++) {
			fgets(value,63,f);
			cy[i][j] = atof(value);
		}
	}
	fclose(f);
	return 0;
}


int
writecoeff( char *name, double cx[4][4], double cy[4][4])
{
	register int i,j;
	FILE *f;

	if(name){
		if ((f = fopen(name,"w")) == NULL) 
			return -1;
	}else f = stdout ;

	for(j=0;j<4;j++){
		for (i=0;i<4;i++) {
			fprintf(f,"%g\n",cx[i][j]);
		}
	}
	for(j=0;j<4;j++){
		for (i=0;i<4;i++) {
			fprintf(f,"%g\n",cy[i][j]);
		}
	}
	if(name) fclose(f);
	return 0;
}

int
compute_tr_dpoints ( int *nx,		/* Source Nikon tiepoints */
					 int *ny,
					 int *mx,		/* Source MIMS tiepoints */
					 int *my, 
					 double *fromNikonX,	/* Nikon points to transform */
					 double *fromNikonY,
					 double *toMimsX,	/* Mims points to return */
					 double *toMimsY,
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

	}
	return 0 ;
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

/*
 * Integer version..
 */

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
	}

	return 0 ;
}
