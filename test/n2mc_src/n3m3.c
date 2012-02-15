/* 
 * Convert a stagelist file to a series of MIMS co-ordinates
 */

#include <stdio.h>
#include <unistd.h>
#include <math.h>
#include <string.h>
#include <sys/fcntl.h>
#include <netinet/in.h>
#include <time.h>

#include "calc.h"
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
	int count, entry = -1, i, rc = 0, entries = 1 ;
	ibd_stru_reflist *rp ;
	char *fname = NULL;
	char *stageFile ;
	void *header ;
	struct_entete_enr	*entete_header ;
	double *stgX, *stgY ;
	int spts = 0 ;
	char *curDate ;
	double dx, dy ;
	char new_comment[IBD_TAILLE_COM], **stgComments ;
	holderDataP hp ;
	int nx[3], ny[3], mx[3], my[3] ;

	if(argc < 3) {
		fprintf(stderr,"Usage: %s <stagefile> <tiepoints file> [-v]\n",
			argv[0]);
		exit(1);
	}

	stageFile = argv[1] ;

	if(read_mims_tiepoints(argv[2],nx,ny,mx,my)) {
		fprintf(stderr,"Error reading tiepoints's from %s\n",argv[2]);
		exit(1);
	}

	curDate = get_date_str();

	for(i=2;i<argc;i++){
		if(!strcmp("-v",argv[i])) verbose = !verbose ;
	}

	if((spts = read_stage_list(stageFile, &stgX, &stgY, &stgComments)) <= 0){
		fprintf(stderr,"%s: Error opening stage list %s\n",argv[0], argv[1]);
		exit(1);
	}

	hp = (holderDataP) open_holder_data( fname ) ;

	if(hp == NULL){
		fprintf(stderr,"%s: Error opening holder data file\n",argv[0]);
		exit(1);
	}

	rp = hp->rentry[0] ;

	if( compute_tr_dpoints( nx, ny, mx, my, stgX, stgY, stgX, stgY, spts) ){
		fprintf(stderr,"%s: Error translating Nikon X,Y stage points\n",argv[0]);
		exit(1);
	}

	for(count=0; count < spts ; count++ ) {
		if(stgComments[count] && strlen(stgComments[count]) )
			strncpy(new_comment, stgComments[count], IBD_TAILLE_COM-1);
		else
			sprintf(new_comment,"%s #%d", stageFile, count+1);
		dx = stgX[count] / 1000.0 ;
		dy = stgY[count] / 1000.0 ;
		if( append_entry( rp, dx, dy, new_comment, curDate ) ) {
			fprintf(stderr,"Error appending %d at %g,%g (%s,%s)\n",
				count+1, stgX[count], stgY[count],
				new_comment, curDate);
			exit(1);
		}
	}

	for(count=0;count<=rp->ibd_nb_ref;count++) print_point(rp,count);

	save_holder_data((void *)hp, fname); 

	free_holder_data((void *)hp);

	free(stgX);
	free(stgY);
	free(stgComments);
	
	exit(0);
}
