
#ifndef _CALC_H_
#define _CALC_H_


#include <stdio.h>
#include <math.h>
#include <string.h>


// #define nint(x) ((int)rint((x)))

#define max(x,y)  ((x) > (y) ? (x) : (y))
#define min(x,y)  ((x) < (y) ? (x) : (y))

typedef struct list2 {
	struct list2 *last, *next ;
	void * item ;
} list2, *list2P ;


typedef struct Point2Float {
	float x,y;
} Point2Float;

typedef struct tie_point_2_struc
    {
    Point2Float tiePtsXY, tieSrcXY;
	} tie_point_2;

typedef struct tie_point_2_list 
	{
	struct tie_point_2_list *last,*next;
	tie_point_2			    *tie_point_2P;	
	} tie_point_2_list;


void delete_list(list2 *list) ;
list2 * add_to_list_rear(list2 *list, void * item) ;

tie_point_2_list *
add_point_to_list(tie_point_2_list * l,int x,int y,int tox,int toy) ;

int list_count(list2 *list) ;

int calcgc(
		tie_point_2_list * pts_list,
		double cx[4][4],
		double cy[4][4],
		int max_power,
		int forward) ;
int
readcoeff(char *name, double cx[4][4], double cy[4][4]) ;


int
writecoeff( char *name, double cx[4][4], double cy[4][4]) ;

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
					) ;

int
read_mims_tiepoints(char *name, int nx[3], int ny[3], int mx[3], int my[3] ) ;

int
write_mims_tiepoints( char *name, int nx[3], int ny[3], int mx[3], int my[3] ) ;

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
				) ;

#endif
