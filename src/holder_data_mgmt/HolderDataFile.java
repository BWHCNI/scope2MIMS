
package holder_data_mgmt;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
/**
 *
 * @author bepstein
 * Specific class to handle the file I/O for the holder coordinates file
 * The formatting assumes big-endian arrangement where applicable.
 */
public class HolderDataFile {
    /* constructors */
    public HolderDataFile() throws IOException{
        setStreamsToNull();
        setFilePathsToNull();
    }

    public HolderDataFile(
            String fpath,
            Boolean open_for_write
        ){
        try{
            setStreamsToNull();
            setFilePathsToNull();

            if (open_for_write){
                setOutFilePath(fpath);
                file_out = new FileOutputStream( getOutFilePath() );
            } else {
                setInFilePath( fpath );
                 file_in = new FileInputStream( getInFilePath() );
            }
        } catch (Exception e){
            
        }
    }

    public HolderDataFile(
            String fpath,
            Boolean open_for_write,
            RefPointList r_p_l
        ){
        try{
            setStreamsToNull();
            setFilePathsToNull();

            if (open_for_write){
                setOutFilePath(fpath);
                file_out = new FileOutputStream( getOutFilePath() );
            } else {
                setInFilePath( fpath );
                 file_in = new FileInputStream( getInFilePath() );
            }
        } catch (Exception e){

        }

        setRefPointList( r_p_l );
    }

    public HolderDataFile(
            String in_fpath,
            String out_fpath,
            RefPointList r_p_l
        ){
        try{
            setStreamsToNull();
            setFilePathsToNull();

            setOutFilePath(out_fpath);
            file_out = new FileOutputStream( getOutFilePath() );

            setInFilePath( in_fpath );
            file_in = new FileInputStream( getInFilePath() );

        } catch (Exception e){

        }

        setRefPointList( r_p_l );
    }
    /* public methods and variables */
 

    public void setInFilePath(String path){
        in_file_path = path;
    }

    public String getInFilePath(){
        return( in_file_path );
    }

    public void setOutFilePath(String path){
        out_file_path = path;
    }

    public String getOutFilePath(){
        return( out_file_path );
    }

    public void setRefPointList(RefPointList r_p_l){
        rpl = r_p_l;
    }

    public RefPointList getPointList(){
        return( rpl );
    }

    public void readFileIn(RefPointList r_p_l,
            FileInputStream fis
            ){

    }

    /**
     * Writes out ther file based upon the ref point list rfl
     */
    public void writeFileOut
        (RefPointList r_p_l,
        FileOutputStream fos
        ){
        RefPoint rf;
        int offset;
        int point_ref_offset;
        int i;
        byte b = 0;

        try {
            /* writing the header */
            offset = writeOutFileHeader( fos );

            /* Seeing if we are far enough along in the file */
            point_ref_offset = referenceListOffset(point_arr_num);

            if (point_ref_offset > offset)
            {
                for (i = offset; i < point_ref_offset; i++)
                {
                    fos.write(b);
                }

                offset = point_ref_offset;
            }

            /* Writing out the number of points.
             */
            writeOutInt(fos, r_p_l.getNumRefPoints());
            offset += 4;
            
            for (i = 0; i < r_p_l.getNumRefPoints(); i++)
                offset += writeOutRefPoint(fos, r_p_l.getRefPoint(i));

        /**/
        } catch (IOException ioe){

        }
    }

    /* private constants */
     /* describe file formating, mostly
     */
    private final int nRecordHeaderSizeLocation = 0;
    private final int nRecordHeaderSize = 16012;
    private final int nFileHeaderSizeLocation = 4;
    private final int nFileHeaderSize = 240;
    private final int ibd_ref_size = 990;
    private final int int_size = 4;
    private final int double_size = 8;
    private final int point_arr_num = 0; /* numero de pe */

    /* struct entete_enr_structure copied */
    private final int nb_max_enr = 0;
    private final int nb_enr = 1;
    private final int taille_enr = 1;
    private int[] tab_enr = new int[2000]; /* Ref list tab location */
    private int[] tab_trou = new int[2000]; /* unused */
    /* struct entete_enr_structure end */

    /* private variables and methods */
    private String in_file_path;
    private String out_file_path;
    private FileInputStream file_in;
    private FileOutputStream file_out;
    private RefPointList rpl;

    private void setStreamsToNull() throws IOException{
        if ( file_in != null ){
            file_in.close();
            file_in = null;
        }

        if ( file_out != null ){
            file_out.close();
            file_out = null;
        }
    }

    private void setFilePathsToNull(){
        in_file_path = null;
        out_file_path = null;
    }

    private int writeOutFileHeader
        (FileOutputStream fo
        ){
        int temp_i;
        double temp_d;
        byte[] temp_bytes;
        int curr_offset = 0; /* keeps track of offset into the file */

        try{
           writeOutInt( fo, nRecordHeaderSize );
           writeOutInt( fo, nFileHeaderSize );
           curr_offset += 8;

           /* fill in the header */
           temp_bytes = new byte[nFileHeaderSize];
           fo.write( temp_bytes);
           curr_offset += nFileHeaderSize;

           writeOutInt( fo, nb_max_enr );
           writeOutInt( fo, nb_enr );
           writeOutInt( fo, taille_enr );
           curr_offset += 12;

           writeOutIntArr( fo, tab_enr);
           curr_offset += 4 * tab_enr.length;

           writeOutIntArr( fo, tab_trou );
           curr_offset += 4 * tab_trou.length;
           return(curr_offset);
        } catch (IOException ioe){

        } finally {
            return(curr_offset);
        }
    }

    private int writeOutRefPoint(
            FileOutputStream fo,
            RefPoint rf)
    {
        int offset = 0;
        int i;
        byte[] dummy_bytes = new byte[2];
        int[] ref_point_links_arr;

        try{
            fo.write(rf.getComment().getBytes());
            offset += rf.getComment().getBytes().length;

            fo.write(rf.getDateString().getBytes());
            offset += rf.getDateString().getBytes().length;

            writeOutDouble( fo, rf.getXCoord());
            offset += 8;

            writeOutDouble( fo, rf.getYCoord() );
            offset += 8;

            writeOutDouble( fo, rf.getZCoord() );
            offset += 8;

            fo.write(dummy_bytes);
            offset += dummy_bytes.length;

            writeOutInt( fo, rf.getNumberOfLink() );
            offset += 4;

            ref_point_links_arr = rf.getRefPointLinks();

            for (i = 0; i < ref_point_links_arr.length; i++)
            {
                writeOutInt(fo, ref_point_links_arr[i]);
                offset += 4;
            }

        } catch (IOException ioe) {

        } finally {
            return( offset );
        }
    }

    private void writeOutInt(FileOutputStream fo,
            int i) throws IOException
    {
        byte[] temp_bytes;
        temp_bytes = DataUtilities.intToByteArr(i);
        DataUtilities.reverseByteOrder(temp_bytes);
        fo.write(temp_bytes);
    }

    private void writeOutDouble(
           FileOutputStream fo,
           double d)
           throws IOException
    {
        byte[] temp_bytes;
        temp_bytes = DataUtilities.doubleToByteArr(d);
        DataUtilities.reverseByteOrder(temp_bytes);
        fo.write(temp_bytes);
    }

    private void writeOutIntArr(
            FileOutputStream fo,
            int[] i_arr) throws IOException
    {
        int i;

        for (i = 0; i < i_arr.length; i++)
            writeOutInt(fo, i_arr[i]);
    }

    private int referenceListOffset(int num_point){
        return(8 + nFileHeaderSize + nRecordHeaderSize + tab_enr[num_point] * taille_enr);
    }
}
