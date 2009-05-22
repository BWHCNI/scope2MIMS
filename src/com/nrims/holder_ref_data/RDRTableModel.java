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


        for (i = 0; i < row_count; i++)
        {
            rf = rpl.getRefPoint(i);
            setValueAt(new Integer(i), i, 0);
            setValueAt(rf.getComment(), i, 1 );
            setValueAt(rf.getDateString(), i, 2);
            setValueAt(new Double( rf.getXCoord() ), i, 3);
            setValueAt(new Double( rf.getYCoord() ), i, 4);
            setValueAt(new Double( rf.getZCoord() ), i, 5);
        }
    }

    public DataPointFileProcessor getDataPointFileProcessor()
    {
        return( dpfp );
    }
}
