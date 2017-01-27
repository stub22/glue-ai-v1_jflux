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

import org.jflux.api.registry.Descriptor;
import org.jflux.api.service.Manager;
import org.jflux.api.service.ServiceManager;
import org.jflux.api.service.binding.ServiceBinding;
import org.jflux.impl.services.rk.lifecycle.DependencyDescriptor;
import org.jflux.impl.services.rk.lifecycle.ManagedService;
import org.jflux.impl.services.rk.osgi.OSGiUtils;
import org.jflux.impl.services.rk.osgi.ServiceClassListener;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.swing.*;

/**
 * @author Matthew Stevenson <www.roboworkshop.org>
 */
public class ManagedServiceListPanel extends JPanel {
	private static final Logger theLogger = LoggerFactory.getLogger(ManagedServiceListPanel.class);
	private Map<Manager, AbstractServicePanel> myPanelMap;
	private ManagedServiceListener myServiceChangeListener;
	private ServiceClassListener<ManagedService> myServiceTracker;
	private ServiceManagerListener myServiceChangeListener2;
	private ServiceClassListener<ServiceManager> myServiceTracker2;
	private List<String> myFilters;
	private Map<Manager, AbstractServicePanel> myFilteredCache;
	private boolean myClassNames;
	private boolean myPropertyKeys;
	private boolean myPropertyValues;
	private boolean myDependencies;
	private String myAvailability;
	private BundleContext context;

	public ManagedServiceListPanel() {
		myServiceChangeListener = new ManagedServiceListener(this);
		myServiceChangeListener2 = new ServiceManagerListener(this);
		myPanelMap = new HashMap<>();
		myAvailability = "Both";

		setFilters("", true, true, true, true, "Both");
	}

	public void setBundleContext(BundleContext context) {
		this.context = context;
		initLayout();
		myServiceTracker = new ServiceClassListener<>(
				ManagedService.class, context, null);
		myServiceTracker.addPropertyChangeListener(
				myServiceChangeListener);
		myServiceTracker.start();


		myServiceTracker2 = new ServiceClassListener<>(
				ServiceManager.class, context, null);
		myServiceTracker2.addPropertyChangeListener(
				myServiceChangeListener2);
		myServiceTracker2.start();
	}

	public void initLayout() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}

	public synchronized void addService(ManagedService service) {
		if (service == null || myPanelMap.containsKey(service)) {
			return;
		}
		AbstractServicePanel panel = new ManagedServicePanel();
		panel.setVisible(false);
		panel.setService(service);
		myPanelMap.put(service, panel);
		add(panel);
		resize(panel.getPreferredSize().height);
		panel.setVisible(true);

		filterCache();
	}

	public synchronized void addService(ServiceManager service) {
		if (service == null || myPanelMap.containsKey(service)) {
			return;
		}
		ServiceManagerPanel panel = new ServiceManagerPanel();
		panel.setVisible(false);
		panel.setService(service);
		panel.setBundleContext(context);
		myPanelMap.put(service, panel);
		add(panel);
		resize(panel.getPreferredSize().height);
		panel.setVisible(true);

		filterCache();
	}

	private void resize(int heightChange) {
		Dimension prefSize = getPreferredSize();
		Dimension newSize =
				new Dimension(prefSize.width, prefSize.height + heightChange);
		//setPreferredSize(newSize);
	}

	public synchronized void removeService(Manager service) {
		if (service == null) {
			return;
		}
		AbstractServicePanel panel = myPanelMap.remove(service);
		if (panel == null) {
			return;
		}
		myPanelMap.remove(service);
		remove(panel);
		resize(-(panel.getPreferredSize().height));
		revalidate();
		repaint();

		refresh();
	}

	public synchronized void clearDependencies() {
		myPanelMap.clear();
		removeAll();
	}

	public synchronized void setFilters(
			String filterStr, boolean classNames, boolean propertyKeys,
			boolean propertyValues, boolean dependencies, String availability) {
		myFilters = new ArrayList<>();
		myClassNames = classNames;
		myPropertyKeys = propertyKeys;
		myPropertyValues = propertyValues;
		myDependencies = dependencies;
		myAvailability = availability;

		String[] filters = filterStr.split(",");
		for (String s : filters) {
			s = s.trim();
			if (!s.isEmpty()) {
				myFilters.add(s);
			}
		}
		filterCache();
	}

	private void filterCache() {
		if (myPanelMap == null) {
			return;
		}
		if (myFilters == null || myFilters.isEmpty()) {
			if (myAvailability.equals("Both")) {
				myFilteredCache = myPanelMap;
				refresh();
				return;
			} else if (myFilters == null) {
				myFilters = new ArrayList<>(1);
				myFilters.add("");
			} else if (myFilters.isEmpty()) {
				myFilters.add("");
			}
		}
		myFilteredCache = new HashMap<>();
		for (Manager thals : myPanelMap.keySet()) {
			if (thals instanceof ManagedService) {
				ManagedService msthals = (ManagedService) thals;
				if (filterList(msthals)) {
					myFilteredCache.put(thals, myPanelMap.get(thals));
				}
			} else if (thals instanceof ServiceManager) {
				ServiceManager smthals = (ServiceManager) thals;
				if (filterList(smthals)) {
					myFilteredCache.put(thals, myPanelMap.get(thals));
				}
			}
		}

		refresh();
	}

	private void refresh() {
		for (Manager ms : myPanelMap.keySet()) {
			if (myFilteredCache.containsKey(ms)) {
				myPanelMap.get(ms).setVisible(true);
			} else {
				myPanelMap.get(ms).setVisible(false);
			}
		}

		updateUI();
	}

	private boolean filterList(ManagedService thals) {
		if (myClassNames) {
			for (String s : thals.getServiceClassNames()) {
				for (String f : myFilters) {
					Pattern p = Pattern.compile(
							".*" + f + ".*",
							Pattern.MULTILINE | Pattern.DOTALL);
					if (s != null && p.matcher(s).matches()) {
						return (myAvailability.equals("Available") &&
								thals.isAvailable()) ||
								(myAvailability.equals("Unavailable") &&
										!thals.isAvailable()) ||
								myAvailability.equals("Both");
					}
				}
			}
		}

		if (myPropertyKeys) {
			for (String s :
					thals.getRegistrationProperties().stringPropertyNames()) {
				for (String f : myFilters) {
					Pattern p = Pattern.compile(
							".*" + f + ".*",
							Pattern.MULTILINE | Pattern.DOTALL);
					if (s != null && p.matcher(s).matches()) {
						return (myAvailability.equals("Available") &&
								thals.isAvailable()) ||
								(myAvailability.equals("Unavailable") &&
										!thals.isAvailable()) ||
								myAvailability.equals("Both");
					}
				}
			}
		}

		if (myPropertyValues) {
			for (String s :
					thals.getRegistrationProperties().stringPropertyNames()) {
				for (String f : myFilters) {
					Pattern p = Pattern.compile(
							".*" + f + ".*",
							Pattern.MULTILINE | Pattern.DOTALL);
					if (thals.getRegistrationProperties().getProperty(s) != null
							&& p.matcher(
							thals.getRegistrationProperties().getProperty(
									s)).matches()) {
						return (myAvailability.equals("Available") &&
								thals.isAvailable()) ||
								(myAvailability.equals("Unavailable") &&
										!thals.isAvailable()) ||
								myAvailability.equals("Both");
					}
				}
			}
		}

		if (myDependencies) {
			for (DependencyDescriptor d :
					(List<DependencyDescriptor>) thals.getDependencies()) {
				for (String f : myFilters) {
					Pattern p = Pattern.compile(
							".*" + f + ".*",
							Pattern.MULTILINE | Pattern.DOTALL);
					if (d.getDependencyName() != null &&
							p.matcher(d.getDependencyName()).matches()) {
						return (myAvailability.equals("Available") &&
								thals.isAvailable()) ||
								(myAvailability.equals("Unavailable") &&
										!thals.isAvailable()) ||
								myAvailability.equals("Both");
					} else if (d.getServiceFilter() != null &&
							p.matcher(d.getServiceFilter()).matches()) {
						return (myAvailability.equals("Available") &&
								thals.isAvailable()) ||
								(myAvailability.equals("Unavailable") &&
										!thals.isAvailable()) ||
								myAvailability.equals("Both");
					}
				}
			}
		}

		return false;
	}

	private boolean filterList(ServiceManager<?> thals) {
		if (myClassNames) {
			for (String s : thals.getLifecycle().getServiceClassNames()) {
				for (String f : myFilters) {
					Pattern p = Pattern.compile(
							".*" + f + ".*",
							Pattern.MULTILINE | Pattern.DOTALL);
					if (s != null && p.matcher(s).matches()) {
						return (myAvailability.equals("Available") &&
								thals.isAvailable()) ||
								(myAvailability.equals("Unavailable") &&
										!thals.isAvailable()) ||
								myAvailability.equals("Both");
					}
				}
			}
		}

		if (myPropertyKeys) {
			for (ServiceBinding sb : thals.getDependencies().keySet()) {
				for (String s : sb.getDescriptor().getPropertyKeys()) {
					for (String f : myFilters) {
						Pattern p = Pattern.compile(
								".*" + f + ".*",
								Pattern.MULTILINE | Pattern.DOTALL);
						if (s != null && p.matcher(s).matches()) {
							return (myAvailability.equals("Available") &&
									thals.isAvailable()) ||
									(myAvailability.equals("Unavailable") &&
											!thals.isAvailable()) ||
									myAvailability.equals("Both");
						}
					}
				}
			}
		}

		if (myPropertyValues) {
			for (ServiceBinding sb : thals.getDependencies().keySet()) {
				Descriptor desc = sb.getDescriptor();
				for (String key : desc.getPropertyKeys()) {
					for (String f : myFilters) {
						String s = desc.getProperty(key);
						Pattern p = Pattern.compile(
								".*" + f + ".*",
								Pattern.MULTILINE | Pattern.DOTALL);
						if (s != null && p.matcher(s).matches()) {
							return (myAvailability.equals("Available") &&
									thals.isAvailable()) ||
									(myAvailability.equals("Unavailable") &&
											!thals.isAvailable()) ||
									myAvailability.equals("Both");
						}
					}
				}
			}
		}

		if (myDependencies) {
			for (ServiceBinding sb : thals.getDependencies().keySet()) {
				for (String f : myFilters) {
					Pattern p = Pattern.compile(
							".*" + f + ".*",
							Pattern.MULTILINE | Pattern.DOTALL);
					Properties props = new Properties();
					Descriptor desc = sb.getDescriptor();
					for (String key : desc.getPropertyKeys()) {
						props.put(key, desc.getProperty(key));
					}
					String filter = OSGiUtils.createServiceFilter(props);
					if (sb.getDependencyName() != null &&
							p.matcher(sb.getDependencyName()).matches()) {
						return (myAvailability.equals("Available") &&
								thals.isAvailable()) ||
								(myAvailability.equals("Unavailable") &&
										!thals.isAvailable()) ||
								myAvailability.equals("Both");
					} else if (filter != null && p.matcher(filter).matches()) {
						return (myAvailability.equals("Available") &&
								thals.isAvailable()) ||
								(myAvailability.equals("Unavailable") &&
										!thals.isAvailable()) ||
								myAvailability.equals("Both");
					}
				}
			}
		}

		return false;
	}

	class ManagedServiceListener implements PropertyChangeListener {
		private Component myComp;

		public ManagedServiceListener(Component comp) {
			myComp = comp;
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt == null) {
				theLogger.warn("Received null property change event.");
				return;
			} else if (evt.getNewValue() == null ||
					!(evt.getNewValue() instanceof ManagedService)) {
				theLogger.warn("Received property change with bad value: {}",
						evt.getPropertyName());
				return;
			}
			if (ServiceClassListener.PROP_SERVICE_ADDED.equals(
					evt.getPropertyName())) {
				addService((ManagedService) evt.getNewValue());
			} else if (ServiceClassListener.PROP_SERVICE_REMOVED.equals(
					evt.getPropertyName())) {
				removeService((ManagedService) evt.getNewValue());
			}
		}
	}

	class ServiceManagerListener implements PropertyChangeListener {
		private Component myComp;

		public ServiceManagerListener(Component comp) {
			myComp = comp;
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt == null) {
				theLogger.warn("Received null property change event.");
				return;
			} else if (evt.getNewValue() == null ||
					!(evt.getNewValue() instanceof ServiceManager)) {
				theLogger.warn("Received property change with bad value: {}",
						evt.getPropertyName());
				return;
			}
			if (ServiceClassListener.PROP_SERVICE_ADDED.equals(
					evt.getPropertyName())) {
				addService((ServiceManager) evt.getNewValue());
			} else if (ServiceClassListener.PROP_SERVICE_REMOVED.equals(
					evt.getPropertyName())) {
				removeService((ServiceManager) evt.getNewValue());
			}
		}
	}
}
