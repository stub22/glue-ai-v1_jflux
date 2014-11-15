/*
 * Copyright 2013 Hanson Robokind LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jflux.swing.messaging.monitor;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.jms.Destination;
import javax.jms.Session;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.IndexedRecord;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;

/**
 *
 * @author jgpallack
 */
public class SavePanel extends javax.swing.JPanel {
    private Schema mySchema;
    private AvroTablePanel myAvroTable;
    
    /**
     * Creates new form SaveLoadPanel
     */
    public SavePanel() {
        initComponents();
    }
    
    public void setSchema(Schema schema) {
        mySchema = schema;
    }
    
    public void setAvroTable(AvroTablePanel avroTable) {
        myAvroTable = avroTable;
    }
    
    public void activate() {
        jButton1.setEnabled(true);
    }
    
    public void deactivate() {
        jButton1.setEnabled(false);
    }
    
    private void saveMessages() {
        List<IndexedRecord> records = myAvroTable.getFilteredRecords();
        
        if(records == null || records.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Nothing to save!",
                    "Nothing to save!", JOptionPane.ERROR_MESSAGE);
            
            return;
        }
        
        JFileChooser fc = new JFileChooser();
        int retVal = fc.showSaveDialog(this);
        
        if(retVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            
            DatumWriter<IndexedRecord> writer =
                    new SpecificDatumWriter<IndexedRecord>(mySchema);
            DataFileWriter<IndexedRecord> fileWriter =
                    new DataFileWriter<IndexedRecord>(writer);
            
            try {
                fileWriter.create(mySchema, file);
                
                for(IndexedRecord record: records) {
                    fileWriter.append(record);
                }
                
                fileWriter.close();
                
                JOptionPane.showMessageDialog(
                        this,
                        "Records saved.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch(IOException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Error: " + ex.getMessage(),
                        "Failure", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();

        jButton1.setText("Save");
        jButton1.setEnabled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(173, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(165, 165, 165))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        saveMessages();
    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    // End of variables declaration//GEN-END:variables
}