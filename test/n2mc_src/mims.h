
#ifndef __MIMS_H_
#define __MIMS_H_

/*
 * Notes..
 * When testing on Linux/i386 systems, structures with
 * doubles are not padded the same way as on sparc systems,
 * yielding different sizes and offsets.  The #ifdef i386
 * are used to force the padding to get these files to
 * read/write the data correctly..
 */

#define IBD_TAILLE_COM          80
#define IBD_TAILLE_DAT          20
#define IBD_REF_MAX_LIEN        200
#define IBD_MAX_REF		        200

typedef struct {
    double x    ;               /* abcisse*/
    double y    ;               /* ordonnee*/
} ibd_point;
 
typedef struct {
// Original 
//    char        ibd_ref_com[IBD_TAILLE_COM];/*commentaire*/
//    char        ibd_ref_dat[IBD_TAILLE_DAT];/*date posit. ref.*/
//#ifdef i386
//	char		dummy_align[4] ;
//#endif
//    ibd_point   ibd_ref_posit;  /* position du manip a la capture.*/
//    char    dummy[10];      	/*effet de bord*/
//    int ibd_ref_lien_nb;        /* nb de liens*/
//    int ibd_ref_lien[IBD_REF_MAX_LIEN];/* no de zone ou est la posit. de ref.*/

    char        ibd_ref_com[IBD_TAILLE_COM];/*commentaire*/
    char        ibd_ref_dat[IBD_TAILLE_DAT];/*date posit. ref.*/
#ifdef i386
	char		dummy_align[4] ;
#endif
    ibd_point   ibd_ref_posit;  /* position du manip a la capture.*/
    double    dZ;      	/*effet de bord*/
    int ibd_ref_lien_nb;        /* nb de liens*/
    int ibd_ref_lien[IBD_REF_MAX_LIEN];/* no de zone ou est la posit. de ref.*/
} ibd_ref;
 
#define SEQ_MAX_ENR     2000

typedef struct {
    int nb_max_enr;
    int nb_enr;
    int taille_enr;
    int tab_enr[SEQ_MAX_ENR];
    int tab_trou[SEQ_MAX_ENR];
} struct_entete_enr ;

typedef struct {
	int ibd_nb_ref ;
#ifdef i386
	char		dummy_align[4] ;
#endif
	ibd_ref ibd_reflist[IBD_MAX_REF] ;
} ibd_stru_reflist ;

typedef struct {
	ibd_stru_reflist ibd_fil_reflist ;
} ibd_stru_enr_reflist ;

typedef struct _holder_data {
	int id ;
	char *fname ;
	ibd_stru_reflist **rentry;
	void *header ;
	struct_entete_enr	*entete_header ;
	int entries, nb_max, nb_enr, taille_enr, taille_cli;
} holderData, *holderDataP ;

/* Prototypes for mims.c */

int edit_data( ibd_stru_reflist *rp , int entry, char *data );
int edit_comment( ibd_stru_reflist *rp , int entry, char *comment );
int edit_xy_entry ( ibd_stru_reflist *rp, int entry, double x, double y);
int delete_entry( ibd_stru_reflist *rp, int entry ) ;
int append_entry( ibd_stru_reflist *r,double x,double y,char *, char *d) ;
int insert_entry( ibd_stru_reflist *rp,
			  int entry, double x, double y, char *com, char *dat ) ;
int print_point( ibd_stru_reflist *rp , int entry );
int read_stage_list( char *stgFile, double **stgX, double **stgY,
				 char ***stgComment ) ;
char * get_date_str() ;
void translate_nikon_to_mims(int spts,
	double *stgX, double *stgY,
	double ax, double bx, double ay, double by ) ;
void * open_holder_data( char *fname ) ;
int save_holder_data( void *data, char *fname ) ;
void free_holder_data( void *data ) ;
int holder_data_get_x( void *data, int entry ) ;
int holder_data_get_y( void *data, int entry );
char * holder_data_get_date( void *data, int entry);
char * holder_data_get_comment( void *data, int entry);

#endif
