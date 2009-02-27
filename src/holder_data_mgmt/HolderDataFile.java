
package holder_data_mgmt;

/**
 *
 * @author bepstein
 * Specific class to handle the file I/O for the holder coordinates file
 */
public class HolderDataFile {
    /* constructors */
    public HolderDataFile(){

    }

    public HolderDataFile(String fpath){
        setFilePath(fpath);
    }

    public String getFilePath(){
        return( file_path );
    }

    public void setFilePath(String path){
        file_path = path;
    }


    /* private constants
     * describe file formating, mostly
     */
    private final int nRecordHeaderSizeLocation = 0;
    private final int nRecordHeaderSize = 16012;
    private final int nFileHeaderSizeLocation = 4;
    private final int nFileHeaderSize = 240;

    /* private variables and methods */
    private String file_path;
}
