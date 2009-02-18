/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package holder_data_mgmt;

/**
 *
 * @author bepstein
 */
public class HolderData {
    /* constructor classes */
    public HolderData()
    {
    }
   
    public HolderData( int nrp )
    {
        setNumRefPoints( nrp );
    }
    
    /* public */
    public void setNumRefPoints(int nrp)
    {
        num_ref_points = nrp;
    }
    
    public int getNumRefPoints()
    {
        return( num_ref_points );
    }
    
    /* private */
    private int num_ref_points;
}
