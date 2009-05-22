/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * RefFileContentReviewFrame.java
 *
 * Created on May 20, 2009, 9:10:06 AM
 */

package com.nrims.holder_ref_data;

import com.nrims.holder_data_mgmt.*;
import org.jdesktop.application.Action;

/**
 *
 * @author bepstein
 */
public class RefFileContentReviewFrame extends javax.swing.JFrame {

    /** Creates new form RefFileContentReviewFrame */
    public RefFileContentReviewFrame() {
        initComponents();
        initLocalData();
    }

    public RefFileContentReviewFrame(DataPointFileProcessor dp)
    {
        initComponents();
        initLocalData();
        dpfp = dp;
        rdr_tm.setDataPointFileProcessor(dp);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        ref_data_review_table = new javax.swing.JTable();
        edit_commit_button = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(com.nrims.holder_ref_data.Holder_Ref_Data_App.class).getContext().getResourceMap(RefFileContentReviewFrame.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        ref_data_review_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Point Number", "Comment", "Date", "X", "Y", "Z"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ref_data_review_table.setCellSelectionEnabled(true);
        ref_data_review_table.setName("ref_data_review_table"); // NOI18N
        jScrollPane1.setViewportView(ref_data_review_table);
        ref_data_review_table.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("ref_data_review_table.columnModel.title0")); // NOI18N
        ref_data_review_table.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("ref_data_review_table.columnModel.title1")); // NOI18N
        ref_data_review_table.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("ref_data_review_table.columnModel.title2")); // NOI18N
        ref_data_review_table.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("ref_data_review_table.columnModel.title3")); // NOI18N
        ref_data_review_table.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("ref_data_review_table.columnModel.title4")); // NOI18N
        ref_data_review_table.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("ref_data_review_table.columnModel.title5")); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(com.nrims.holder_ref_data.Holder_Ref_Data_App.class).getContext().getActionMap(RefFileContentReviewFrame.class, this);
        edit_commit_button.setAction(actionMap.get("commitChangesToRefPointFile")); // NOI18N
        edit_commit_button.setText(resourceMap.getString("edit_commit_button.text")); // NOI18N
        edit_commit_button.setName("edit_commit_button"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(edit_commit_button)
                .addContainerGap(76, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(edit_commit_button)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RefFileContentReviewFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton edit_commit_button;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable ref_data_review_table;
    // End of variables declaration//GEN-END:variables

    private DataPointFileProcessor dpfp;
    private RDRTableModel rdr_tm;

    private void initLocalData()
    {       
        dpfp = null;
        rdr_tm = new RDRTableModel( ref_data_review_table.getModel() );
        ref_data_review_table.setModel(rdr_tm);
    }

    @Action
    public void commitChangesToRefPointFile() {
        HolderDataFile hdf = new HolderDataFile( dpfp.getHolderPointFilePath(),
                true,
                dpfp.getRefPointList());

        hdf.writeFileOut();
        hdf.close();
        this.setVisible(false);
    }
}