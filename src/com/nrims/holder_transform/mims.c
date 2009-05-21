
/* 
 * Functions to read th Cameca data/holder/7pp_ref.dat files
 * which contain 'co-ordinates' for X,Y locations.
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

#define return_error(rc)	return errmsg(__FUNCTION__,__LINE__,rc);
#define return_null_err(type,msg)	\
	return (type) errnull(__FUNCTION__,__LINE__,msg);

double atof( const char *) ;

static void *
errnull( char *fn, int line, char *msg )
{
	fprintf(stderr,"Error [%s:%s (%d)] %s\n",__FILE__,fn,line,msg);
	return NULL ;
}

static int
errmsg( char *fn, int line, int errcode )
{
	fprintf(stderr,"Error [%s:%s (%d)] %d\n",__FILE__,fn,line,errcode);
	return errcode ;
}

static int
check_swap()
{
	static int ok = FALSE ;
	static int needs_swap = FALSE ;
	if(!ok){
		ok = TRUE ;
		if(0x1234 != htonl(0x1234)) needs_swap = TRUE ;
		if(verbose) fprintf(stderr,"Swapping %s\n",
			needs_swap ? "Enabled" : "Disabled");
	}
	return needs_swap ;
}

static void
swap_int( int *data )
{
	int old = *data ;
	*data =  htonl(old) ;
}

static void
swap_double( double *data )
{
    union { u_char b[8] ; double d ; } bd, db ;
    db.d = *data ;
    bd.b[7] = db.b[0] ;
    bd.b[6] = db.b[1] ;
    bd.b[5] = db.b[2] ;
    bd.b[4] = db.b[3] ;
    bd.b[3] = db.b[4] ;
    bd.b[2] = db.b[5] ;
    bd.b[1] = db.b[6] ;
    bd.b[0] = db.b[7] ;
    *data = bd.d ;
}

static int
read_data ( char *name, char *data, int size, int offset)
{
	int fd, rc ;
	if(!name) return_error(1);
	if(!data) return_error(2);


	if(( fd = open(name,O_RDONLY) ) < 0 ) return_error(3) ;
	if(offset) lseek(fd, offset, SEEK_SET);
	rc = read(fd, (void *)data, size);

	if(verbose) fprintf(stderr,
		"read_data: read %s - offset %d - bytes %d of %d\n",
		name, offset, rc, size);

	close(fd);


	if( rc <= 0 ) return_error(rc) ;

	if(check_swap()){
		switch(size){
		case 2:
			if(rc == 2){
				unsigned short sp,  *sdata = (unsigned short *)data ;
				sp = htons( *sdata ) ;
				*sdata = sp ;
			}
			break ;
		case 4:
			if(rc == 4) { 	
				unsigned long lp,  *ldata = (unsigned long *)data ;
				lp = htonl( *ldata ) ;
				*ldata = lp ;
			}
			break ;
		}
	}

	return 0;
}

static int
lit_taille( char *fname, int *enr, int *cli )
{
	int rc = read_data(fname, (char *)enr, sizeof(int), 0) ;
	if(rc) return_error(rc) ;
	rc = read_data(fname, (char *)cli, sizeof(int), sizeof(int) ) ;
	if(rc) return_error(rc) ;
	if(verbose)
		fprintf(stderr,"lit_taille(%s, %d, %d)\n", fname, *enr, *cli);
	return 0 ;
}

static int
calc_offset_entete( int enr, int cli )
{
	return 2 * sizeof(int) + cli ;
}

static int
calc_offset_enr( int enr, int cli, int taille_enr, int no_enr)
{
	return 2 * sizeof(int) + enr + cli + taille_enr * no_enr ;
}

static int
lit_entete_enr( char *fname,
				struct_entete_enr *entete_enr,
				int taille,
				int offset )
{
	int rc = read_data(fname, (char *)entete_enr, 
				taille,	/* sizeof(struct_entete_enr) */
				offset) ;
	if(rc) return_error(rc) ;

	if(check_swap()){
		int i ;
		swap_int(&entete_enr->nb_max_enr);
		swap_int(&entete_enr->nb_enr);
		swap_int(&entete_enr->taille_enr);
		for(i=0;i<SEQ_MAX_ENR;i++){
			swap_int(&entete_enr->tab_enr[i]);
			swap_int(&entete_enr->tab_trou[i]);
		}
	}
	return 0 ;
}

static int
lire_tot (  char *fname,
			int *max_enr,
			int *enr,
			int *taille_enr,
			int *taille_entete_cli )
{
	int offset_entete_enr ;
	int	offset_enr ;
	int taille_entete_enr ;
	struct_entete_enr entete_enr ;

	int rc ;
	memset(&entete_enr,0,sizeof(struct_entete_enr));

	rc = lit_taille( fname, &taille_entete_enr, taille_entete_cli );
	if(rc) return_error(rc) ;

	offset_entete_enr =
		calc_offset_entete(taille_entete_enr,*taille_entete_cli);

	rc = lit_entete_enr(fname, &entete_enr,taille_entete_enr,offset_entete_enr);
	if(rc) return_error(rc) ;

	*max_enr = entete_enr.nb_max_enr ;
	*enr = entete_enr.nb_enr ;
	*taille_enr = entete_enr.taille_enr ;

	if(verbose)
		fprintf(stderr,"lire_tot(%s, %d, %d, %d, %d)\n",
			fname,
			*max_enr,
			*enr,
			*taille_enr,
			*taille_entete_cli);
	return 0 ;
}

static int
ibd_enr_reflist_lire( char *fname,
		int entry,
		char *data,
		int size )
{
	int offset1, offset2, rc, size1 ;
	int taille_entete_enr, taille_entete_cli;
	static int first_time = TRUE ;

	struct_entete_enr entete_enr ;
	memset(&entete_enr,0,sizeof(struct_entete_enr));

	rc = lit_taille( fname, &taille_entete_enr, &taille_entete_cli);
	if(rc) return_error(rc) ;

	offset1 = calc_offset_entete(taille_entete_enr,taille_entete_cli);
	rc = lit_entete_enr( fname, &entete_enr, taille_entete_enr, offset1);
	if(rc) return_error(rc) ;

	if(entry < 1)  return_error(2);

	if(verbose && first_time ){
		int i ;
		fprintf(stderr,"entete_enr.nb_max_enr = %d\n", entete_enr.nb_max_enr);
		fprintf(stderr,"entete_enr.nb_enr = %d\n", entete_enr.nb_enr);
		fprintf(stderr,"entete_enr.taille_enr = %d\n", entete_enr.taille_enr);
		first_time = FALSE ;
		for(i=0;i<entete_enr.nb_max_enr;i++){
			fprintf(stderr,"entete_enr.tab_enr[%d] = %d"
				" entete_enr.tab_trou[%d] = %d\n",
				i, entete_enr.tab_enr[i],
				i, entete_enr.tab_trou[i]);
		}
	}

	if(size > entete_enr.taille_enr) size1 = entete_enr.taille_enr;
	else size1 = size ;

	if(entry > entete_enr.nb_enr) {
		int i = entry-entete_enr.nb_enr -1 ;
		offset2 = calc_offset_enr(taille_entete_enr,
							 taille_entete_cli,
							 entete_enr.taille_enr,
							 entete_enr.tab_trou[i]);
		if(verbose) fprintf(stderr,
			"read entry %d tab_trou %d offset %d\n", entry-1,
			entete_enr.tab_trou[i], offset2);
	}else{
		offset2 = calc_offset_enr(taille_entete_enr,
							 taille_entete_cli,
							 entete_enr.taille_enr,
							 entete_enr.tab_enr[entry-1]);
		if(verbose) fprintf(stderr,
			"read entry %d tab_enr %d offset %d\n", entry-1,
			entete_enr.tab_enr[entry-1], offset2);
	}

	rc = read_data( fname, data, size1, offset2 );

	if(rc) return_error(rc);


	if(verbose){
		fprintf(stderr,
			"ibd_enr_reflist_lire(%s,%d,..,%d)\n",fname,entry,size);
		fprintf(stderr,
			"read size %d size1 %d offset2 %d\n", size, size1, offset2);
		fprintf(stderr,
			"taille_enr %d tab_enr[%d] %d\n",
			entete_enr.taille_enr, entry-1, entete_enr.tab_enr[entry-1]);
	}

	return 0 ;
}


static void
swap_ibd_ref( ibd_ref * ref )
{
	int i ;
	if(ref){
		swap_double(&ref->ibd_ref_posit.x);
		swap_double(&ref->ibd_ref_posit.y);
		swap_int(&ref->ibd_ref_lien_nb);
		for(i=0;i<IBD_REF_MAX_LIEN;i++)
			swap_int(&ref->ibd_ref_lien[i]);
	}
}

static ibd_stru_reflist *
read_entry ( char *fname,
			 int entry, 
			 ibd_stru_reflist *rentry )
{
	int rc, nb_max, nb_enr, taille_enr, taille_cli, free_it = FALSE ;
	ibd_stru_enr_reflist ibd_enr_reflist ;
	int read_trou = FALSE ;

	if(NULL == rentry) {
		rentry = 
			 (ibd_stru_reflist *)calloc(1, sizeof(ibd_stru_reflist));
		free_it = TRUE ;
	}else{
		memset(&ibd_enr_reflist,0,sizeof(ibd_stru_enr_reflist));
	}

	rc = lire_tot( fname, &nb_max, &nb_enr, &taille_enr, &taille_cli);
	if(rc){
		if(free_it) free(rentry);
		return_null_err(ibd_stru_reflist *, "lire_tot") ;
	}

	if(entry < 0 || entry > nb_max-1){
		if(free_it) free(rentry);
		return_null_err(ibd_stru_reflist *, "out of range") ;
	}

	rc = ibd_enr_reflist_lire( fname,
		entry+1,
		(char *)&ibd_enr_reflist,
		sizeof(ibd_stru_enr_reflist));

	if(rc) {
		if(free_it) free(rentry);
		return_null_err(ibd_stru_reflist *, "reading reflist") ;
	}

	if(check_swap()){
		int i ;
		swap_int(&ibd_enr_reflist.ibd_fil_reflist.ibd_nb_ref);
		for(i=0;i<IBD_MAX_REF;i++)
			swap_ibd_ref(&ibd_enr_reflist.ibd_fil_reflist.ibd_reflist[i]);
	}

	memcpy(rentry,&ibd_enr_reflist.ibd_fil_reflist,sizeof(ibd_stru_reflist));

	return rentry ;
}

static void *
read_header( char *fname )
{
	void *header ;
	int rc, enr, cli, sz, *hptr ;

	rc = lit_taille( fname, &enr, &cli ) ;
	if(rc) return_null_err( void *, "lit_taille");

	sz = 2*sizeof(int) + cli ;

	header = (void *)calloc(1, sz );

	if(!header) {
		fprintf(stderr,"Error [%s:%s (%d)] %s\n",
			__FILE__, __FUNCTION__,__LINE__,"calloc failed!");
		return NULL ;
	}

	rc = read_data(fname, (char *)header, sz, 0);

	if(rc) {
		fprintf(stderr,"Error [%s:%s (%d)] %s\n",
			__FILE__, __FUNCTION__,__LINE__,"read failed!");
		free(header);
		return NULL ;
	}

	if(check_swap()){
		int *hptr = (int *)header ;
		swap_int(&hptr[0]) ;
		swap_int(&hptr[1]) ;
	}

	return header ;
}

static int 
write_data_file( char *fname,
			void *header,
			struct_entete_enr *entete_enr,
			ibd_stru_reflist **rentry ,
			int n_rentries )
{ 
	int i, j, k, rc, fd, *hptr, entete_enr_size, header_size ;
	char newname[MAXPATHLEN] ;
	int index = 1, total = 0 ;
	ibd_stru_reflist *rp ;
	ibd_stru_enr_reflist ibd_enr_reflist ;

	/*
	 * If it exist, save a backup..
	 */
	if(backup && !access(fname,R_OK)){
		sprintf(newname,"%s.%d",fname,index) ;
		while(!access(newname,R_OK))
			sprintf(newname,"%s.%d",fname,++index) ;
		if(rename(fname,newname)) {
			fprintf(stderr,"renaming %s->%s:", fname,newname);
			perror(":"); 
		}
	}

	fd = open(fname, O_RDWR|O_CREAT, 438 ) ;

	if(fd < 0){
		fprintf(stderr,"Error writing %s:", fname);
		perror(":"); 
		return -1 ;
	}

	hptr = (int *)header ;
	entete_enr_size = hptr[0] ;
	header_size = hptr[1] + 2 * sizeof(int) ;

	if(check_swap()) {
		swap_int(&hptr[0]);
		swap_int(&hptr[1]);
	}

	rc = write(fd,header,header_size);
	total += rc ;

	if(check_swap()) {
		swap_int(&hptr[0]);
		swap_int(&hptr[1]);
	}

	if(rc != header_size){
		fprintf(stderr,"Error writing %s:", fname);
		perror(":"); 
		return -1 ;
	}

	if(entete_enr->nb_max_enr != n_rentries)
		entete_enr->nb_max_enr = n_rentries ;

	if(check_swap()) {
		swap_int(&entete_enr->nb_max_enr);
		swap_int(&entete_enr->nb_enr);
		swap_int(&entete_enr->taille_enr);
		for(i=0;i<SEQ_MAX_ENR;i++){
			swap_int(&entete_enr->tab_enr[i]);
			swap_int(&entete_enr->tab_trou[i]);
		}
	}

	rc = write(fd, entete_enr, entete_enr_size);
	total += rc ;

	if(check_swap()) {
		swap_int(&entete_enr->nb_max_enr);
		swap_int(&entete_enr->nb_enr);
		swap_int(&entete_enr->taille_enr);
		for(i=0;i<SEQ_MAX_ENR;i++){
			swap_int(&entete_enr->tab_enr[i]);
			swap_int(&entete_enr->tab_trou[i]);
		}
	}

	if(rc != entete_enr_size){
		fprintf(stderr,"Error writing %s (2)", fname);
		perror(":"); 
		return -1 ;
	}

	rp = rentry[0] ;

	for(i=0;i<n_rentries;i++){

		int offset ; 

		/* Here's the tricky part..
		 * write the data back in the order 
		 * found in entete_enr->tab_enr[i]..
		 */


		if(i >= entete_enr->nb_enr) {
		  for(rp = NULL,k=0; rp == NULL && k<n_rentries; k++){
			offset = entete_enr_size + header_size +
				sizeof(ibd_stru_enr_reflist) * entete_enr->tab_trou[k] ;
			if(total == offset) rp = rentry[k] ;
		  }
		}else{
		  for(rp = NULL,k=0; rp == NULL && k<n_rentries; k++){
			offset = entete_enr_size + header_size +
				sizeof(ibd_stru_enr_reflist) * entete_enr->tab_enr[k] ;
			if(total == offset) rp = rentry[k] ;
		  }
		}

		if(!rp) {
			fprintf(stderr,"Error: %s missing entry %d\n", fname, i);
			exit(1);
		}

		if(check_swap()){
			swap_int(&rp->ibd_nb_ref);
			for(j=0;j<IBD_MAX_REF;j++)
				swap_ibd_ref(&rp->ibd_reflist[j]);
		}

		memset(&ibd_enr_reflist, 0, sizeof(ibd_stru_enr_reflist)) ;
		memcpy( &ibd_enr_reflist.ibd_fil_reflist, rp, sizeof(ibd_stru_reflist));
		
		rc = write(fd, &ibd_enr_reflist, sizeof(ibd_stru_enr_reflist));

		total += rc ;

		if(check_swap()){
			swap_int(&rp->ibd_nb_ref);
			for(j=0;j<IBD_MAX_REF;j++)
				swap_ibd_ref(&rp->ibd_reflist[j]);
		}

		if(rc != sizeof(ibd_stru_enr_reflist)){
			fprintf(stderr,"Error writing %s (3)", fname);
			perror(":"); 
			return -1 ;
		}
	}

	return 0 ;
}

static struct_entete_enr	*
read_struc_entete( char *fname )
{
	struct_entete_enr *entete_enr ;
	int rc, enr, cli, sz, offset ;

	rc = lit_taille( fname, &enr, &cli ) ;
	if(rc) return_null_err(struct_entete_enr *, "lit_taille");

	offset = 2*sizeof(int) + cli ;

	sz = enr ;

	if(sz != sizeof(struct_entete_enr) ){
		fprintf(stderr,"Error [%s:%s (%d)] %s\n",
			__FILE__, __FUNCTION__,__LINE__,"struct_entete_enr size?!");
		return NULL ;
	}

	entete_enr = (struct_entete_enr *)calloc(1, sz) ;

	if(!entete_enr) {
		fprintf(stderr,"Error [%s:%s (%d)] %s\n",
			__FILE__, __FUNCTION__,__LINE__,"calloc failed!");
		return NULL ;
	}

	rc = read_data(fname, (char *)entete_enr, sz, offset);

	if(rc) {
		fprintf(stderr,"Error [%s:%s (%d)] %s\n",
			__FILE__, __FUNCTION__,__LINE__,"read failed!");
		free(entete_enr);
		return NULL ;
	}

	if(check_swap()) {
		int i ;
		swap_int(&entete_enr->nb_max_enr);
		swap_int(&entete_enr->nb_enr);
		swap_int(&entete_enr->taille_enr);
		for(i=0;i<SEQ_MAX_ENR;i++){
			swap_int(&entete_enr->tab_enr[i]);
			swap_int(&entete_enr->tab_trou[i]);
		}
	}

	return entete_enr ;
}


int
edit_data( ibd_stru_reflist *rp , int entry, char *data )
{
	if(!rp) return -1 ;
	if(entry >= IBD_MAX_REF || entry < 0) return -1 ;
	memset(rp->ibd_reflist[entry].ibd_ref_dat,0,IBD_TAILLE_DAT);
	if(data) strncpy( rp->ibd_reflist[entry].ibd_ref_dat, data, IBD_TAILLE_DAT-1);

	return 0 ;
}

int
edit_comment( ibd_stru_reflist *rp , int entry, char *comment )
{
	if(!rp) return -1 ;
	if(entry >= IBD_MAX_REF || entry < 0) return -1 ;

	memset(rp->ibd_reflist[entry].ibd_ref_com,0,IBD_TAILLE_COM);
	if(comment)
		strncpy( rp->ibd_reflist[entry].ibd_ref_com, comment, IBD_TAILLE_COM-1);

	return 0 ;
}

int
edit_xy_entry ( ibd_stru_reflist *rp, int entry, double x, double y)
{
	if(!rp) return -1 ;
	if(entry >= IBD_MAX_REF || entry < 0) return -1 ;

	rp->ibd_reflist[entry].ibd_ref_posit.x = x ;
	rp->ibd_reflist[entry].ibd_ref_posit.y = y ;

	return 0 ;
}

int
delete_entry( ibd_stru_reflist *rp, int entry ) 
{
	int i, total ;
	if(!rp) return -1 ;
	if(entry >= IBD_MAX_REF || entry < 0) return -1 ;

	total = rp->ibd_nb_ref ;
	if(rp->ibd_nb_ref > 1) rp->ibd_nb_ref-- ;
	else {
		rp->ibd_nb_ref = 0 ;
		return 0 ;
	}

	for(i=entry;i<total;i++)
		memcpy((void *)&rp->ibd_reflist[i],		/* to.. */
			   (void *)&rp->ibd_reflist[i+1], 	/* from.. */
			   sizeof(ibd_ref));

	return 0 ;
}

int
append_entry( ibd_stru_reflist *rp, double x, double y, char *com, char *dat ) 
{
	int entry ;
	if(!rp) return -1 ;
	entry = rp->ibd_nb_ref ;
	if(entry >= IBD_MAX_REF) return -1 ;
	++rp->ibd_nb_ref ;
	edit_xy_entry(rp, entry, x, y);
	edit_comment(rp, entry, com);
	edit_data(rp, entry, dat);
	return 0 ;
}

int
insert_entry( ibd_stru_reflist *rp,
			  int entry,
			  double x, double y,
			  char *com, char *dat ) 
{
	int i, total ;
	if(entry >= IBD_MAX_REF || entry < 0) return -1 ;
	if(!rp) return -1 ;
	total = rp->ibd_nb_ref ;
	if(total < IBD_MAX_REF ) ++rp->ibd_nb_ref ;

	for(i=total-1; i >= entry ; i--)
		memcpy((void *)&rp->ibd_reflist[i+1],	/* to.. */
			   (void *)&rp->ibd_reflist[i],		/* from.. */
			   sizeof(ibd_ref));

	edit_xy_entry(rp, entry, x, y);
	edit_comment(rp, entry, com);
	edit_data(rp, entry, dat);

	return 0 ;
}

int
print_point( ibd_stru_reflist *rp , int entry )
{
	int i = entry - 1 ;

	i = max(0, i) ;
	i = min(i, rp->ibd_nb_ref-1) ;

	fprintf(stdout,"%3d: Elem: %-20s Date: %-15s X=%6g um, Y=%6g um\n",
			i+1,
			rp->ibd_reflist[i].ibd_ref_com,
			rp->ibd_reflist[i].ibd_ref_dat,
			1000*rp->ibd_reflist[i].ibd_ref_posit.x,
			1000*rp->ibd_reflist[i].ibd_ref_posit.y);
}


static int
sort_date( ibd_ref *sort1, ibd_ref *sort2 )
{
	return strcmp(sort1->ibd_ref_dat, sort2->ibd_ref_dat);
}

static int
sort_rev_date( ibd_ref *sort1, ibd_ref *sort2 )
{
	int rc = strcmp(sort1->ibd_ref_dat, sort2->ibd_ref_dat);
	if(rc < 0) return 1 ;
	else if(rc > 0) return -1 ;
	return rc ;
}

static int
sort_name( ibd_ref *sort1, ibd_ref *sort2 )
{
	return strcmp(sort1->ibd_ref_com, sort2->ibd_ref_com);
}

static int
sort_rev_name( ibd_ref *sort1, ibd_ref *sort2 )
{
	int rc =  strcmp(sort1->ibd_ref_com, sort2->ibd_ref_com);
	if(rc < 0) return 1 ;
	else if(rc > 0) return -1 ;
	return rc ;
}

int
read_stage_list( char *stgFile,
		 double **stgX,
		 double **stgY,
		 char ***stgComment ) 
{
	double *sx = NULL, *sy = NULL ;
	FILE *file ;
	char rstr[80], *ax, *ay, *az, **cstr = NULL, *comment ;
	int allocated = 0, npts = 0 ;

	if((file = fopen(stgFile,"r")) == NULL){
		return 0 ;
	}

	fgets(rstr,79,file);	/* STAGE_LIST */
	if(strncmp(rstr,"STAGE_LIST", 10)){
		fprintf(stderr,"%s is not a stage list??\n", stgFile);
		fclose(file);
		return 0 ;
	}

	fgets(rstr,79,file);	/* UNITS_UM */
	if(strncmp(rstr,"UNITS_UM", 8)){
		fprintf(stderr,"%s UNITS_UM error??\n", stgFile);
	}

	while(fgets(rstr,79,file)) {
		ax = (char *)strtok(rstr," \t\n");
		ay = (char *)strtok(NULL," \t\n");
		az = (char *)strtok(NULL," \t\n");
		comment = (char *)strtok(NULL," \t\n");
		if(ax && ay) {
			if(allocated < npts+1) {
				allocated += 1024 ;
				if(sx) sx = (double *)realloc(sx, allocated * sizeof(double)) ;
				else sx = (double *)calloc(allocated, sizeof(double) ) ;
				if(sy) sy = (double *)realloc(sy, allocated * sizeof(double)) ;
				else sy = (double *)calloc(allocated, sizeof(double) ) ;
				if(cstr) cstr = (char **)
					realloc(cstr, allocated * sizeof(char *));
				else cstr = (char **)calloc(allocated, sizeof(char *) ) ;
			}
			if(sx && sy) {
				sx[npts] = rint(atof(ax));
				sy[npts] = rint(atof(ay));
				if(comment && strlen(comment)) cstr[npts] = strdup(comment);
				else cstr[npts] = NULL ;
				npts++ ;
			}
		}
	}

	fclose(file);

	if(npts) {
		if(stgX) *stgX = sx ;
		if(stgY) *stgY = sy ;
		if(stgComment) *stgComment = cstr ;
	}

#if 0
	if(verbose)
#endif
	{
		int i ;
		printf("Stage File %s : %d XY points\n", stgFile,npts);
		for(i=0;i<npts;i++){
			printf("Point %d: X %g Y %g\n", i+1, sx[i], sy[i]);
		}
		fflush(stdout);
	}

	return npts ;

}

int
read_vpts_list( char *stgFile,
		 double **stgX,
		 double **stgY,
		 char ***stgComment ) 
{
	double *sx = NULL, *sy = NULL ;
	FILE *file ;
	char rstr[80], *ax, *ay, *az, **cstr = NULL, *comment ;
	int allocated = 0, npts = 0 ;

	if((file = fopen(stgFile,"r")) == NULL){
		return 0 ;
	}

	fgets(rstr,79,file);	/* STAGE_LIST */
	if(strncmp(rstr,"VPTS_LIST", 9)){
		fprintf(stderr,"%s is not a stage list??\n", stgFile);
		fclose(file);
		return 0 ;
	}

	fgets(rstr,79,file);	/* UNITS_UM */
	if(strncmp(rstr,"UNITS_UM", 8)){
		fprintf(stderr,"%s UNITS_UM error??\n", stgFile);
	}

	while(fgets(rstr,79,file)) {
		ax = (char *)strtok(rstr," \t\n");
		ay = (char *)strtok(NULL," \t\n");
		comment = (char *)strtok(NULL," \t\n");

		if(ax && ay) {
			if(allocated < npts+1) {
				allocated += 1024 ;
				if(sx) sx = (double *)realloc(sx, allocated * sizeof(double)) ;
				else sx = (double *)calloc(allocated, sizeof(double) ) ;
				if(sy) sy = (double *)realloc(sy, allocated * sizeof(double)) ;
				else sy = (double *)calloc(allocated, sizeof(double) ) ;
				if(cstr) cstr = (char**)realloc(cstr,allocated*sizeof(char*));
				else cstr = (char **)calloc(allocated, sizeof(char *) ) ;
			}
			if(sx && sy) {
				sx[npts] = rint(atof(ax));
				sy[npts] = rint(atof(ay));
				if(comment) cstr[npts] = strdup(comment);
				else cstr[npts] = NULL ;
				npts++ ;
			}
		}
	}

	fclose(file);

	if(npts) {
		if(stgX) *stgX = sx ;
		if(stgY) *stgY = sy ;
		if(stgComment) *stgComment = cstr ;
	}

#if 0
	if(verbose)
#endif
	{
		int i ;
		printf("Stage File %s : %d XY points\n", stgFile,npts);
		for(i=0;i<npts;i++){
			printf("Point %d: X %g Y %g\n", i+1, sx[i], sy[i]);
		}
		fflush(stdout);
	}

	return npts ;

}

char *
get_date_str()
{
	time_t tt ;
	struct tm *lt ;
	static char datestr[IBD_TAILLE_DAT+1] ;

	time(&tt);
	lt = localtime(&tt);
	strftime(datestr,IBD_TAILLE_DAT,"%Y/%m/%d %T",lt);

	return datestr ;
}

void
translate_nikon_to_mims(int spts,
	double *stgX, double *stgY,
	double ax, double bx, double ay, double by )
{
	int i ;
	for(i=0;i<spts;i++){
		stgX[i] = stgX[i] * ax + bx ;
		stgY[i] = stgY[i] * ay + by ;
	}
}

void
rotate_nikon_to_mims(int spts,
	double *stgX, double *stgY,
	double cx[4][4], double cy[4][4] )
{
	int i ;
	double sx, sy ;

	for(i=0;i<spts;i++){
		sx = stgX[i] ;
		sy = stgY[i] ;
		stgX[i] = cx[0][0] + cx[0][1] * sx + cx[1][0] * sy ;
		stgY[i] = cy[0][0] + cy[0][1] * sx + cy[1][0] * sy ;
	}
}

void *
open_holder_data( char *fname )
{
	int rc, count;
	char defName[MAXPATHLEN];

	holderDataP hp = (holderDataP)calloc(1, sizeof(holderData) ) ;

	if(hp == NULL) return NULL ;
	hp->id = 0x12fe34dc	;	/* magic test.. */

	if(fname == NULL){
		sprintf(defName,"%s/data/holder/7pp_ref.dat", getenv("HOME"));
		fname = defName ;
	}

	if(access(fname,R_OK)){
		fprintf(stderr,"Error: opening %s\n", fname);
		return NULL ;
	}

	hp->fname = strdup(fname);

	hp->header = read_header(fname) ;
	hp->entete_header = read_struc_entete( fname );

	rc = lire_tot( fname,
		&hp->nb_max, &hp->nb_enr, &hp->taille_enr, &hp->taille_cli);
	if(rc) {
		free(hp);
		return NULL ;
	}

	hp->entries = hp->entete_header->nb_max_enr ;
	hp->rentry = (ibd_stru_reflist **)
		calloc(hp->entries, sizeof(ibd_stru_reflist *));

	for(count = 0 ; count < hp->entries ; count++ )
		hp->rentry[count] = read_entry(fname,count,NULL) ;

	return (void *)hp ;
}

int
save_holder_data( void *data, char *fname )
{
	holderDataP hp = (holderDataP) data ;
	if( hp->id != 0x12fe34dc	) return -1 ;
	if(fname == NULL) fname = hp->fname ;
	return write_data_file(fname,
		hp->header, hp->entete_header, hp->rentry, hp->entries) ;
}

void
free_holder_data( void *data )
{
	int count ;
	holderDataP hp = (holderDataP) data ;

	if( (hp != NULL) && (hp->id == 0x12fe34dc) ){
		for(count = 0 ; count < hp->entries ; count++ )
			if(hp->rentry[count]) free(hp->rentry[count]) ;
		if(hp->rentry) free(hp->rentry);
		if(hp->entete_header) free(hp->entete_header);
		if(hp->header) free(hp->header);
		if(hp->fname) free(hp->fname);
		hp->id = 0 ;
		free(hp);
	}
}


int
holder_data_get_x( void *data, int entry )
{
	int xv = 0, i = entry - 1 ;
	holderDataP hp = (holderDataP) data ;
	ibd_stru_reflist *rp ;

	if( (hp != NULL) && (hp->id == 0x12fe34dc) ){
		i = max(0, i) ;
		i = min(i, rp->ibd_nb_ref-1) ;
		rp = hp->rentry[0];
		xv = 1000*rp->ibd_reflist[i].ibd_ref_posit.x ;
	}
	return xv ;
}

int
holder_data_get_y( void *data, int entry )
{
	int yv = 0, i = entry - 1 ;
	holderDataP hp = (holderDataP) data ;
	ibd_stru_reflist *rp ;

	if( (hp != NULL) && (hp->id == 0x12fe34dc) ){
		i = max(0, i) ;
		i = min(i, rp->ibd_nb_ref-1) ;
		rp = hp->rentry[0];
		yv = 1000*rp->ibd_reflist[i].ibd_ref_posit.y ;
	}
	return yv ;
}

char *
holder_data_get_date( void *data, int entry)
{
	int i = entry - 1 ;
	holderDataP hp = (holderDataP) data ;
	ibd_stru_reflist *rp ;

	if( (hp != NULL) && (hp->id == 0x12fe34dc) ){
		i = max(0, i) ;
		i = min(i, rp->ibd_nb_ref-1) ;
		rp = hp->rentry[0];
		return	rp->ibd_reflist[i].ibd_ref_dat ;
	}
	return NULL ;
}

char *
holder_data_get_comment( void *data, int entry)
{
	int i = entry - 1 ;
	holderDataP hp = (holderDataP) data ;
	ibd_stru_reflist *rp ;

	if( (hp != NULL) && (hp->id == 0x12fe34dc) ){
		i = max(0, i) ;
		i = min(i, rp->ibd_nb_ref-1) ;
		rp = hp->rentry[0];
		return	rp->ibd_reflist[i].ibd_ref_com ;
	}
	return NULL ;
}
