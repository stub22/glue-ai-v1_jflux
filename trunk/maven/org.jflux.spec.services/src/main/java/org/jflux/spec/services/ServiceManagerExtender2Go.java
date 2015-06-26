/*
 * Copyright 2013 The Friendularity Project (www.friendularity.org).
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
package org.jflux.spec.services;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jflux.api.registry.Registry;
import org.jflux.api.service.DefaultRegistrationStrategy;
import org.jflux.api.service.RegistrationStrategy;
import org.jflux.api.service.ServiceDependency;
import org.jflux.api.service.ServiceLifecycle;
import org.jflux.api.service.ServiceManager;
import org.jflux.api.service.binding.ServiceBinding;
import org.osgi.framework.BundleContext;
import org.jflux.impl.services.rk.osgi.ServiceClassListener;
import org.jflux.onto.common.osgi.OSGiServPropBinding;
import org.jflux.spec.services.rdf2go.SMEManager;
import org.jflux.spec.services.rdf2go.SMELifecycle;
import org.jflux.spec.services.rdf2go.SMEBinding;
import org.jflux.spec.services.rdf2go.SMEDependency;
import org.jflux.spec.services.rdf2go.SMSSvcRegStrategy;
import org.jflux.spec.services.rdf2go.SMSBindStrategy;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.apache.jena.riot.RDFDataMgr;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.node.Resource;
import org.jflux.api.registry.basic.BasicDescriptor;
import org.jflux.api.service.binding.ServiceBinding.BindingStrategy;
import org.jflux.spec.services.jvocab.ServiceManagement_OWL2;
import java.util.HashMap;

/**
 *
 * @author Major Jacquote II
 */
public class ServiceManagerExtender2Go
		extends ServiceClassListener<SMEManager> {

	/**
	 * Stores the managed connections for later removal
	 */
	private final Map<SMEManager, ServiceManager> myManagedServicesMap;
	/**
	 * Context reference for interacting with JFlux
	 */
	private final Registry myRegistry;
	private final Model model2go;

	/**
	 * The logger, used for reporting errors.
	 */
	private final static Logger theLogger
			= Logger.getLogger(ServiceManagerExtender2Go.class.getName());
	
	private static final URIImpl URI_BS_EAGER = new URIImpl(ServiceManagement_OWL2.BIND_STRAT_EAGER.getURI(), false);
	private final Map<URIImpl,ServiceBinding.BindingStrategy> theStrategiesByR2goURI;
						
							
	public ServiceManagerExtender2Go(
			BundleContext context, Registry registry, String serviceFilter) {
		super(SMEManager.class, context, serviceFilter);
		myRegistry = registry;
		myManagedServicesMap
				= new HashMap<SMEManager, ServiceManager>();
		
		theStrategiesByR2goURI= new HashMap<URIImpl,ServiceBinding.BindingStrategy>();
		theStrategiesByR2goURI.put(URI_BS_EAGER, ServiceBinding.BindingStrategy.EAGER);
		
		com.hp.hpl.jena.rdf.model.Model jenaModel = RDFDataMgr.loadModel("../src/main/resources/org/jflux/spec/services/ServiceTest.ttl");
		model2go = new org.ontoware.rdf2go.impl.jena.ModelImplJena(jenaModel);
		model2go.open();
	}

	/**
	 * Creates and starts a service manager.
	 *
	 * @param serviceManagerEntity data object used to generate connection
	 */
	@Override
	protected void addService(SMEManager serviceManagerEntity) {
		ServiceLifecycle lifecycle;
		Map<String, ServiceBinding> bindings
				= new HashMap<String, ServiceBinding>();

		if (serviceManagerEntity == null
				|| myManagedServicesMap.containsKey(serviceManagerEntity)) {
			return;
		}

		String lifecycleFQCN = getLifecyceleFCQN(serviceManagerEntity);
		try {
			Class lifecycleClass
					= Class.forName(lifecycleFQCN);
			lifecycle = (ServiceLifecycle) lifecycleClass.newInstance();
		} catch (Exception e) {
			theLogger.log(
					Level.SEVERE, "Unable to instantiate class: {0}",
					lifecycleFQCN);
			return;
		}

		for (Entry<String, SMEBinding> specItem
				: getBindingMapForServiceManager(serviceManagerEntity).entrySet()) {
			SMEBinding sbt = specItem.getValue();
			SMEDependency sme_dep = getDependencyEntity(sbt);
			ServiceDependency dep = getDep(sme_dep.getLocalName(), lifecycle);

			if (dep == null) {
				theLogger.log(Level.SEVERE, "Dependency with name: {0} was not found.", sme_dep.getLocalName());
				continue;
			}

			
			ServiceBinding binding
					= new ServiceBinding(
							dep, getDescriptor(sbt), getBindingStrat(sbt));
			bindings.put(specItem.getKey(), binding);
		}

		RegistrationStrategy strat = getRegStrat(serviceManagerEntity);

		ServiceManager serviceManager
				= new ServiceManager(lifecycle, bindings, strat, null);
		// Start the service manager
		serviceManager.start(myRegistry);
		// Store the service manager so it may be removed later.
		myManagedServicesMap.put(serviceManagerEntity, serviceManager);
	}

	private ServiceDependency getDep(String name, ServiceLifecycle<?> l) {
		for (ServiceDependency d : l.getDependencySpecs()) {
			if (d.getDependencyName().equals(name)) {
				return d;
			}
		}
		return null;
	}

	/**
	 * Stops a service manager.
	 *
	 * @param serviceManagerEntity data object used to generate connection
	 */
	@Override
	protected void removeService(SMEManager serviceManagerEntity) {
		if (serviceManagerEntity == null
				|| !myManagedServicesMap.containsKey(serviceManagerEntity)) {
			return;
		}
		ServiceManager manager
				= myManagedServicesMap.remove(serviceManagerEntity);
		if (manager != null) {
			manager.stop();
		}
	}

	private Map<String, SMEBinding> getBindingMapForServiceManager(SMEManager serviceManagerEntity) {
		ClosableIterator iter = serviceManagerEntity.getAllSvcBinding();
		Map<String, SMEBinding> bindings = new HashMap<String, SMEBinding>();

		while (iter.hasNext()) {
			Resource obj = (Resource) iter.next();

			SMEBinding binding = SMEBinding.getInstance(model2go, obj.asURI());
			SMEDependency dep = binding.getSvcBindDep();
			String depName = dep.getFullyQualifiedJClzName();

			bindings.put(depName, binding);

		}

		return bindings;
	}

	private SMEDependency getDependencyEntity(SMEBinding sbt) {

		return sbt.getSvcBindDep();
	}

	private BasicDescriptor getDescriptor(SMEBinding sbt) {
		ClosableIterator iter = sbt.getAllOSGiServPropBinding();
		Map<String, String> properties = getPropertyMap(iter);

		BasicDescriptor desc = new BasicDescriptor(sbt.getFullyQualifiedJClzName(), properties);

		return desc;
	}

	private RegistrationStrategy getRegStrat(SMEManager serviceManagerEntity) {
		SMSSvcRegStrategy regStrat = serviceManagerEntity.getRegStrategy();
		List<String> classNames = new ArrayList<String>();
		ClosableIterator iter = serviceManagerEntity.getAllOSGiServPropBinding();
		Map<String, String> properties = getPropertyMap(iter);

		classNames.add(regStrat.getFullyQualifiedJClzName());

		RegistrationStrategy strat
				= new DefaultRegistrationStrategy(classNames.toArray(new String[classNames.size()]), properties);

		return strat;
	}

	private BindingStrategy getBindingStrat(SMEBinding sbt) {
		SMSBindStrategy sbst = sbt.getBindStrategy();
		SMSBindStrategy indv = new SMSBindStrategy(model2go,yieldR2GoURI(ServiceManagement_OWL2.BIND_STRAT_EAGER), false );
		
		if (sbst.equals(indv)) {
			return ServiceBinding.BindingStrategy.EAGER;
		} else {
			return ServiceBinding.BindingStrategy.LAZY;
		}
	}

	private String getLifecyceleFCQN(SMEManager serviceManagerEntity) {
		SMELifecycle lifecycleEntity = serviceManagerEntity.getStartsLifecycle();

		if (lifecycleEntity.hasFullyQualifiedJClzName()) {
			return lifecycleEntity.getFullyQualifiedJClzName();
		} else {
			return null;
		}
	}

	private Map<String, String> getPropertyMap(ClosableIterator iter) {
		Map<String, String> properties = new HashMap<String, String>();

		while (iter.hasNext()) {
			Resource obj = (Resource) iter.next();
			OSGiServPropBinding ps = OSGiServPropBinding.getInstance(model2go, obj.asURI());
			properties.put(ps.getServPropKey().getServPropKeyName(), ps.getServPropValue());
		}

		return properties;
	}
	
	
	//TODO: Replace with a
	private URI yieldR2GoURI(com.hp.hpl.jena.rdf.model.Resource id) {
		
		return new URIImpl(id.getURI());
	}
}
