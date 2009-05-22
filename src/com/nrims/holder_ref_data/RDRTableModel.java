/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nrims.holder_ref_data;

import javax.swing.table.*;
import com.nrims.holder_data_mgmt.*;

/**
 *
 * @author bepstein
 */
public class RDRTableModel extends AbstractTableModel{
    /* Private variables */
    private TableModel tm = null;
    private DataPointFileProcessor dpfp = null;
    private int row_count;


    private String[] column_names =
    {
        "Point #",
        "Comment",
        "Date",
        "X",
        "Y",
        "Z"
    };

    private static final int POINT_NUM_COL_NUM = 0;
    private static final int COMMENT_COL_NUM = 1;
    private static final int DATE_COL_NUM = 2;
    private static final int X_COORD_COL_NUM = 3;
    private static final int Y_COORD_COL_NUM = 4;
    private static final int Z_COORD_COL_NUM = 5;

    private final int column_count = column_names.length;

    private Object[][] table_content;

    private void initInternalData(TableModel tm_in)
    {
        int i, j;

        tm = tm_in;
        row_count = tm.getRowCount();

        /* Copying content */
        if ( (row_count > 0) )
        {
            table_content = new Object[row_count][column_count];

            for (i = 0; i < row_count; i++)
            {
                for (j = 0; j < column_count; j++)
                    table_content[i][j] = tm.getValueAt(i, j);
            }
        }
    }

    private void initInternalData(TableModel tm_in,
            DataPointFileProcessor dp_in)
    {
        initInternalData( tm_in );
        setDataPointFileProcessor( dp_in );
    }

    /* Constructors */
    public RDRTableModel(TableModel tm_in)
    {
        initInternalData(tm_in);
    }

    public RDRTableModel(TableModel tm_in,
            DataPointFileProcessor dp_in)
    {
        initInternalData(tm_in, dp_in);
    }

    /* Public methods */
    @Override
    public boolean isCellEditable(int row, int column)
    {
        if ( column == 0 )
            return( false );

        return( true );
    }

    @Override
    public String getColumnName(int column)
    {
        if ( column_count <= column )
            return( new Integer( column ).toString() );

        return( column_names[ column ] );
    }

    @Override
    public int getColumnCount()
    {
        return( column_count );
    }

    @Override
    public int getRowCount()
    {
        return( row_count );
    }

    @Override
    public void setValueAt(Object obj, int row, int column)
    {
        table_content[row][column] = obj;
        fireTableCellUpdated(row, column);
        RefPointList rpl = dpfp.getRefPointList();

        /* Updating the corresponding point in dpfp */
        switch ( column )
        {
            case COMMENT_COL_NUM:
                rpl.getRefPoint(row).setComment( obj.toString() );
                break;
            case DATE_COL_NUM:
                rpl.getRefPoint(row).setDateString( obj.toString() );
                break;
            case X_COORD_COL_NUM:
                rpl.getRefPoint(row).setXCoord(new Double( obj.toString() ));
                break;
            case Y_COORD_COL_NUM:
                rpl.getRefPoint(row).setYCoord(new Double( obj.toString() ));
                break;
            case Z_COORD_COL_NUM:
                rpl.getRefPoint(row).setZCoord(new Double( obj.toString() ));
                break;
        }

        dpfp.setRefPointList(rpl);
    }

    @Override
    public Object getValueAt(int row, int col)
    {
        return( table_content[row][col] );
    }

    public void setDataPointFileProcessor(DataPointFileProcessor dp_in)
    {
        int i;
        dpfp = dp_in;
        RefPointList rpl = dpfp.getRefPointList();
        RefPoint rf;

        if (rpl == null)
            return;

        row_count = rpl.getNumRefPoints();

        if ( row_count == 0 )
        {
            table_content = null;
            return;
        }

        table_content = new Object[row_count][column_count];

        /* Filling up the content */
        for (i = 0; i < row_count; i++)
        {
            rf = rpl.getRefPoint(i);
            table_content[i][POINT_NUM_COL_NUM] = new Integer(i + 1);
            table_content[i][COMMENT_COL_NUM] = rf.getComment();
            table_content[i][DATE_COL_NUM] = rf.getDateString();
            table_content[i][X_COORD_COL_NUM] = new Double( rf.getXCoord() );
            table_content[i][Y_COORD_COL_NUM] = new Double( rf.getYCoord() );
            table_content[i][Z_COORD_COL_NUM] = new Double( rf.getZCoord() );
        }
    }

    public DataPointFileProcessor getDataPointFileProcessor()
    {
        return( dpfp );
    }
}