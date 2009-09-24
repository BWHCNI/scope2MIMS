package com.nrims.holder_transform;

/**
 * Class representing microscope and
 * machine stage reference points.
 * @author bepstein
 */

public class MicroInstrReferencePoints
{
    /* Class constants */
    private final static int DIMENSIONS = 2;
    /* private variables */
    private int num_points;

    /* Points as seen under the microscope.
     * Array of (X,Y) pairs/
     */
    private int[][] micro_points;

    /* Points as seen in the instrument.
     * Array of (X,Y) pairs/
     */
    private int[][] instr_points;

    private void fillLastCoordInArray(int x, int y, int[][] in_arr)
    {
            int tmp_arr[] = in_arr[ in_arr. length ];

            if ( tmp_arr.length != DIMENSIONS )
                return;

            in_arr[ in_arr.length ][0] = x;
            in_arr[ in_arr.length ][1] = y;
    }

    private void addPointToMicro(int x, int y)
    {
            if ( micro_points == null )
                micro_points = new int[1][2];

            fillLastCoordInArray(x, y, micro_points);
    }

    private void addPointToInstr(int x, int y)
    {
            if ( instr_points == null )
                instr_points = new int[1][2];

            fillLastCoordInArray(x, y, instr_points);
    }

    private void init_data()
    {
        num_points = 0;
        micro_points = null;
        instr_points = null;
    }

    /* Constructors */
    public MicroInstrReferencePoints()
    {
        init_data();
    }

    /* public methods */
    public int getNumPoints()
    {
            return( num_points );
    }

    public int[] getMicroscopePoint(int index)
    {
            if ( micro_points == null )
                return( null );

            if (index >= micro_points.length)
                return( null );

            return( micro_points[index] );
    }

    public int getMicroscopePointX(int index)
    {
            int[] tmp_pt = getMicroscopePoint(index);

            if (tmp_pt == null)
                return( 0 );

            return( tmp_pt[0] );
    }

    public int getMicroscopePointY(int index)
    {
            int[] tmp_pt = getMicroscopePoint(index);

            if (tmp_pt == null)
                return( 0 );

            return( tmp_pt[1] );
    }

    public int[] getInstrumentPoint(int index)
    {
            if ( instr_points == null )
                return( null );

            if (index >= instr_points.length)
                return( null );

            return( instr_points[index] );
    }

    public int getInstrumentPointX(int index)
    {
            int[] tmp_pt = getInstrumentPoint(index);

            if (tmp_pt == null)
                return( 0 );

            return( tmp_pt[0] );
    }

    public int getInstrumentPointY(int index)
    {
            int[] tmp_pt = getInstrumentPoint(index);

            if (tmp_pt == null)
                return( 0 );

            return( tmp_pt[1] );
    }

    public void addPointPair(
                int mic_x,
                int mic_y,
                int instr_x,
                int instr_y
                )
    {
            addPointToMicro(mic_x, mic_y);
            addPointToInstr(instr_x, instr_y);
            num_points++;
    }
}
