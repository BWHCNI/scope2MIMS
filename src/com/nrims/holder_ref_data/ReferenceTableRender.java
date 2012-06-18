/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nrims.holder_ref_data;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author fkashem
 */
public class ReferenceTableRender extends DefaultTableCellRenderer {
    public Component getTableCellRendererComponent(
    JTable aTable, 
    Object obj, 
    boolean isSelected, 
    boolean hasFocus, 
    int aRow, int aColumn
  ) {  
    /* 
    * Implementation Note :
    * It is important that no "new" be present in this 
    * implementation (excluding exceptions):
    * if the table is large, then a large number of objects would be 
    * created during rendering.
    */
    if (obj == null) return this;
    Component renderer = super.getTableCellRendererComponent(
      aTable, obj, isSelected, hasFocus, aRow, aColumn
    );
    
    String s =  aTable.getModel().getValueAt(aRow, 4).toString();
    
    if(isSelected) {
        renderer.setBackground(Color.blue.darker().darker());
        renderer.setForeground(Color.white);
    } else if (s.equalsIgnoreCase("true")) {
      renderer.setBackground(Color.green.brighter());
      renderer.setForeground(Color.black);
    } else {
        renderer.setBackground(null);
        renderer.setForeground(Color.black);
    }
    
    return this;
  }
}


