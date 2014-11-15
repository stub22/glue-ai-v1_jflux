/*
 * Copyright 2014 the RoboWorkshop Project
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
 * DependencyPanel.java
 *
 * Created on Feb 13, 2012, 10:59:32 PM
 */
package org.jflux.swing.services;

import java.awt.Color;
import java.util.Properties;
import org.jflux.api.registry.Descriptor;
import org.jflux.api.service.binding.ServiceBinding;
import org.jflux.impl.services.rk.osgi.OSGiUtils;

/**
 *
 * @author Matthew Stevenson <www.roboworkshop.org>
 */
public class ManagerDependencyPanel extends javax.swing.JPanel {
    private final static String theNullStatus = "--";
    private final static String theAvailableStatus = "Available";
    private final static String theUnavailableStatus = "Unavailable";
    private final static Color theNullColor = new Color(225, 225, 160);
    private final static Color theAvailableColor = new Color(170, 221, 170);
    private final static Color theUnavailableColor = new Color(213, 132, 132);
    private ServiceBinding myDependency;
    private Boolean myStatus;
    
    /** Creates new form DependencyPanel */
    public ManagerDependencyPanel() {
        initComponents();
        lblFilter.setOpaque(false);
        jScrollPane1.setOpaque(false);
        jScrollPane1.getViewport().setOpaque(false);
        jScrollPane1.addMouseWheelListener(new InnerScrollPaneWheelListener());
        lblType.setOpaque(false);
        jScrollPane2.setOpaque(false);
        jScrollPane2.getViewport().setOpaque(false);
        jScrollPane2.addMouseWheelListener(new InnerScrollPaneWheelListener());
        updateDisplay(null);
        changeColor(null);
    }

    public void setDependency(ServiceBinding dependency, Boolean status){
        myDependency = dependency;
        updateDisplay(status);
    }
    
    public void updateDisplay(Boolean status){
        updateStatus(status);
        if(myDependency == null){
            lblName.setText("--");
            lblFilter.setText("--");
            lblType.setText("--");
            return;
        }
        String name = myDependency.getDependencyName();
        Descriptor desc = myDependency.getDescriptor();
        String type = desc.getClassName();
        Properties props = new Properties();
        
        for(String key: desc.getPropertyKeys()){
            props.put(key, desc.getProperty(key));
        }
        
        String filter = OSGiUtils.createServiceFilter(props);
        
        name = name == null ? "--" : name;
        type = type == null ? "--" : type;
        filter = filter == null ? "--" : filter;
        lblName.setText(name);
        lblFilter.setText(filter);
        lblType.setText(type);
    }
    
    public void updateStatus(Boolean status){
        if(myStatus == status){
            return;
        }
        changeColor(status);
    }
    
    private void changeColor(Boolean status){
        String text;
        Color bg;
        myStatus = status;
        if(status == null){
            text = theNullStatus;
            bg = theNullColor;
        }else if(status){
            text = theAvailableStatus;
            bg = theAvailableColor;
        }else{
            text = theUnavailableStatus;
            bg = theUnavailableColor;
        }
        lblStatus.setText(text);
        setBackground(bg);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlMain = new javax.swing.JPanel();
        pnlTitle = new javax.swing.JPanel();
        pnlName = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lblName = new javax.swing.JLabel();
        pnlStatus = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        pnlBody = new javax.swing.JPanel();
        pnlType = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lblType = new javax.swing.JTextArea();
        pnlFilter = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lblFilter = new javax.swing.JTextArea();

        setBackground(new java.awt.Color(225, 224, 159));
        setMaximumSize(new java.awt.Dimension(32767, 78));
        setPreferredSize(new java.awt.Dimension(343, 69));

        pnlMain.setOpaque(false);
        pnlMain.setPreferredSize(new java.awt.Dimension(343, 69));

        pnlTitle.setOpaque(false);

        pnlName.setOpaque(false);

        jLabel1.setText("Name:");

        lblName.setText("--");

        javax.swing.GroupLayout pnlNameLayout = new javax.swing.GroupLayout(pnlName);
        pnlName.setLayout(pnlNameLayout);
        pnlNameLayout.setHorizontalGroup(
            pnlNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNameLayout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblName))
        );
        pnlNameLayout.setVerticalGroup(
            pnlNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel1)
                .addComponent(lblName))
        );

        pnlStatus.setOpaque(false);

        jLabel3.setText("Status:");

        lblStatus.setText("--");

        javax.swing.GroupLayout pnlStatusLayout = new javax.swing.GroupLayout(pnlStatus);
        pnlStatus.setLayout(pnlStatusLayout);
        pnlStatusLayout.setHorizontalGroup(
            pnlStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlStatusLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStatus)
                .addContainerGap())
        );
        pnlStatusLayout.setVerticalGroup(
            pnlStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(lblStatus)
                .addComponent(jLabel3))
        );

        javax.swing.GroupLayout pnlTitleLayout = new javax.swing.GroupLayout(pnlTitle);
        pnlTitle.setLayout(pnlTitleLayout);
        pnlTitleLayout.setHorizontalGroup(
            pnlTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTitleLayout.createSequentialGroup()
                .addComponent(pnlName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 190, Short.MAX_VALUE)
                .addComponent(pnlStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnlTitleLayout.setVerticalGroup(
            pnlTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlStatus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pnlBody.setOpaque(false);

        pnlType.setOpaque(false);

        jLabel2.setText("Type:");
        jLabel2.setMaximumSize(new java.awt.Dimension(45, 18));
        jLabel2.setMinimumSize(new java.awt.Dimension(45, 18));
        jLabel2.setPreferredSize(new java.awt.Dimension(45, 18));

        jScrollPane2.setBorder(null);
        jScrollPane2.setOpaque(false);

        lblType.setColumns(20);
        lblType.setEditable(false);
        lblType.setLineWrap(true);
        lblType.setRows(1);
        lblType.setWrapStyleWord(true);
        lblType.setBorder(null);
        lblType.setOpaque(false);
        jScrollPane2.setViewportView(lblType);

        javax.swing.GroupLayout pnlTypeLayout = new javax.swing.GroupLayout(pnlType);
        pnlType.setLayout(pnlTypeLayout);
        pnlTypeLayout.setHorizontalGroup(
            pnlTypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTypeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlTypeLayout.setVerticalGroup(
            pnlTypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 21, Short.MAX_VALUE)
        );

        pnlFilter.setOpaque(false);

        jLabel4.setText("Filter:");
        jLabel4.setMaximumSize(new java.awt.Dimension(45, 18));
        jLabel4.setMinimumSize(new java.awt.Dimension(45, 18));
        jLabel4.setPreferredSize(new java.awt.Dimension(45, 18));

        jScrollPane1.setBorder(null);
        jScrollPane1.setMinimumSize(new java.awt.Dimension(21, 0));
        jScrollPane1.setOpaque(false);

        lblFilter.setColumns(20);
        lblFilter.setEditable(false);
        lblFilter.setLineWrap(true);
        lblFilter.setRows(1);
        lblFilter.setWrapStyleWord(true);
        lblFilter.setBorder(null);
        lblFilter.setOpaque(false);
        jScrollPane1.setViewportView(lblFilter);

        javax.swing.GroupLayout pnlFilterLayout = new javax.swing.GroupLayout(pnlFilter);
        pnlFilter.setLayout(pnlFilterLayout);
        pnlFilterLayout.setHorizontalGroup(
            pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFilterLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlFilterLayout.setVerticalGroup(
            pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFilterLayout.createSequentialGroup()
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout pnlBodyLayout = new javax.swing.GroupLayout(pnlBody);
        pnlBody.setLayout(pnlBodyLayout);
        pnlBodyLayout.setHorizontalGroup(
            pnlBodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlType, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlFilter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnlBodyLayout.setVerticalGroup(
            pnlBodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBodyLayout.createSequentialGroup()
                .addComponent(pnlType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlFilter, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlMainLayout = new javax.swing.GroupLayout(pnlMain);
        pnlMain.setLayout(pnlMainLayout);
        pnlMainLayout.setHorizontalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlBody, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnlMainLayout.setVerticalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMainLayout.createSequentialGroup()
                .addComponent(pnlTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlBody, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlMain, javax.swing.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea lblFilter;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTextArea lblType;
    private javax.swing.JPanel pnlBody;
    private javax.swing.JPanel pnlFilter;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlName;
    private javax.swing.JPanel pnlStatus;
    private javax.swing.JPanel pnlTitle;
    private javax.swing.JPanel pnlType;
    // End of variables declaration//GEN-END:variables
}
