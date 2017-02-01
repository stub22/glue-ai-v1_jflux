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
package org.jflux.swing.services;

import java.awt.Container;

import org.osgi.framework.BundleContext;

/**
 *
 * @author Matthew Stevenson <www.roboworkshop.org>
 */
public class ServicesFrame extends javax.swing.JFrame {

	static int serviceFramesCount = 0;

	public static void create(final BundleContext context) {
		java.awt.EventQueue.invokeLater(new Runnable() {

			@Override public void run() {
				ServicesFrame sf = new ServicesFrame();
				if (context != null) sf.setBundleContext(context);
				sf.setVisible(true);
			}
		});
	}
	int serviceFrameNum = 0;


	private boolean isOperational() {
		return serviceFrameNum == 0;
	}

	@Override public void setVisible(boolean b) {
		if (!isOperational())
			return;
		super.setVisible(b);
	}

    /**
     * Creates new form ServicesFrame
     */
    public ServicesFrame() {
		serviceFrameNum = serviceFramesCount;
		serviceFramesCount++;
		if (isOperational()) {
        initComponents();
        recenterServicesPanel();
    }
	}

    /**
     * Matisse Forms Editor forgets about "Center" and uses "First"
	 * this restores the scrollbars
     */
    private void recenterServicesPanel() {
        Container conts = getContentPane();
		conts.remove(servicesPanel1);
		conts.add(servicesPanel1, java.awt.BorderLayout.CENTER);
    }

    public void setBundleContext(BundleContext context){
		if (!isOperational())
			return;
        servicesPanel1.setBundleContext(context);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        servicesPanel1 = new org.jflux.swing.services.ServicesPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Service Manager");
        getContentPane().add(servicesPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            org.slf4j.LoggerFactory.getLogger(ServicesFrame.class).error(ex.getMessage(), ex);
        } catch (InstantiationException ex) {
            org.slf4j.LoggerFactory.getLogger(ServicesFrame.class).error(ex.getMessage(), ex);
        } catch (IllegalAccessException ex) {
            org.slf4j.LoggerFactory.getLogger(ServicesFrame.class).error(ex.getMessage(), ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            org.slf4j.LoggerFactory.getLogger(ServicesFrame.class).error(ex.getMessage(), ex);
        }
        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
				ServicesFrame sf = new ServicesFrame();
				sf.setDefaultCloseOperation(EXIT_ON_CLOSE);
				sf.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jflux.swing.services.ServicesPanel servicesPanel1;
    // End of variables declaration//GEN-END:variables
}
