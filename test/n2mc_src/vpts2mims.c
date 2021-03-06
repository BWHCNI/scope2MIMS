
/* 
 * Convert a vpts list file to a series of MIMS co-ordinates
 */

#include <stdio.h>
#include <unistd.h>
#include <math.h>
#include <string.h>
#include <sys/fcntl.h>
#include <netinet/in.h>
#include <time.h>

#include "mims.h"

#ifndef max
#undef min
#define max(a,b)  (((a) > (b)) ? (a) : (b))
#define min(a,b)  (((a) < (b)) ? (a) : (b))
#endif

#ifndef TRUE
#define TRUE 1
#undef 	FALSE
#define FALSE 0
#endif

#ifndef FALSE
#define FALSE 0
#undef TRUE
#define TRUE  1
#endif

#ifndef MAXPATHLEN
#define MAXPATHLEN	4096
#endif

#ifdef DEBUG
static int verbose = 1 ;
#else
static int verbose = 0 ;
#endif

static int backup = 1 ;

main(int argc, char **argv)
{
	int count, oldCount, entry = -1, i, rc = 0, entries = 1 ;
	ibd_stru_reflist *rp ;
	char *fname = NULL;
	char *stageFile ;
	void *header ;
	struct_entete_enr	*entete_header ;
	double *stgX, *stgY ;
	int spts = 0 ;
	char *curDate ;
	double nx, ny ;
	char new_comment[IBD_TAILLE_COM], **stgComments ;
	holderDataP hp ;

	if(argc < 2) {
		fprintf(stderr, "Usage: %s <vptsfile> [-f <holder file>]\n",
			argv[0]);
		exit(1);
	}

	stageFile = argv[1] ;
	curDate = get_date_str();

	for(i=2;i<argc;i++){
		if(!strcmp("-n",argv[i])) entry = atoi(argv[++i]);
		else if(!strcmp("-f",argv[i])) fname = argv[++i];
	}

	if((spts = read_vpts_list(stageFile, &stgX, &stgY, &stgComments)) <= 0){
		fprintf(stderr,"%s: Error opening stage list %s\n",argv[0], argv[1]);
		exit(1);
	}

	hp = (holderDataP) open_holder_data( fname ) ;

	if(hp == NULL){
		fprintf(stderr,"%s: Error opening holder data file\n",argv[0]);
		exit(1);
	}

	rp = hp->rentry[0] ;

	oldCount = rp->ibd_nb_ref ;

	for(count=0; count < spts ; count++ ) {
		if(stgComments[count])
			strncpy(new_comment,
			 stgComments[count], IBD_TAILLE_COM-1);
		else
			sprintf(new_comment,"%s #%d", stageFile, count+1);
		nx = stgX[count] / 1000.0 ;
		ny = stgY[count] / 1000.0 ;
		if( append_entry( rp, nx, ny, new_comment, curDate ) ) {
			fprintf(stderr,"Error appending %d at %g,%g (%s,%s)\n",
				count+1, stgX[count], stgY[count],
				new_comment, curDate);
			exit(1);
		}
	}

	for(count=oldCount;count<=rp->ibd_nb_ref;count++) print_point(rp,count);

	save_holder_data((void *)hp, fname); 

	free_holder_data((void *)hp);

	free(stgX);
	free(stgY);
	free(stgComments);
	
	exit(0);
}
