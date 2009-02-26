
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
    /* private variables and methods */
    private String file_path;
}
