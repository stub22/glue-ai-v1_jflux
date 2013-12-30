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

/*
 * AvroTablePanel.java
 *
 * Created on Mar 6, 2013, 3:13:54 PM
 */
package org.jflux.swing.messaging.monitor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.jflux.api.core.Listener;

/**
 *
 * @author Matthew Stevenson <www.robokind.org>
 */
public class AvroTablePanel extends javax.swing.JPanel implements Listener<IndexedRecord>{
    private AvroTableModel myModel;
    private List<IndexedRecord> myRecords;
    private JTable jTable1;
    private JTextField jTextField1;
    private GridBagConstraints myConstraints;
    private Schema mySchema;
    private JScrollPane jScrollPane1;
    private List<JCheckBox> myCheckBoxes;
    private List<String> myFilters;
    private List<IndexedRecord> myFilteredRecords;
    
    /** Creates new form AvroTablePanel */
    public AvroTablePanel() {
        initComponents();
        setSchema(null);
    }
    
    public final void setSchema(Schema schema) {
        myConstraints = new GridBagConstraints();
        myConstraints.fill = GridBagConstraints.HORIZONTAL;
        boolean odd = true;
        removeAll();
        
        mySchema = schema;
        setLayout(new GridBagLayout());
        
        if(mySchema == null) {
            revalidate();
            return;
        }
        
        jTextField1 = new JTextField();
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
        });
        
        myConstraints.gridwidth = 2;
        myConstraints.gridx = 0;
        myConstraints.weightx = 1.0;
        myConstraints.gridy = 0;
        add(jTextField1, myConstraints);

        myConstraints.weightx = 0.0;
        myConstraints.gridwidth = 1;
        
        myCheckBoxes = new ArrayList<JCheckBox>();
        
        for(Schema.Field field: mySchema.getFields()) {
            if(odd) {
                myConstraints.gridy++;
                myConstraints.gridx = 0;
            } else {
                myConstraints.gridx = 1;
            }
            
            odd = !odd;
            
            JCheckBox checkBox = new JCheckBox(field.name());
            
            checkBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    filterCache();
                }
            });
            
            myCheckBoxes.add(checkBox);
            add(checkBox, myConstraints);
        }
        
        myConstraints.gridx = 0;
        myConstraints.weightx = 1.0;
        myConstraints.gridy++;
        myConstraints.weighty = 1.0;
        myConstraints.gridheight = 6;
        myConstraints.gridwidth = 2;
        myConstraints.fill = GridBagConstraints.BOTH;
        
        myModel = new AvroTableModel();
        myRecords = new ArrayList<IndexedRecord>();
        myModel.setRecords(myRecords);
        myModel.setSchema(mySchema);
        
        jTable1 = new JTable(myModel);
        jScrollPane1 = new JScrollPane(jTable1);
        add(jScrollPane1, myConstraints);
        
        myConstraints.gridy += 6;
        myConstraints.gridheight = 1;
        myConstraints.gridwidth = 1;
        myConstraints.fill = GridBagConstraints.NONE;
        
        setFilters("");
        revalidate();
    }
    
    public void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {
        setFilters(jTextField1.getText());
    }
    
    public void setFilters(String filterStr) {
        myFilters = new ArrayList<String>();

        String[] filters = filterStr.split(",");
        for(String s : filters) {
            s = s.trim();
            if(!s.isEmpty()) {
                myFilters.add(s);
            }
        }
        filterCache();
    }
    
    private void filterCache() {
        if(myFilters == null || myFilters.isEmpty()) {
            myFilteredRecords = myRecords;
            refresh();
            
            return;
        }

        myFilteredRecords = new ArrayList<IndexedRecord>();
        for(IndexedRecord thals : myRecords) {
            if(filterList(thals)) {
                myFilteredRecords.add(thals);
            }
        }

        refresh();
    }
    
    private boolean filterList(IndexedRecord thals){
        for(JCheckBox checkBox: myCheckBoxes) {
            if(checkBox.isSelected()){
                int index = -1;
                boolean timestamp = false;
                String s;
                
                for(int i = 0; i < mySchema.getFields().size(); i++) {
                    if(mySchema.getFields().get(i).name().equals(checkBox.getText())) {
                        index = i;
                        
                        if(checkBox.getText().equals("timestampMillisecUTC")) {
                            timestamp = true;
                        }
                        
                        break;
                    }
                }
                
                if(index < 0) {
                    continue;
                }
                
                if(timestamp) {
                    SimpleDateFormat sdf =
                            new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
                    
                    s = sdf.format(thals.get(index));
                } else {
                    s = thals.get(index).toString();
                }
                
                for(String f : myFilters){
                    Pattern p = Pattern.compile(
                            ".*" + f + ".*", Pattern.MULTILINE | Pattern.DOTALL);
                    if(s != null && p.matcher(s).matches()){
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    private void refresh(){
        myModel.setRecords(myFilteredRecords);
        myModel.fireTableDataChanged();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 414, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 403, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    @Override
    public void handleEvent(IndexedRecord t) {
        myRecords.add(t);
        filterCache();
    }
    
    public List<IndexedRecord> getFilteredRecords() {
        return myFilteredRecords;
    }
}
