/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nrims.holder_data_mgmt;

/**
 * Class emulating an individual reference point
 * and data associated therewith.
 * @author bepstein
 */
public class REFPoint extends DataPoint {
    /* private methods and variables */
    private String ibd_ref_com; /* comment */
    private final int comment_length = 80;

    private static final String default_comment = "Holder reference point" +
            "                                                          "; /* total 80 chars */


    private String ibd_ref_dat; /* date */
    private final int date_str_length = 20;

    private final String default_date_str = "1950/01/01 13:14:15 "; /* total 20 chars */

    private double x; /* x coordinate */
    private double y; /* y coordinate */
    private double z; /* z coordinate */
    private final int ibd_max_ref = 200; /* maximum number of points */
    private int ibd_ref_lien_nb; /* number of link */
    private int ibd_ref_lien[]; /* ref point link */

    /* fields for dummy/extraneous bytes */
    private byte[] buffer_arr1;
    private final int byte_arr1_length = 4;
    private byte[] buffer_arr2;
    private final int byte_arr2_length = 4;

    /* total length of RefPoint inside the file */
    private final int ref_point_record_total_length = 936;
    
    private void initData()
    {
        int[] dummy_int_arr = new int[ibd_max_ref];

        int i;

        for (i = 0; i<dummy_int_arr.length; i++)
            dummy_int_arr[ i ] = 0;

        buffer_arr1 = new byte[byte_arr1_length];

        for (i = 0; i<buffer_arr1.length; i++)
            buffer_arr1[ i ] = 0;

        buffer_arr2 = new byte[byte_arr2_length];

        for (i = 0; i<buffer_arr2.length; i++)
            buffer_arr2[ i ] = 0;

        setComment( default_comment );
        setDateString( default_date_str );
        setXCoord( 0 );
        setYCoord( 0 );
        setZCoord( 0 );
        setNumberOfLinks( ibd_max_ref );
        setRefPointLinks( dummy_int_arr );
    }

    /* constructor methods */
    public REFPoint()
    {
        initData();
    }
    
    public REFPoint(
            String comment,
            String date_str,
            double xcoord,
            double ycoord,
            double zcoord,
            int num_of_link,
            int ref_point_links[])
    {
        initData();
        
        setComment( comment );
        setDateString( date_str );
        setXCoord( xcoord );
        setYCoord( ycoord );
        setZCoord( zcoord );
        setNumberOfLinks( num_of_link );
        setRefPointLinks( ref_point_links );
    }
    
    /* public methods */    
    public void setComment(String comment)
    {
        ibd_ref_com = comment;

        /* Making sure the length of the comment is exactly comment_length */
        if (ibd_ref_com.length() < comment_length )
        {
            ibd_ref_com = ibd_ref_com.concat(new String(new byte[comment_length - ibd_ref_com.length()]));
        }   else if (ibd_ref_com.length() > comment_length )
        {
            ibd_ref_com = ibd_ref_com.substring(0, comment_length - 1);
        }
    }
    
    public String getComment()
    {
        return( ibd_ref_com );
    }
    
    public void setDateString(String date_str)
    {
        ibd_ref_dat = date_str;

        /* making sure the length of ibd_ref_dat is exactly date_str_length */
        if (ibd_ref_dat.length() < date_str_length)
        {
            ibd_ref_dat = ibd_ref_dat.concat(new String(new byte[date_str_length - ibd_ref_dat.length()]));
        } else if (ibd_ref_dat.length() > date_str_length){
             ibd_ref_dat = ibd_ref_dat.substring(0, date_str_length - 1);
        }
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
    
    public void setNumberOfLinks(int nlink)
    {
        ibd_ref_lien_nb = nlink;
    }
    
    public int getNumberOfLinks()
    {
        return( ibd_ref_lien_nb );
    }
    
    public void setRefPointLinks(int[] rfl)
    {
        int i;
        int temp_arr[] = rfl;
        ibd_ref_lien = new int[ ibd_max_ref ];

        if ( rfl.length > ibd_max_ref )
        {
            for (i = 0; i < ibd_max_ref; i++)
                ibd_ref_lien[i] = rfl[i];
        } else if ( rfl.length < ibd_max_ref )
        {
            for (i = 0; i < rfl.length; i++)
                ibd_ref_lien[i] = rfl[i];

            for (i = rfl.length; i < ibd_max_ref; i++)
                ibd_ref_lien[i] = 0;
        }
        
    }
    
    public int[] getRefPointLinks()
    {
        return( ibd_ref_lien );
    }

    public void setBufferArr1(byte [] bytes_in)
    {
        int i, size;

        if ( bytes_in == null )
            return;

        size = buffer_arr1.length;

        if ( bytes_in.length < size )
            size = bytes_in.length;

        for (i = 0; i < size; i++)
            buffer_arr1[ i ] = bytes_in[ i ];
    }

    public byte[] getBufferArr1()
    {
        return( buffer_arr1 );
    }

    public void setBufferArr2(byte [] bytes_in)
    {
        int i, size;

        if ( bytes_in == null )
            return;

        size = buffer_arr2.length;

        if ( bytes_in.length < size )
            size = bytes_in.length;

        for (i = 0; i < size; i++)
            buffer_arr2[ i ] = bytes_in[ i ];
    }

    public byte[] getBufferArr2()
    {
        return( buffer_arr2 );
    }

    /* Presents the RefPoint as a byte array for file I/O. */
    public byte[] toByteArray()
    {
        byte[] ret_value = new byte[ ref_point_record_total_length ];
        int i;
        int offset = 0;

        /* copy buffer_arr1 */
        for (i = 0; i < buffer_arr1.length; i++)
            ret_value[offset + i] = buffer_arr1[i];

        offset += buffer_arr1.length;

        /* copy comment */

        return( ret_value );
    }

    public static String getDefaultComment()
    {
        return( default_comment );
    }
    
    /*
     * Method moved from DataPointFileProcessor class. Changed to be on this 
     * ref point rather than having a ref point as a parameter.
     */
    private void printRefPoint()
            
    {
        System.out.println("X: " + getXCoord());
        System.out.println("Y: " + getYCoord());
        System.out.println("Z: " + getZCoord());
        System.out.println("Comment: " + getComment());
        System.out.println("Date: " + getDateString());
    }
}
