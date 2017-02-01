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

import org.jflux.impl.services.rk.lifecycle.DependencyDescriptor.DependencyType;
import org.jflux.impl.services.rk.property.PropertyChangeSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Manages the state and availability of a service through a Dependency-
 * Injection pattern.
 *
 * A ServiceLifecycleProvider defines the service dependencies as a List of
 * ServiceDependencyDescriptors, which provide a unique String key, dependency
 * class, and dependency filter string.
 *
 * When all of the dependencies are available, the service lifecycle will be
 * started with a call to the <code>start</code> method.  This should create
 * the service and make it available through the <code>getService</code> method.
 *
 * Once the service is started, if a dependency is changed or removed,
 * <code>dependencyChanged</code> will be called with the new dependency.
 *
 * Once a ServiceLifecycleProvider is initialized, it may define new
 * dependencies or remove dependencies added in this way, but the original
 * dependencies cannot change.  When dependencies are added and removed after
 * initialization, a property change event should be fired.
 *
 * @param <T> type of service managed by this lifecycle provider
 * @author Matthew Stevenson <www.robokind.org>
 */
public interface ServiceLifecycleProvider<T> extends PropertyChangeSource {
	/**
	 * Property change name for adding a dependency after initializing.
	 */
	public final static String PROP_DEPENDENCY_ADDED = "dependencyAdded";

	/**
	 * Property change name for removing a dependency after initializing.
	 */
	public final static String PROP_DEPENDENCY_REMOVED = "dependencyRemoved";

	/**
	 * Called the first time all dependencies are available.
	 *
	 * @param dependencyMap dependencies needed to start the service lifecycle
	 */
	public void start(Map<String, Object> dependencyMap);

	public void stop();

	/**
	 * Called after the service is started and a dependency is changed or
	 * removed.  This may result in a new service or null being returned by
	 * getService.
	 *
	 * @param dependencyId dependency id defined in the <code>DependencyDescriptor</code>
	 * @param dependency   the new dependency, or null if the dependency is no longer available.
	 */
	public void dependencyChanged(
			String dependencyId,
			Object dependency,
			Map<String, Object> dependencies);

	/**
	 * Returns the service managed by this lifecycle provider.  Returns null if
	 * the lifecycle has not been started or if the service is unavailable.
	 *
	 * @return service the service managed by this lifecycle provider
	 */
	public T getService();

	/**
	 * Returns a list describing the service dependencies required by this
	 * lifecycle provider.
	 *
	 * @return list describing the service dependencies required by this lifecycle provider
	 */
	public List<DependencyDescriptor> getDependencyDescriptors();


	/**
	 * Returns the properties to be used when register the service managed by
	 * this lifecycle provider.  This is intended to be used as the registration
	 * properties for the OSGi Service Registry.
	 *
	 * @return properties to be used when register the service managed by this lifecycle provider
	 */
	public Properties getRegistrationProperties();

	/**
	 * Returns the names of the interfaces this service implements and makes
	 * available.
	 *
	 * @return names of the interfaces this service implements and makes available
	 */
	public String[] getServiceClassNames();

	/**
	 * The Validator is used validate a dependencyId and dependency against a
	 * list of DependencyDescriptors.
	 */
	public static class Validator {
		private static final Logger theLogger = LoggerFactory.getLogger(Validator.class);

		/**
		 * Validates a map of dependency ids and services to validate against a
		 * list of DependencyDescriptors.  A map is valid if it has a dependency
		 * matching each descriptor in the list.  A descriptor is matched if
		 * there is an entry where the key and the dependency's class matches
		 * the descriptor's fields.
		 *
		 * @param descriptors   list of DependencyDescriptors needed to be matched
		 * @param depdendencies map of ids and dependencies
		 * @return true if there is a dependency in the map for each descriptor, false if one or
		 * more descriptors is not filled
		 */
		public static boolean validateServices(
				List<DependencyDescriptor> descriptors,
				Map<String, Object> depdendencies) {
			if (descriptors == null || descriptors.isEmpty()) {
				return true;
			} else if (depdendencies == null || depdendencies.isEmpty()) {
				for (DependencyDescriptor desc : descriptors) {
					if (DependencyType.REQUIRED == desc.getDependencyType()) {
						return false;
					}
				}
				return true;
			}
			for (DependencyDescriptor descriptor : descriptors) {
				if (!checkDescriptor(descriptor, depdendencies)) {
					return false;
				}
			}
			return true;
		}

		/**
		 * Validates a DependencyDescriptor against a map of dependencies.  A
		 * descriptor is valid if there is an entry where the key and the
		 * dependency's class matches the descriptor's fields.  If there is no
		 * dependency for the name, the descriptor is only valid if it is
		 * optional.
		 * If the dependecy's class does not match the descriptor, it is
		 * invalid.
		 *
		 * @param descriptor   descriptor to check for
		 * @param dependencies map of ids and dependencies to check against
		 * @return true is a map entry matches the descriptor, false otherwise
		 */
		private static boolean checkDescriptor(
				DependencyDescriptor descriptor,
				Map<String, Object> dependencies) {
			String id = descriptor.getDependencyName();
			Object req = dependencies.get(id);
			if (req == null) {
				return DependencyType.OPTIONAL ==
						descriptor.getDependencyType();
			}
			Class reqClass = descriptor.getServiceClass();
			if (!reqClass.isAssignableFrom(req.getClass())) {
				return false;
			}
			return true;
		}

		/**
		 * Validates an id and dependency against a list of
		 * DependencyDescriptors.  The id and dependency are valid if there is a
		 * descriptor with a matching dependency id and class.
		 *
		 * @param descriptors  list of DependencyDescriptors to match against
		 * @param dependencyId id for the dependency
		 * @param dependency   dependency to validate
		 * @return true if a matching descriptor is found
		 */
		public static boolean validateService(
				List<DependencyDescriptor> descriptors,
				String dependencyId, Object dependency) {
			if (dependencyId == null || dependency == null) {
				theLogger.warn("Found null argument.  Returning false.");
				return false;
			}
			for (DependencyDescriptor descriptor : descriptors) {
				String id = descriptor.getDependencyName();
				Class reqClass = descriptor.getServiceClass();
				if (dependencyId.equals(id) &&
						reqClass.isAssignableFrom(dependency.getClass())) {
					return true;
				}
			}
			return false;
		}

		/**
		 * Returns true if there is a DependencyDescriptor with the given
		 * dependency id.
		 *
		 * @param descriptors  list of DependencyDescriptors to search
		 * @param dependencyId dependency id to search for
		 * @return true if there is a DependencyDescriptor with the given dependency id
		 */
		public static boolean validateServiceId(
				List<DependencyDescriptor> descriptors,
				String dependencyId) {
			if (dependencyId == null) {
				theLogger.warn("Found null dependencyId.  Returning false.");
				return false;
			}
			for (DependencyDescriptor descriptor : descriptors) {
				String id = descriptor.getDependencyName();
				if (dependencyId.equals(id)) {
					return true;
				}
			}
			return false;
		}
	}
}
