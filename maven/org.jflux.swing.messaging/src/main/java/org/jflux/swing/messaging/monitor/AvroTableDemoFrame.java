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
 * AvroTableDemoFrame.java
 *
 * Created on Mar 6, 2013, 3:22:07 PM
 */
package org.jflux.swing.messaging.monitor;

import org.jflux.swing.messaging.OSGiSchemaSelector;
import org.osgi.framework.BundleContext;

/**
 *
 * @author Matthew Stevenson <www.robokind.org>
 */
public class AvroTableDemoFrame extends javax.swing.JFrame {
    private AvroQpidConnector myConnector;
    private OSGiSchemaSelector mySelector;

    /** Creates new form AvroTableDemoFrame */
    public AvroTableDemoFrame() {
        initComponents();
        pack();
        myConnector = new AvroQpidConnector();
    }
    
    public void start(BundleContext context){
        mySelector = new OSGiSchemaSelector(context);
        mySelector.start();
        myConnector.addListener(avroTablePanel1);
        avroQpidConnectionPanel1.setConnector(myConnector);
        avroQpidConnectionPanel1.setSchemaSelector(mySelector);
        avroQpidConnectionPanel1.setAvroTable(avroTablePanel1);
        avroQpidConnectionPanel1.setSavePanel(savePanel1);
        avroQpidConnectionPanel1.setContext(context);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        avroTablePanel1 = new org.jflux.swing.messaging.monitor.AvroTablePanel();
        avroQpidConnectionPanel1 = new org.jflux.swing.messaging.monitor.AvroQpidConnectionPanel();
        savePanel1 = new org.jflux.swing.messaging.monitor.SavePanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Message Monitor");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(avroQpidConnectionPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 423, Short.MAX_VALUE)
            .addComponent(avroTablePanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 423, Short.MAX_VALUE)
            .addComponent(savePanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(avroQpidConnectionPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(avroTablePanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(savePanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            org.slf4j.LoggerFactory.getLogger(AvroTableDemoFrame.class).error(ex.getMessage(), ex);
        } catch (InstantiationException ex) {
            org.slf4j.LoggerFactory.getLogger(AvroTableDemoFrame.class).error(ex.getMessage(), ex);
        } catch (IllegalAccessException ex) {
            org.slf4j.LoggerFactory.getLogger(AvroTableDemoFrame.class).error(ex.getMessage(), ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            org.slf4j.LoggerFactory.getLogger(AvroTableDemoFrame.class).error(ex.getMessage(), ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new AvroTableDemoFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jflux.swing.messaging.monitor.AvroQpidConnectionPanel avroQpidConnectionPanel1;
    private org.jflux.swing.messaging.monitor.AvroTablePanel avroTablePanel1;
    private org.jflux.swing.messaging.monitor.SavePanel savePanel1;
    // End of variables declaration//GEN-END:variables
}
