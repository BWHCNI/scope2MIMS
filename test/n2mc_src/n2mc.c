
#include <stdio.h>
#include <math.h>
#include <string.h>
#include "calc.h"


helpmsg()
{
	printf("\n");
	printf("Commands\n");
	printf("a    Prompts to append a sequence of Nikon/MIMS tiepoints\n");
	printf("c    Computes and prints the translation/rotation coefficents\n");
	printf("w    Prompts for a filename to save the coefficients\n");
	printf("r    Prompts for a filename to read coefficients\n");
	printf("d    Prompts to delete a pair of coefficients\n");
	printf("t    Prompts a Nikon x,y and computes MIMS X,Y\n");
	printf("q	 Quit\n");
}

int
compute_transform( int n, int *xp, int *yp, int *xq, int *yq, int maxPower,
			double cx[4][4], double cy[4][4] )
{
	tie_point_2_list *tpl = NULL ;
	int i, dx1, dy1, dx2, dy2 ;
	int rc ;

	if(n == 0) return 0 ;
	for(i=0;i<n;i++){
		dx1 = xp[i] ;
		dy1 = yp[i] ;
		dx2 = xq[i] ;
		dy2 = yq[i] ;
		tpl = add_point_to_list(tpl,dx1,dy1,dx2,dy2);
	}
	rc = calcgc(tpl,cx,cy,maxPower,1) ;
	delete_list((list2*)tpl);
	return rc == 0 ? 1 : 0 ;
}

/*
 * Raw data
 */
static int
test1(int *xp,int *yp,int *xq,int *yq)
{
		xp[0]= 7556 ;
		xp[1]= 37662 ;
		xp[2]= 5638 ;
		xp[3]= 37457 ;
	
		xp[4]= 34672 ;
		xp[5]= 13746 ;
		xp[6]= 34853 ;
		xp[7]= 15436 ;
		xp[8]= 23618 ;
	
		yp[0]= 20360 ;
		yp[1]= 24309 ;
		yp[2]= 33250 ;
		yp[3]= 38509 ;
	
		yp[4]= 10115 ;
		yp[5]= 10346 ;
		yp[6]= 37555 ;
		yp[7]= 37913 ;
		yp[8]= 24042 ;
	
		xq[0]= 16886 ;
		xq[1]= -13249;
		xq[2]= 18636 ;
		xq[3]= -13261;
	
		xq[4]= -10038 ;
		xq[5]= 9960 ;
		xq[6]= -11264 ;
		xq[7]= 8687 ;
		xq[8]= 51 ;

		yq[0]= -6188 ;
		yq[1]= -2720 ;
		yq[2]= 6728 ;
		yq[3]= 11486 ;
	
		yq[4]= -16467 ;
		yq[5]= -16175 ;
		yq[6]= 11094 ;
		yq[7]= 11614 ;
		yq[8]= -2460 ;
		return 9;
}

/*
 * Fit 25000,24500 -1,1 0,0
 */

static int
test2(int *xp,int *yp,int *xq,int *yq)
{
		xp[0]= 7556 ;
		xp[1]= 37662 ;
		xp[2]= 5638 ;
		xp[3]= 37457 ;
	
		xp[4]= 34672 ;
		xp[5]= 13746 ;
		xp[6]= 34853 ;
		xp[7]= 15436 ;
		xp[8]= 23618 ;
	
		yp[0]= 20360 ;
		yp[1]= 24309 ;
		yp[2]= 33250 ;
		yp[3]= 38509 ;
	
		yp[4]= 10115 ;
		yp[5]= 10346 ;
		yp[6]= 37555 ;
		yp[7]= 37913 ;
		yp[8]= 24042 ;
	
		xq[0]= 17444 ;
		xq[1]= -12662;
		xq[2]= 19362 ;
		xq[3]= -12457;
	
		xq[4]= -9672 ;
		xq[5]= 11254 ;
		xq[6]= -9853 ;
		xq[7]= 9564 ;
		xq[8]= 1382 ;

		yq[0]= -4140 ;
		yq[1]= -191 ;
		yq[2]= 8750 ;
		yq[3]= 14009 ;
	
		yq[4]= -14385 ;
		yq[5]= -14154 ;
		yq[6]= 13055 ;
		yq[7]= 13413 ;
		yq[8]= -458 ;
		return 9;
}

/*
 * Fit 25000,24500 -1,1 -0.0015,0.002
 */

static int
test3(int *xp,int *yp,int *xq,int *yq)
{
		xp[0]= 7556 ;
		xp[1]= 37662 ;
		xp[2]= 5638 ;
		xp[3]= 37457 ;
	
		xp[4]= 34672 ;
		xp[5]= 13746 ;
		xp[6]= 34853 ;
		xp[7]= 15436 ;
		xp[8]= 23618 ;
	
		yp[0]= 20360 ;
		yp[1]= 24309 ;
		yp[2]= 33250 ;
		yp[3]= 38509 ;
	
		yp[4]= 10115 ;
		yp[5]= 10346 ;
		yp[6]= 37555 ;
		yp[7]= 37913 ;
		yp[8]= 24042 ;
	
		xq[0]= 17413 ;
		xq[1]= -12698;
		xq[2]= 19312 ;
		xq[3]= -12514;
	
		xq[4]= -9687 ;
		xq[5]= 11238 ;
		xq[6]= -9909 ;
		xq[7]= 9507 ;
		xq[8]= 1345 ;

		yq[0]= -4124 ;
		yq[1]= -115 ;
		yq[2]= 8761 ;
		yq[3]= 14083 ;
	
		yq[4]= -14315 ;
		yq[5]= -14126 ;
		yq[6]= 13124 ;
		yq[7]= 13443 ;
		yq[8]= -410 ;
		return 9;
}

/*
 * Fit 25000,24500 -1,1 0.015,-0.025
 */
static int
test4(int *xp,int *yp,int *xq,int *yq)
{
		xp[0]= 7556  ; yp[0]= 20360 ; xq[0]= 17749 ; yq[0]= -4328 ;
		xp[1]= 37662 ; yp[1]= 24309 ; xq[1]= -12297; yq[1]= -1132 ;
		xp[2]= 5638  ; yp[2]= 33250 ; xq[2]= 19860 ; yq[2]= 8609 ;
		xp[3]= 37457 ; yp[3]= 38509 ; xq[3]= -11879; yq[3]= 13072 ;
		xp[4]= 34672 ; yp[4]= 10115 ; xq[4]= -9520 ; yq[4]= -15251 ;
		xp[5]= 13746 ; yp[5]= 10346 ; xq[5]= 11409 ; yq[5]= -14497 ;
		xp[6]= 34853 ; yp[6]= 37555 ; xq[6]= -9289 ; yq[6]= 12183 ;
		xp[7]= 15436 ; yp[7]= 37913 ; xq[7]= 10132 ; yq[7]= 13027 ;
		xp[8]= 23618 ; yp[8]= 24042 ; xq[8]= 1742  ; yq[8]= -1048 ;
		return 9;
}

/*
 * Patrick's data
 */
static int
test5(int *xp,int *yp,int *xq,int *yq)
{
		// Position 1
		xp[0]= 34258 ; yp[0]= 10286 ; xq[0]= -10680 ; yq[0]= -16080 ;
		xp[1]= 34258 ; yp[1]= 10286 ; xq[1]= -10686 ; yq[1]= -16068 ;
		xp[2]= 34154 ; yp[2]= 10390 ; xq[2]= -10589 ; yq[2]= -15955 ;
		xp[3]= 34142 ; yp[3]= 10435 ; xq[3]= -10586 ; yq[3]= -15916 ;

		// Position 2
		xp[4]= 25481 ; yp[4]= 7806  ; xq[4]= -1706  ; yq[4]= -18438 ;
		xp[5]= 25064 ; yp[5]= 10904 ; xq[5]= -1508  ; yq[5]= -15349 ;
		xp[6]= 25064 ; yp[6]= 10833 ; xq[6]= -1511  ; yq[6]= -15408 ;
		xp[7]= 24702 ; yp[7]= 9768  ; xq[7]= -1084  ; yq[7]= -16479 ;

		// Position 3
		xp[8]= 12024 ; yp[8]= 10178  ; xq[8]= 11503  ; yq[8]= -15970 ;

		// Position 4
		xp[9] = 40337 ; yp[9] = 17991 ; xq[9] = -17270  ; yq[9] = -8350 ;
		xp[10]= 40325 ; yp[10]= 18218 ; xq[10]= -17252  ; yq[10]= -8122 ;
		xp[11]= 40669 ; yp[11]= 16765 ; xq[11]= -17515  ; yq[11]= -9587 ;

		// Position 5
		xp[12]= 28301 ; yp[12]= 16590 ; xq[12]= -5154  ; yq[12]= -9661 ;

		// Position 6
		xp[13]= 19591 ; yp[13]= 16000 ; xq[13]= 3580  ; yq[13]= -10154 ;
		xp[14]= 19464 ; yp[14]= 16314 ; xq[14]= 3671  ; yq[14]= -9861 ;
		// Position 6
		
		return 15;
}

main(int argc, char **argv)
{
	char instr[80], *xs, *ys ;
	int i, j, reading = 1 ;
	int ax=-1, bx=0, ay=1, by = 0 ;
	int dx1, dy1, dx2, dy2 ;
	int maxPower = 1 ;
	int computed = 0 ;
	double atof();

	double cx[4][4], cy[4][4] ;
	int n = 0, xp[100], yp[100], xq[100], yq[100] ;

	if(argc > 1 && !strcmp(argv[1],"-test")) n = test1(xp,yp,xq,yq);

	while(reading){
		printf("> ");
		fgets(instr,79,stdin);
		switch(instr[0]){
			default: helpmsg(); break ;
			case 'q': reading = 0 ; break ;
			case '1': n = test1(xp,yp,xq,yq); break ;
			case '2': n = test2(xp,yp,xq,yq); break ;
			case '3': n = test3(xp,yp,xq,yq); break ;
			case '4': n = test4(xp,yp,xq,yq); break ;
			case '5': n = test5(xp,yp,xq,yq); break ;
			case 'f':
				printf("Enter cx[0][0], cy[0][0] terms [%g,%g]>",
					cx[0][0],cy[0][0]);
				fgets(instr,79,stdin);
				xs = strtok(instr," ,\t\n");
				ys = strtok(NULL," ,\t\n");
				if(xs && ys){
					cx[0][0] = atof(xs) ;
					cy[0][0] = atof(ys) ;
				}

				printf("Enter cx[0][1], cy[1][0] terms [%g,%g]>",
					cx[0][1],cy[1][0]);
				fgets(instr,79,stdin);
				xs = strtok(instr," ,\t\n");
				ys = strtok(NULL," ,\t\n");
				if(xs && ys){
					cx[0][1] = atof(xs) ;
					cy[1][0] = atof(ys) ;
				}
				printf("Enter cx[1][0], cy[0][1] terms [%g,%g]>",
					cx[1][0],cy[0][1]);
				fgets(instr,79,stdin);
				xs = strtok(instr," ,\t\n");
				ys = strtok(NULL," ,\t\n");
				if(xs && ys){
					cx[1][0] = atof(xs) ;
					cy[0][1] = atof(ys) ;
				}

			break ;
			case 't':
				if(n < 3){
					fprintf(stderr,"Enter data first and press 'c'\n");
					break ;
				}
				if(!computed) computed = compute_transform(
					n,xp,yp,xq,yq,maxPower,cx,cy);
				if(computed){
					printf("Enter Nikon X,Y> ");
					fgets(instr,79,stdin);
					xs = strtok(instr," ,\t\n");
					ys = strtok(NULL," ,\t\n");
					if(xs && ys){
						dx1 = atoi(xs) ;
						dy1 = atoi(ys) ;
					}else break ;
					dx2 = (int)(cx[0][0] 
						+ cx[0][1] * dx1 
						+ cx[1][0] * dy1
						) ;
					dy2 = (int)(cy[0][0] 
						+ cy[0][1] * dy1 
						+ cy[1][0] * dy1
						) ;

					printf("Nikon %d,%d - Expected MIMS %d,%d\n",
						dx1,dy1,dx2,dy2);
				}
			break ;

			case 'e':
				if(n > 0 && computed){
					double sumx1=0.0, sumx2=0.0, sumy1=0.0, sumy2=0.0;
					double d1, d2, sumd1 = 0.0, sumd2 = 0.0 ;

					printf("Point Nikon\t\tMims\t\t1st Order\t2nd Order\tErr1\tErr2\n");
					for(i=0;i<n;i++){

						dx1 = cx[0][0] 
							+ cx[0][1] * xp[i]
							+ cx[1][0] * yp[i] ;
						dy1 = cy[0][0] 
							+ cy[0][1] * xp[i]
							+ cy[1][0] * yp[i]  ;
						dx2 = cx[0][0] 
							+ cx[0][1] * xp[i]
							+ cx[0][2] * xp[i] * xp[i]
							+ cx[1][0] * yp[i]
							+ cx[2][0] * yp[i] * yp[i]
							+ cx[1][1] * xp[i] * yp[i] ;

						dy2 = cy[0][0] 
							+ cy[0][1] * xp[i]
							+ cy[0][2] * xp[i] * xp[i]
							+ cy[1][0] * yp[i] 
							+ cy[2][0] * yp[i] * yp[i]
							+ cy[1][1] * xp[i] * yp[i] ;

						d1 = hypot((double)(dx1-xq[i]),(double)(dy1-yq[i]));
						d2 = hypot((double)(dx2-xq[i]),(double)(dy2-yq[i]));
						sumd1 += (d1*d1) ;
						sumd2 += (d2*d2) ;

						printf("[%2d] %5d,%5d\t%5d,%5d\t%5d,%5d\t%5d,%5d\t%6.1f\t%6.1f\n",
							   i+1, xp[i],yp[i], xq[i],yq[i],
							   dx1,dy1, dx2,dy2,d1,d2);
						sumx1 += (dx1-xq[i]) * (dx1-xq[i]);
						sumx2 += (dx2-xq[i]) * (dx2-xq[i]);
						sumy1 += (dy1-yq[i]) * (dy1-yq[i]);
						sumy2 += (dy2-yq[i]) * (dy2-yq[i]);
					}
					sumx1 = sqrt(sumx1/(double)(n-1));
					sumx2 = sqrt(sumx2/(double)(n-1));
					sumy1 = sqrt(sumy1/(double)(n-1));
					sumy2 = sqrt(sumy2/(double)(n-1));
					sumd1 = sqrt(sumd1/(double)(n-1));
					sumd2 = sqrt(sumd2/(double)(n-1));

					printf("RMS 1st %g,%g  2nd %g,%g - Err1 %g Err2 %g\n",
						sumx1,sumy1, sumx2, sumy2, sumd1, sumd2);
				}
			break ;
			case 'a':
				computed = 0 ;
				printf("Enter Nikon X,Y> ");
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
				xp[n] = dx1 ;
				yp[n] = dy1 ;
				xq[n] = dx2 ;
				yq[n] = dy2 ;
				n++ ;
				printf("Point[%d] Stage %d,%d Mims %d,%d\n",
					n, dx1,dy1, dx2,dy2);
			break ;

			case 'r':
				printf("Enter a filename to read the coefficients > ");

				fgets(instr,79,stdin);

				if(strlen(instr) > 2) {
					instr[strlen(instr)-1] = '\0';

					if ( readcoeff(instr,cx,cy) ){
						fprintf(stderr,"Reading %s Failed\n", instr);

					}else{

						printf(" %s \n", instr );
						printf("cx(0,0) %g\n", cx[0][0]);
						printf("cx(0,1) %g\n", cx[0][1]);
						printf("cx(0,2) %g\n", cx[0][2]);
						printf("cx(1,0) %g\n", cx[1][0]);
						printf("cx(2,0) %g\n", cx[2][0]);
						printf("cy(0,0) %g\n", cy[0][0]);
						printf("cy(0,1) %g\n", cy[0][1]);
						printf("cy(0,2) %g\n", cy[0][2]);
						printf("cy(1,0) %g\n", cy[1][0]);
						printf("cy(2,0) %g\n", cy[2][0]);
						printf("\n");
						computed = 1 ;
					}
				}
			break;

			case 'w':
				if(!computed) computed = compute_transform(
					n,xp,yp,xq,yq,maxPower,cx,cy);
				if(!computed){
					fprintf(stderr,"No Data to write!\n");
					break ;
				}
				printf("Enter a filename to save the coefficients > ");
				fgets(instr,79,stdin);
				if(strlen(instr) > 2){
					instr[strlen(instr)-1] = '\0';
					if( writecoeff(instr,cx,cy) ) {
						fprintf(stderr,"Writing %s Failed\n", instr);
						
					}else{
						printf(" %s \n", instr );
						printf("cx(0,0) %g\n", cx[0][0]);
						printf("cx(0,1) %g\n", cx[0][1]);
						printf("cx(0,2) %g\n", cx[0][2]);
						printf("cx(1,0) %g\n", cx[1][0]);
						printf("cx(2,0) %g\n", cx[2][0]);
						printf("cy(0,0) %g\n", cy[0][0]);
						printf("cy(0,1) %g\n", cy[0][1]);
						printf("cy(0,2) %g\n", cy[0][2]);
						printf("cy(1,0) %g\n", cy[1][0]);
						printf("cy(2,0) %g\n", cy[2][0]);
						printf("\n");
					}
				}
			break ;
				
			case 'l':
				for(i=0;i<n;i++){
					printf("Point[%d] Stage %d,%d Mims %d,%d\n",
						i+1, xp[i],yp[i], xq[i],yq[i]);
				}
			break ;

			case 'd':
				if(n == 0) break ;
				for(i=0;i<n;i++){
					printf("Point[%d] Stage %d,%d Mims %d,%d\n",
					i+1, xp[i],yp[i], xq[i],yq[i]);
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
							xp[j-1] = xp[j];
							yp[j-1] = yp[j];
							xq[j-1] = xq[j];
							yq[j-1] = yq[j];
						}
						n-- ;
					}
					for(i=0;i<n;i++){
						printf("Point[%d] Stage %d,%d Mims %d,%d\n",
						i+1, xp[i],yp[i], xq[i],yq[i]);
					}
				}
			break ;
			case 'p':
				printf("Enter max Power of coefficients > ");
				fgets(instr,79,stdin);
				if(strlen(instr) > 1) {
					maxPower = atoi(instr);
					if(maxPower < 0) maxPower = 1 ;
					if(maxPower > 3) maxPower = 3 ;
					printf("max Power set to %d\n", maxPower);
				}
			break ;
			case 'c':
			{
				if(n < 3) {
					fprintf(stderr,"I need at least 3 points!\n");
					break ;
				}

				computed = compute_transform(
					n,  xp, yp, xq, yq, 
					maxPower, cx, cy ) ;

				if(computed) {
					printf("\n");
					printf("cx(0,0) %g\n", cx[0][0]);
					printf("cx(0,1) %g\n", cx[0][1]);
					printf("cx(0,2) %g\n", cx[0][2]);
					printf("cx(1,0) %g\n", cx[1][0]);
					printf("cx(2,0) %g\n", cx[2][0]);
					printf("cy(0,0) %g\n", cy[0][0]);
					printf("cy(0,1) %g\n", cy[0][1]);
					printf("cy(0,2) %g\n", cy[0][2]);
					printf("cy(1,0) %g\n", cy[1][0]);
					printf("cy(2,0) %g\n", cy[2][0]);
					printf("\n");
				} else fprintf(stderr,"Computing tranforms Failed\n");

			}
			break ;

		}
	}
	return 0 ;
}
