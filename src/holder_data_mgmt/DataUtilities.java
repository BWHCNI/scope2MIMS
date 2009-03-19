/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package holder_data_mgmt;

/**
 *
 * @author bepstein
 * Container class for utility functions.
 */

public class DataUtilities {

    public static byte[] adjustAndNullTerminateByteArray(
            byte[] in_arr,
            int length
            )
    {
        int i;
        int copy_limit;
        byte[] ret_value = null;
        final byte null_byte = 0;

        if ( length <= 0 )
            return( ret_value );

        ret_value = new byte[length];
        ret_value[ length - 1 ] = null_byte;

        if (in_arr.length >= length - 1)
        {
            copy_limit = length - 1;
        } else {
            copy_limit = in_arr.length;

            for (i = copy_limit; i < length - 1; i++)
                ret_value[i] = null_byte;
        }

        for (i = 0; i < copy_limit; i++)
            ret_value[i] = in_arr[i];

        return( ret_value );
    }

    public static void reverseByteOrder(byte[] bytes_in_out)
    {
        byte temp_b;
        int i, j, k;

        for (i = 0; i < bytes_in_out.length; i = i + 2)
        {
            j = i / 2;
            k = bytes_in_out.length - j - 1;

            if ( j >= k )
                break;

            temp_b = bytes_in_out[j];
            bytes_in_out[j] = bytes_in_out[k];
            bytes_in_out[k] = temp_b;
        }
    }

    public static long byteToLong(byte b)
    {
        long ret_value = 0;

        byte bit1_mask = (byte)0x01;
        byte bit2_mask = (byte)0x02;
        byte bit3_mask = (byte)0x04;
        byte bit4_mask = (byte)0x08;
        byte bit5_mask = (byte)0x10;
        byte bit6_mask = (byte)0x20;
        byte bit7_mask = (byte)0x40;
        byte bit8_mask = (byte)0x80;

        long bit1_mask_long = 0x0000000000000001L;
        long bit2_mask_long = 0x0000000000000002L;
        long bit3_mask_long = 0x0000000000000004L;
        long bit4_mask_long = 0x0000000000000008L;
        long bit5_mask_long = 0x0000000000000010L;
        long bit6_mask_long = 0x0000000000000020L;
        long bit7_mask_long = 0x0000000000000040L;
        long bit8_mask_long = 0x0000000000000080L;

        if ( (b & bit1_mask)  != 0 )
            ret_value = ret_value | bit1_mask_long;

        if ( (b & bit2_mask)  != 0 )
            ret_value = ret_value | bit2_mask_long;

        if ( (b & bit3_mask) != 0 )
            ret_value = ret_value | bit3_mask_long;

        if ( (b & bit4_mask)  != 0 )
            ret_value = ret_value | bit4_mask_long;

        if ( (b & bit5_mask)  != 0 )
            ret_value = ret_value | bit5_mask_long;

        if ( (b & bit6_mask)  != 0 )
            ret_value = ret_value | bit6_mask_long;

        if ( (b & bit7_mask)  != 0 )
            ret_value = ret_value | bit7_mask_long;

        if ( (b & bit8_mask)  != 0 )
            ret_value = ret_value | bit8_mask_long;

        return( ret_value );
    }

    public static long byte8ToLong(byte [] bytes_in){
        long ret_value = 0;
        int i, j, k;
        long curr_byte;

        if (bytes_in.length != 8)
            return( ret_value);

        for (i = 0; i < 8; i++)
        {
            curr_byte = byteToLong( bytes_in[i] );

            for (j = 0; j < i; j++)
                curr_byte = curr_byte << 8;

            ret_value = ret_value | curr_byte;
        }

        return( ret_value );
    }

    public static double byte8ToDouble(byte[] bytes_in)
    {
        long lvalue = byte8ToLong(bytes_in);
        return( Double.longBitsToDouble(lvalue) );
    }

    public static byte[] intToByteArr(int intvalue)
    {
        byte ret_arr[] = new byte[4];

        ret_arr[0] = (byte)(intvalue & 0x00000ff);
        ret_arr[1] = (byte)((intvalue & 0x0000ff00) >> 8);
        ret_arr[2] = (byte)((intvalue & 0x00ff0000) >> 16);
        ret_arr[3] = (byte)((intvalue & 0xff000000) >> 24);
        
        return( ret_arr );
    }

    public static byte[] longToByteArr(long longvalue)
    {
        byte ret_arr[] = new byte[8];

        ret_arr[0] = (byte)(longvalue & 0x00000000000000ffL);
        ret_arr[1] = (byte)((longvalue & 0x000000000000ff00L) >> 8);
        ret_arr[2] = (byte)((longvalue & 0x0000000000ff0000L) >> 16);
        ret_arr[3] = (byte)((longvalue & 0x00000000ff000000L) >> 24);
        ret_arr[4] = (byte)((longvalue & 0x000000ff00000000L) >> 32);
        ret_arr[5] = (byte)((longvalue & 0x0000ff0000000000L) >> 40);
        ret_arr[6] = (byte)((longvalue & 0x00ff000000000000L) >> 48);
        ret_arr[7] = (byte)((longvalue & 0xff00000000000000L) >> 56);

        return( ret_arr );
    }

    public static byte[] doubleToByteArr(double dvalue)
    {
        return( longToByteArr( Double.doubleToRawLongBits(dvalue) ) );
    }

}
