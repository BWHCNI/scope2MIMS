/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nrims.holder_ref_data;

import com.nrims.holder_data_mgmt.DataPointFileProcessor;
import com.nrims.holder_data_mgmt.DataPoint;
import java.util.ArrayList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author fkashem
 */
public class NikonTableModel extends AbstractTableModel {
    private TableModel tm = null;
    private DataPointFileProcessor dpfp = null;
    private int row_count;
    private String[] column_names =
    {
        "Point #",
        "X",
        "Y",
        "Z",
        "Reference",
    };
    private static final int POINT_NUM_COL_NUM = 0;
    private static final int X_COORD_COL_NUM = 1;
    private static final int Y_COORD_COL_NUM = 2;
    private static final int Z_COORD_COL_NUM = 3;
    private static final int REFERENCE_COL_NUM = 4;
    private final int column_count = column_names.length;
    private Object[][] table_content;
    
    public NikonTableModel(DataPointFileProcessor dp_in) {
        dpfp = dp_in;
        setDataPointFileProcessor(dp_in);
    }

    public int getRowCount() {
        return row_count;
    }

    public int getColumnCount() {
        return column_count;
    }

    public Object getValueAt(int row, int col) {
        return( table_content[row][col] );
    }
    
    public String getColumnName(int column)
    {
        if ( column_count <= column )
            return( new Integer( column ).toString() );

        return( column_names[ column ] );
    }
    
    public DataPointFileProcessor getDataPointFileProcessor() {
        return( dpfp );
    }
    
    public void setDataPointFileProcessor(DataPointFileProcessor dp_in) {
        int i;
        dpfp = dp_in;
        DataPoint addPoint;
        ArrayList<DataPoint> ptsList = dpfp.getScopePoints();

        if (ptsList == null)
            return;

        row_count = ptsList.size();

        if ( row_count == 0 )
        {
            table_content = null;
            return;
        }

        table_content = new Object[row_count][column_count];

        /* Filling up the content */
        for (i = 0; i < row_count; i++)
        {
            addPoint = ptsList.get(i);
            table_content[i][POINT_NUM_COL_NUM] = new Integer ( addPoint.getNum() );
            table_content[i][X_COORD_COL_NUM] = new Double( addPoint.getXCoord() );
            table_content[i][Y_COORD_COL_NUM] = new Double( addPoint.getYCoord() );
            table_content[i][Z_COORD_COL_NUM] = new Double( addPoint.getZCoord() );
            table_content[i][REFERENCE_COL_NUM] = new Boolean ( addPoint.getIsReference() );
        }
    }
    
}
