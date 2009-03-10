
package holder_data_mgmt;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
/**
 *
 * @author bepstein
 * Specific class to handle the file I/O for the holder coordinates file
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

    /**
     * Writes out ther file based upon the ref point list rfl
     */
    public void writeFileOut
        (RefPointList r_p_l,
        FileOutputStream fos
        ){
        RefPoint rf;


    }

    /* private constants */
     /* describe file formating, mostly
     */
    private final int nRecordHeaderSizeLocation = 0;
    private final int nRecordHeaderSize = 16012;
    private final int nFileHeaderSizeLocation = 4;
    private final int nFileHeaderSize = 240;

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

    private void writeOutFileHeader
        (FileOutputStream fo
        ){

        try{
            fo.write( int32ToByte4(nRecordHeaderSize) );
            fo.write( int32ToByte4(nFileHeaderSize) );
        } catch (IOException ie){

        }
    }

    private byte[] int32ToByte4(int i)
    {
        byte[] byte_arr = new byte[4];

        byte_arr[0] = (byte)(i);
        byte_arr[1] = (byte)(i >> 8);
        byte_arr[2] = (byte)(i >> 16);
        byte_arr[3] = (byte)(i >> 24);

        return( byte_arr );
    }
}
