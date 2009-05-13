/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nrims.holder_data_mgmt;

/**
 *
 * @author bepstein
 * The class serves as a container for the holder data (emulates the full file).
 */
public class HolderData {
    /* private */
    private int num_ref_points;
    private RefPointList point_list;

    /* constructor classes */
    public HolderData()
    {
        setNumRefPoints(0);
        point_list = null;
    }
   
    public HolderData( int nrp )
    {
        setNumRefPoints( nrp );
        point_list = null;
    }

    public HolderData(RefPointList rpl)
    {
        setRefPointList( rpl );
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
    
    public void setRefPointList(RefPointList rpl)
    {
        point_list = rpl;
    }

    public RefPointList getRefPointList()
    {
        return( point_list );
    }
}
