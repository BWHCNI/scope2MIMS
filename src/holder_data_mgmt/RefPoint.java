/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package holder_data_mgmt;

/**
 *
 * @author bepstein
 */
public class RefPoint {
    /* constructor methods */
    public void RefPoint()
    {    
    }
    
    public void RefPoint(
            String comment,
            String date_str,
            double xcoord,
            double ycoord,
            double zcoord,
            int num_of_link,
            int ref_point_links[])
    {
        setComment( comment );
        setDateString( date_str );
        setXCoord( xcoord );
        setYCoord( ycoord );
        setZCoord( zcoord );
        setNumberOfLink( num_of_link );
        setRefPointLinks( ref_point_links );
    }
    
    /* public methods */    
    public void setComment(String comment)
    {
        ibd_ref_com = comment;
    }
    
    public String getComment()
    {
        return( ibd_ref_com );
    }
    
    public void setDateString(String date_str)
    {
        ibd_ref_dat = date_str;
    }
    
    public String getDateString()
    {
        return( ibd_ref_dat );
    }
    
    public void setXCoord(double xcoord)
    {
        x = xcoord;
    }
    
    public double getXCoord()
    {
        return( x );
    }
    
    public void setYCoord(double ycoord)
    {
        y = ycoord;
    }
    
    public double getYCoord()
    {
        return( y );
    }
    
    public void setZCoord(double zcoord)
    {
        z = zcoord;
    }
    
    public double getZCoord()
    {
        return( z );
    }
    
    public void setNumberOfLink(int nlink)
    {
        ibd_ref_lien_nb = nlink;
    }
    
    public int getNumberOfLink()
    {
        return( ibd_ref_lien_nb );
    }
    
    public void setRefPointLinks(int[] rfl)
    {
        ibd_ref_lien = rfl;
    }
    
    public int[] getRefPointLinks()
    {
        return( ibd_ref_lien );
    }
    
    /* private methods and variables */
    private String ibd_ref_com; /* comment */
    private String ibd_ref_dat; /* date */
    private double x; /* x coordinate */
    private double y; /* y coordinate */
    private double z; /* z coordinate */
    private int ibd_ref_lien_nb; /* number of link */
    private int ibd_ref_lien[]; /* ref point link */
    
}
