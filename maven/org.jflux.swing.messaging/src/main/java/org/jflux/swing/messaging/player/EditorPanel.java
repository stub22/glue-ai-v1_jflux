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
package org.jflux.swing.messaging.player;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.generic.IndexedRecord;

/**
 *
 * @author Jason G. Pallack <jgpallack@gmail.com>
 */
public class EditorPanel extends javax.swing.JPanel {
    private IndexedRecord myRecord;
    private GridBagConstraints myConstraints;
    /**
     * Creates new form EditorPanel
     */
    public EditorPanel() {
        initComponents();

        myConstraints = new GridBagConstraints();
        myConstraints.fill = GridBagConstraints.HORIZONTAL;
    }
       
    public void setRecord(IndexedRecord record) {
        myRecord = record;
        Schema schema = myRecord.getSchema();
        
        removeAll();
        myConstraints.gridx = 0;
        myConstraints.gridy = 0;
        myConstraints.weightx = 0.0;
        myConstraints.gridwidth = 1;
        setLayout(new GridBagLayout());
        
        List<Field> fields = schema.getFields();
        
        for(int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            
            Object item = myRecord.get(i);
            
            if(item == null) {
                myRecord.put(i, RecordUtils.marshal(field.schema()));
            }
            
            Class cls = myRecord.get(i).getClass();
            
            JLabel label = new JLabel(field.name());
            myConstraints.gridx = 0;
            myConstraints.weightx = 0.0;
            add(label, myConstraints);
            
            myConstraints.gridx = 1;
            myConstraints.weightx = 1.0;
            
            if(IndexedRecord.class.isAssignableFrom(cls)) {
                addEditorPanel(i);
            } else if(List.class.isAssignableFrom(cls)) {
                addList(i);
            } else {
                addEditor(field, i);
            }
            
            myConstraints.gridy++;
        }
        
        revalidate();
    }
    
    private void addEditorPanel(int i) {
        IndexedRecord record = (IndexedRecord)myRecord.get(i);
        EditorPanel panel = new EditorPanel();
        
        panel.setRecord(record);
        add(panel, myConstraints);
    }
    
    private void addList(int i) {
        ListEditorPanel panel = new ListEditorPanel(myRecord, i);
        
        add(panel, myConstraints);
    }
    
    private void addEditor(final Field field, final int i) {
        final JTextField editor = new JTextField(myRecord.get(i).toString());
        
        editor.setDropTarget(null);
        
        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent evt) {
                if(evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    String val = editor.getText();
                    pushValue(val, field, i);
                }
            }
        });
        
        editor.addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent evt) {
                String val = editor.getText();
                pushValue(val, field, i);
            }

            @Override
            public void focusGained(FocusEvent evt) {
            }
        });
        
        add(editor, myConstraints);
    }
    
    private void pushValue(String val, Field field, int i) {
        Class cls = myRecord.get(i).getClass();
        
        try {
            Object parsedVal = parseValue(cls, val);
            myRecord.put(i, parsedVal);
        } catch(Exception ex) {
            String message = "Failed to set " + val + " on " + field.name();
            String longMessage = message + ":\n\n" + ex.getMessage();
            
            JOptionPane.showMessageDialog(
                    this, longMessage, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private Object parseValue(Class cls, String val) {
        if(Integer.class.isAssignableFrom(cls)) {
            return Integer.parseInt(val);
        } else if(Long.class.isAssignableFrom(cls)) {
            return Long.parseLong(val);
        } else if(Short.class.isAssignableFrom(cls)) {
            return Short.parseShort(val);
        } else if(Byte.class.isAssignableFrom(cls)) {
            return Byte.parseByte(val);
        } else if(Float.class.isAssignableFrom(cls)) {
            return Float.parseFloat(val);
        } else if(Double.class.isAssignableFrom(cls)) {
            return Double.parseDouble(val);
        } else if(String.class.isAssignableFrom(cls)) {
            return val;
        } else if(Boolean.class.isAssignableFrom(cls)) {
            return Boolean.parseBoolean(val);
        } else {
            throw new IllegalArgumentException(val);
        }
    }
    
    public IndexedRecord getRecord() {
        return myRecord;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
