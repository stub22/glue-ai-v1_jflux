/*
 * Copyright 2012 Hanson Robokind LLC.
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
package org.jflux.impl.services.rk.lifecycle;

import org.jflux.impl.services.rk.property.PropertyChangeNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Abstract implementation of ServiceLifecycleProvider.  Automatically validates
 * dependencies against the descriptors provided at creation.
 *
 * @param <I> Interface or Base Class of the service managed by this lifecycle
 * @param <T> actual type of the service managed
 * @author Matthew Stevenson <www.robokind.org>
 */
public abstract class AbstractLifecycleProvider<I, T extends I> extends
		PropertyChangeNotifier implements ServiceLifecycleProvider<I> {
	private static final Logger theLogger = LoggerFactory.getLogger(AbstractLifecycleProvider.class);

	private List<DependencyDescriptor> myDescriptors;
	private List<DependencyDescriptor> myRuntimeDescriptors;
	private boolean myStaisfiedFlag;
	/**
	 * Instance of the service being managed
	 */
	protected T myService;
	/**
	 * OSGi registration properties
	 */
	protected Properties myRegistrationProperties;
	/**
	 * Names of the classes provided by this service.
	 */
	protected String[] myServiceClassNames;

	/**
	 * Creates a new AbstractLifecycleProvider with the given
	 * DependencyDescriptors.
	 *
	 * @param deps descriptors of the dependencies for this lifecycle
	 */
	public AbstractLifecycleProvider(List<DependencyDescriptor> deps) {
		if (deps == null) {
			throw new NullPointerException();
		}
		myStaisfiedFlag = false;
		myDescriptors = deps;
	}

	@Override
	public synchronized void start(Map<String, Object> dependencies) {
		if (!Validator.validateServices(myDescriptors, dependencies)) {
			throw new IllegalArgumentException(
					"Invalid Dependency set for factory.");
		}
		myService = create(dependencies);
		myStaisfiedFlag = true;
	}

	@Override
	public synchronized void stop() {

	}

	/**
	 * Called when all dependencies are available.  This should create and
	 * return the service for this lifecycle.
	 *
	 * @param dependencies validated map of the lifecycle's dependencies
	 * @return the service managed by this lifecycle, null if the service cannot be created
	 */
	protected abstract T create(Map<String, Object> dependencies);

	@Override
	public synchronized T getService() {
		if (!isSatisfied()) {
			return null;
		}
		return myService;
	}

	@Override
	public Properties getRegistrationProperties() {
		return myRegistrationProperties;
	}

	@Override
	public List<DependencyDescriptor> getDependencyDescriptors() {
		return myDescriptors;
	}

	public boolean isSatisfied() {
		return myStaisfiedFlag;
	}

	/**
	 * Validates the arguments before calling <code>handleChange</code>.
	 * If the service is null and the dependencies are satisfied, a service is
	 * created and <code>handleChange</code> is not called.
	 *
	 * @param dependencyName name of the dependency changed
	 * @param dependency     new dependency
	 * @param dependencies   all available dependencies
	 */
	@Override
	public synchronized void dependencyChanged(
			String dependencyName, Object dependency,
			Map<String, Object> dependencies) {
		if (dependencyName == null) {
			throw new NullPointerException();
		}
		if (!validate(dependencyName, dependency)) {
			return;
		}
		myStaisfiedFlag =
				Validator.validateServices(myDescriptors, dependencies);
		if (myService == null && isSatisfied()) {
			myService = create(dependencies);
			return;
		}
		handleChange(dependencyName, dependency, dependencies);
	}

	/**
	 * Called from <code>dependencyChanged</code> with validated values.
	 *
	 * @param name                  name of the dependency changed
	 * @param dependency            new dependency value, null if it was removed
	 * @param availableDependencies a map of all available dependencies and their names
	 */
	protected abstract void handleChange(String name, Object dependency,
										 Map<String, Object> availableDependencies);

	private boolean validate(String id, Object req) {
		List<DependencyDescriptor> reqs = getDependencyDescriptors();
		if (req != null) {
			if (Validator.validateService(reqs, id, req)) {
				return true;
			}
			theLogger.warn("Invalid service or id.  id: {}, service: {}",
					id, req);
			return false;
		}
		if (Validator.validateServiceId(reqs, id)) {
			return true;
		}
		theLogger.warn("Invalid service id: {}.", id);
		return false;
	}

	/**
	 * Returns the Class of the service managed by this lifecycle provider.
	 *
	 * @return Class of the service managed by this lifecycle provider
	 */
	protected abstract Class<I> getServiceClass();

	@Override
	public String[] getServiceClassNames() {
		if (myServiceClassNames == null) {
			myServiceClassNames = new String[]{getServiceClass().getName()};
		}
		return myServiceClassNames;
	}

	/**
	 * Adds a dependency after the lifecycle is initialized.
	 *
	 * @param desc dependency to add
	 * @return true if successful, false if the dependency name is in use
	 */
	protected boolean addRuntimeDependency(DependencyDescriptor desc) {
		if (desc == null) {
			throw new NullPointerException();
		}
		for (DependencyDescriptor d : getDependencyDescriptors()) {
			if (desc.getDependencyName().equals(d.getDependencyName())) {
				theLogger.warn("Unable to add dependency, name already in use: {}",
						desc.getDependencyName());
				return false;
			}
		}
		/*if(DependencyType.REQUIRED == desc.getDependencyType()){
			DependencyDescriptor optDesc = new DependencyDescriptor(
                    desc.getDependencyName(), 
                    desc.getServiceClass(), 
                    desc.getServiceFilter(), 
                    DependencyType.OPTIONAL);
            desc = optDesc;
        }*/
		if (myRuntimeDescriptors == null) {
			myRuntimeDescriptors = new ArrayList<>();
		}
		theLogger.info("Adding optional runtime dependency {}",
				desc.getDependencyName());
		myDescriptors.add(desc);
		myRuntimeDescriptors.add(desc);
		firePropertyChange(PROP_DEPENDENCY_ADDED, null, desc);
		return true;
	}

	/**
	 * Removes a dependency after the lifecycle is initialized.
	 * If the dependency was not added after initialization, it will not be
	 * removed.
	 *
	 * @param depName dependency to remove
	 * @return true if successful, false if the dependency name is not found, or does not belong to
	 * a runtime dependency.
	 */
	protected boolean removeRuntimeDependency(String depName) {
		if (depName == null) {
			throw new NullPointerException();
		}
		if (myRuntimeDescriptors == null) {
			return false;
		}
		DependencyDescriptor desc = getRuntimeDescriptor(depName);
		if (desc == null) {
			theLogger.warn("Could not find optional runtime dependency {}", depName);
			return false;
		}
		theLogger.info("Removing optional runtime dependency {}", depName);
		myRuntimeDescriptors.remove(desc);
		myDescriptors.remove(desc);
		firePropertyChange(PROP_DEPENDENCY_REMOVED, null, desc);
		return true;
	}

	private DependencyDescriptor getRuntimeDescriptor(String name) {
		for (DependencyDescriptor d : myRuntimeDescriptors) {
			if (name.equals(d.getDependencyName())) {
				return d;
			}
		}
		return null;
	}
}
