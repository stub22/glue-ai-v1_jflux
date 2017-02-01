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
 * ServiceManagerPanel.java
 *
 */
package org.jflux.swing.services;

import org.jflux.api.registry.Registry;
import org.jflux.api.service.ServiceDependency;
import org.jflux.api.service.ServiceDependency.Cardinality;
import org.jflux.api.service.ServiceManager;
import org.jflux.api.service.binding.DependencyTracker;
import org.jflux.api.service.binding.ServiceBinding;
import org.jflux.impl.registry.OSGiRegistry;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;


/**
 * @author Matthew Stevenson <www.roboworkshop.org>
 */
public class ServiceManagerPanel extends AbstractServicePanel<ServiceManager> {
	private static final Logger theLogger = LoggerFactory.getLogger(ServiceManagerPanel.class);
	private ServiceManager<?> myService;
	private ServiceChangeListener myServiceChangeListener;
	private ArrayList<DepChangeListener> myDepChangeListeners;
	private boolean myPropertiesVisible;
	private boolean myDependenciesVisible;
	private BundleContext context;

	/**
	 * Creates new form ServiceManagerPanel
	 */
	public ServiceManagerPanel() {
		initComponents();
		pnlDependencyList.setLayout(
				new BoxLayout(pnlDependencyList, BoxLayout.Y_AXIS));
		myServiceChangeListener = new ServiceChangeListener();
		myDepChangeListeners = new ArrayList<>();
		myPropertiesVisible = true;
		tblProperties.setTableHeader(null);
		lblType.setOpaque(false);
		jScrollPane2.setOpaque(false);
		jScrollPane2.getViewport().setOpaque(false);
		jScrollPane2.addMouseWheelListener(new InnerScrollPaneWheelListener());
	}

	public void setBundleContext(BundleContext context) {
		this.context = context;
	}

	@Override
	public void setService(ServiceManager service) {
		if (myService == service) {
			updateServiceInfo();
			return;
		}
		if (myService != null) {
			myService.removePropertyChangeListener(myServiceChangeListener);
			Map<ServiceBinding, DependencyTracker> deps = myService.getDependencies();
			for (DependencyTracker tracker : deps.values()) {
				for (DepChangeListener listener : myDepChangeListeners) {
					tracker.removePropertyChangeListener(listener);
				}

			}
		}
		myDepChangeListeners.clear();
		myService = service;
		updateServiceInfo();
		setVals();
		setDependencies();
		if (myService != null) {
			myService.addPropertyChangeListener(myServiceChangeListener);
			Map<ServiceBinding, DependencyTracker> deps = myService.getDependencies();
			if (deps != null) {
				for (DependencyTracker tracker : deps.values()) {
					DepChangeListener tempService = new DepChangeListener(tracker.getDependencyName());
					myDepChangeListeners.add(tempService);
					tracker.addPropertyChangeListener(tempService);
				}
			}
		}
		markRepaint();
	}

	private void updateServiceInfo() {
		updateRegistrationStatus();
		if (myService == null) {
			lblType.setText("--");
			lblDependencyCount.setText("(--)");
			return;
		}
		String names = "";
		for (String name : myService.getLifecycle().getServiceClassNames()) {
			if (!names.isEmpty()) {
				names += "\n";
			}
			names += name;
		}
		lblType.setText(names);
	}

	private void setVals() {
		Map<String, String> props =
				myService.getRegistrationStrategy().getRegistrationProperties(
						null);
		Object[][] objs = new Object[props.size()][2];
		int i = 0;
		for (Entry e : props.entrySet()) {
			objs[i][0] = e.getKey();
			objs[i][1] = e.getValue();
			i++;
		}
		tblProperties.setModel(new DefaultTableModel(
				objs, new String[]{"Property Name", "Value"}));
		int h = tblProperties.getRowCount() *
				(tblProperties.getRowHeight() + tblProperties.getRowMargin());
		Dimension dim = pnlProperties.getSize();
		dim.setSize(dim.width, h);
		Dimension dim2 = tblProperties.getSize();
		Dimension d2 = new Dimension(dim2.width, h);
		//tblProperties.setSize(d2);
		pnlProperties.setPreferredSize(d2);
	}

	private void setDependencies() {
		pnlDependencyList.clearDependencies();
		if (myService != null) {
			for (Entry<ServiceBinding, DependencyTracker> dep :
					myService.getDependencies().entrySet()) {
				Boolean status = isAvailable(dep.getKey(), dep.getValue());
				pnlDependencyList.addDependency(dep.getKey(), status);
			}
		}
		updateDependencyCount();
		//resizeDependencies();
	}

	private boolean isAvailable(
			ServiceBinding binding, DependencyTracker tracker) {
		ServiceDependency.Cardinality c =
				binding.getDependencySpec().getCardinality();

		if (c.isRequired() && tracker.getTrackedDependency() == null) {
			return false;
		}

		return true;
	}

	private void resizeDependencies() {
		Dimension d = pnlDependencyList.getDependenciesSize();
		pnlDependencyList.setPreferredSize(d);
	}

	private void updateDependencyStatus(String dependencyId) {
		if (dependencyId == null) {
			return;
		}
		Boolean status = null;
		Boolean available = null;
		if (myService != null) {
			Map<ServiceBinding, DependencyTracker> depMap =
					myService.getDependencies();
			for (Entry<ServiceBinding, DependencyTracker> e : depMap.entrySet()) {
				ServiceBinding spec = e.getKey();

				if (!spec.getDependencyName().equals(dependencyId)) {
					continue;
				}

				DependencyTracker tracker = e.getValue();
				Cardinality c = spec.getDependencySpec().getCardinality();
				status = !(c.isRequired() && tracker.getTrackedDependency() == null);
				available = tracker.getTrackedDependency() != null;
				break;
			}
		}

		pnlDependencyList.updateDependnecyStatus(dependencyId, status, available);
		updateDependencyCount();
	}

	private void updateDependencyCount() {
		if (myService == null) {
			lblDependencyCount.setText("(--/--)");
			return;
		}
		Map<ServiceBinding, DependencyTracker> depMap =
				myService.getDependencies();
		Set<ServiceBinding> deps = depMap.keySet();

		int total = deps.size();
		int available = 0;

		for (ServiceBinding dep : deps) {
			DependencyTracker tracker = depMap.get(dep);
			if (tracker.getTrackedDependency() != null) {
				available++;
			}
		}

		lblDependencyCount.setText("(" + available + "/" + total + ")");
	}

	private void updateRegistrationStatus() {
		if (myService == null) {
			btnRegister.setEnabled(false);
			btnUnregister.setEnabled(false);
			lblStatus.setText("--");
			return;
		}
		boolean registered = myService.getRegistrationStrategy().isRegistered();
		boolean available = myService.isAvailable();
		if (registered) {
			btnRegister.setEnabled(false);
			btnUnregister.setEnabled(true);
			lblStatus.setText("Available");
		} else if (available) {
			btnRegister.setEnabled(true);
			btnUnregister.setEnabled(false);
			lblStatus.setText("Unregistered");
		} else {
			btnRegister.setEnabled(false);
			btnUnregister.setEnabled(false);
			lblStatus.setText("Unavailable");
		}
	}

	@Override
	public PropertyChangeListener getServiceChangeListener() {
		return myServiceChangeListener;
	}

	class DepChangeListener implements PropertyChangeListener {
		private String propName;

		public DepChangeListener(String name) {
			propName = name;
		}

		public DepChangeListener() {
		}

		@Override
		public synchronized void propertyChange(PropertyChangeEvent evt) {

			updateServiceInfo();

			if (propName != null) {
				updateDependencyStatus(propName);
			}
		}
	}

	class ServiceChangeListener implements PropertyChangeListener {
		@Override
		public synchronized void propertyChange(PropertyChangeEvent evt) {
			if (evt == null) {
				return;
			}
			String name = evt.getPropertyName();
			if (ServiceManager.PROP_SERVICE_CHANGED.equals(name)) {
				setService((ServiceManager) evt.getNewValue());
			} else if (ServiceManager.PROP_DEPENDENCY_CHANGED.equals(name)) {
				Object obj = evt.getNewValue();
				if (!(obj instanceof String)) {
					return;
				}
				updateServiceInfo();
				updateDependencyStatus((String) obj);
			}
		}
	}

	private void markRepaint() {
		RepaintManager.currentManager(this).markCompletelyDirty(this);
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		pnlTitle = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		lblStatus = new javax.swing.JLabel();
		jScrollPane2 = new javax.swing.JScrollPane();
		lblType = new javax.swing.JTextArea();
		pnlCollapsable = new javax.swing.JPanel();
		pnlDepsCollapse = new javax.swing.JPanel();
		lblDependencies = new javax.swing.JLabel();
		lblDependencyCount = new javax.swing.JLabel();
		pnlDependencyList = new org.jflux.swing.services.ManagerDependencyListPanel();
		pnlPropsCollapse = new javax.swing.JPanel();
		lblRegistrationProperties = new javax.swing.JLabel();
		pnlProperties = new javax.swing.JPanel();
		tblProperties = new javax.swing.JTable();
		pnlButtons = new javax.swing.JPanel();
		btnUnregister = new javax.swing.JButton();
		btnRegister = new javax.swing.JButton();
		btnDispose = new javax.swing.JButton();

		jLabel1.setText("Service Type:");

		lblStatus.setText("--");

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

		javax.swing.GroupLayout pnlTitleLayout = new javax.swing.GroupLayout(pnlTitle);
		pnlTitle.setLayout(pnlTitleLayout);
		pnlTitleLayout.setHorizontalGroup(
				pnlTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(pnlTitleLayout.createSequentialGroup()
								.addComponent(jLabel1)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(lblStatus)
								.addContainerGap())
		);
		pnlTitleLayout.setVerticalGroup(
				pnlTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 21, Short.MAX_VALUE)
						.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(lblStatus, javax.swing.GroupLayout.Alignment.TRAILING)
								.addComponent(jLabel1))
		);

		pnlDepsCollapse.setBorder(javax.swing.BorderFactory.createEtchedBorder());

		lblDependencies.setText("[-]Dependencies");
		lblDependencies.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				lblDependenciesMouseClicked(evt);
			}
		});

		lblDependencyCount.setText("(--/--)");
		lblDependencyCount.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				lblDependencyCountMouseClicked(evt);
			}
		});

		pnlDependencyList.setMaximumSize(new java.awt.Dimension(175, 600));

		javax.swing.GroupLayout pnlDependencyListLayout = new javax.swing.GroupLayout(pnlDependencyList);
		pnlDependencyList.setLayout(pnlDependencyListLayout);
		pnlDependencyListLayout.setHorizontalGroup(
				pnlDependencyListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGap(0, 175, Short.MAX_VALUE)
		);
		pnlDependencyListLayout.setVerticalGroup(
				pnlDependencyListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGap(0, 0, Short.MAX_VALUE)
		);

		javax.swing.GroupLayout pnlDepsCollapseLayout = new javax.swing.GroupLayout(pnlDepsCollapse);
		pnlDepsCollapse.setLayout(pnlDepsCollapseLayout);
		pnlDepsCollapseLayout.setHorizontalGroup(
				pnlDepsCollapseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(pnlDepsCollapseLayout.createSequentialGroup()
								.addComponent(lblDependencies)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(lblDependencyCount)
								.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addComponent(pnlDependencyList, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		);
		pnlDepsCollapseLayout.setVerticalGroup(
				pnlDepsCollapseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(pnlDepsCollapseLayout.createSequentialGroup()
								.addGroup(pnlDepsCollapseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(lblDependencies)
										.addComponent(lblDependencyCount))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(pnlDependencyList, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);

		pnlPropsCollapse.setBorder(javax.swing.BorderFactory.createEtchedBorder());

		lblRegistrationProperties.setText("[-]Registration Properties");
		lblRegistrationProperties.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				lblRegistrationPropertiesMouseClicked(evt);
			}
		});

		tblProperties.setModel(new javax.swing.table.DefaultTableModel(
				new Object[][]{
						{null, null}
				},
				new String[]{
						"Property Name", "Value"
				}
		) {
			Class[] types = new Class[]{
					java.lang.String.class, java.lang.String.class
			};
			boolean[] canEdit = new boolean[]{
					false, false
			};

			public Class getColumnClass(int columnIndex) {
				return types[columnIndex];
			}

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return canEdit[columnIndex];
			}
		});
		tblProperties.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
		tblProperties.setCellSelectionEnabled(true);
		tblProperties.setFillsViewportHeight(true);
		tblProperties.setMinimumSize(new java.awt.Dimension(0, 0));
		tblProperties.getTableHeader().setReorderingAllowed(false);

		javax.swing.GroupLayout pnlPropertiesLayout = new javax.swing.GroupLayout(pnlProperties);
		pnlProperties.setLayout(pnlPropertiesLayout);
		pnlPropertiesLayout.setHorizontalGroup(
				pnlPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(tblProperties, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
		);
		pnlPropertiesLayout.setVerticalGroup(
				pnlPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(tblProperties, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
		);

		javax.swing.GroupLayout pnlPropsCollapseLayout = new javax.swing.GroupLayout(pnlPropsCollapse);
		pnlPropsCollapse.setLayout(pnlPropsCollapseLayout);
		pnlPropsCollapseLayout.setHorizontalGroup(
				pnlPropsCollapseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(lblRegistrationProperties, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(pnlProperties, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		);
		pnlPropsCollapseLayout.setVerticalGroup(
				pnlPropsCollapseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(pnlPropsCollapseLayout.createSequentialGroup()
								.addComponent(lblRegistrationProperties)
								.addGap(8, 8, 8)
								.addComponent(pnlProperties, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
		);

		javax.swing.GroupLayout pnlCollapsableLayout = new javax.swing.GroupLayout(pnlCollapsable);
		pnlCollapsable.setLayout(pnlCollapsableLayout);
		pnlCollapsableLayout.setHorizontalGroup(
				pnlCollapsableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(pnlPropsCollapse, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(pnlDepsCollapse, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		);
		pnlCollapsableLayout.setVerticalGroup(
				pnlCollapsableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(pnlCollapsableLayout.createSequentialGroup()
								.addComponent(pnlPropsCollapse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(pnlDepsCollapse, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);

		btnUnregister.setText("Unregister");
		btnUnregister.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnUnregisterActionPerformed(evt);
			}
		});

		btnRegister.setText("Register");
		btnRegister.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnRegisterActionPerformed(evt);
			}
		});

		btnDispose.setText("Dispose");
		btnDispose.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnDisposeActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout pnlButtonsLayout = new javax.swing.GroupLayout(pnlButtons);
		pnlButtons.setLayout(pnlButtonsLayout);
		pnlButtonsLayout.setHorizontalGroup(
				pnlButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(pnlButtonsLayout.createSequentialGroup()
								.addContainerGap()
								.addGroup(pnlButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(btnRegister, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(btnUnregister, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(btnDispose, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addContainerGap())
		);
		pnlButtonsLayout.setVerticalGroup(
				pnlButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(pnlButtonsLayout.createSequentialGroup()
								.addComponent(btnRegister)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(btnUnregister)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(btnDispose))
		);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
												.addGap(12, 12, 12)
												.addComponent(pnlCollapsable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(pnlButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
										.addComponent(pnlTitle, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
		);
		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
								.addComponent(pnlTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(pnlCollapsable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(pnlButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
								.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
	}// </editor-fold>//GEN-END:initComponents

	private void btnUnregisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUnregisterActionPerformed

		if (myService == null) {
			btnUnregister.setEnabled(false);
			btnRegister.setEnabled(false);
			return;
		}

		myService.stop();
		updateServiceInfo();
		btnUnregister.setEnabled(false);
		btnRegister.setEnabled(true);

//        myService.setRegistrationEnabled(false);
	}//GEN-LAST:event_btnUnregisterActionPerformed

	private void btnRegisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegisterActionPerformed
		if (myService == null) {
			btnUnregister.setEnabled(false);
			btnRegister.setEnabled(false);
			return;
		}
//        myService.setRegistrationEnabled(true);
		Registry registry = new OSGiRegistry(context);
		myService.start(registry);
		updateServiceInfo();
		btnUnregister.setEnabled(true);
		btnRegister.setEnabled(false);

	}//GEN-LAST:event_btnRegisterActionPerformed

	private void lblRegistrationPropertiesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblRegistrationPropertiesMouseClicked
		if (myPropertiesVisible) {
			lblRegistrationProperties.setText("[+]Registration Properties");
			myPropertiesVisible = false;
			pnlProperties.setVisible(false);
		} else {
			lblRegistrationProperties.setText("[-]Registration Properties");
			myPropertiesVisible = true;
			pnlProperties.setVisible(true);
		}
	}//GEN-LAST:event_lblRegistrationPropertiesMouseClicked

	private void lblDependenciesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblDependenciesMouseClicked
		toggleDependenciesVisible();
	}//GEN-LAST:event_lblDependenciesMouseClicked

	private void lblDependencyCountMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblDependencyCountMouseClicked
		toggleDependenciesVisible();
	}//GEN-LAST:event_lblDependencyCountMouseClicked

	private void btnDisposeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDisposeActionPerformed
		disposeService();
	}//GEN-LAST:event_btnDisposeActionPerformed

	private void toggleDependenciesVisible() {
		if (myDependenciesVisible) {
			lblDependencies.setText("[+]Dependencies");
			myDependenciesVisible = false;
			pnlDependencyList.setVisible(false);
		} else {
			lblDependencies.setText("[-]Dependencies");
			myDependenciesVisible = true;
			pnlDependencyList.setVisible(true);
		}
	}

	private void disposeService() {
		myService.dispose();
		updateServiceInfo();
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton btnDispose;
	private javax.swing.JButton btnRegister;
	private javax.swing.JButton btnUnregister;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JLabel lblDependencies;
	private javax.swing.JLabel lblDependencyCount;
	private javax.swing.JLabel lblRegistrationProperties;
	private javax.swing.JLabel lblStatus;
	private javax.swing.JTextArea lblType;
	private javax.swing.JPanel pnlButtons;
	private javax.swing.JPanel pnlCollapsable;
	private org.jflux.swing.services.ManagerDependencyListPanel pnlDependencyList;
	private javax.swing.JPanel pnlDepsCollapse;
	private javax.swing.JPanel pnlProperties;
	private javax.swing.JPanel pnlPropsCollapse;
	private javax.swing.JPanel pnlTitle;
	private javax.swing.JTable tblProperties;
	// End of variables declaration//GEN-END:variables
}
