/*
 * Copyright 2013 The JFlux Project (www.jflux.org).
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
package org.jflux.api.service;

import java.util.List;
import java.util.Map;

/**
 * @author Matthew Stevenson
 */
public interface ServiceLifecycle<T> {
	/**
	 * Property change event name for a dependency becoming available.
	 */
	public final static String PROP_DEPENDENCY_AVAILABLE = "dependencyAvailable";
	/**
	 * Property change event name for a dependency changing.
	 */
	public final static String PROP_DEPENDENCY_CHANGED = "dependencyChanged";
	/**
	 * Property change event name for a dependency becoming unavailable.
	 */
	public final static String PROP_DEPENDENCY_UNAVAILABLE = "dependencyUnavailable";

	/**
	 * Returns a list describing the service dependencies required by this
	 * lifecycle provider.
	 *
	 * @return list describing the service dependencies required by this lifecycle provider
	 */
	public List<ServiceDependency> getDependencySpecs();

	/**
	 *
	 * @param dependencyMap
	 * @return
	 */
	public T createService(Map<String, Object> dependencyMap);

	/**
	 * Only called if the {@link org.jflux.api.service.ServiceDependency}'s
	 * {@link org.jflux.api.service.ServiceDependency.UpdateStrategy} is
	 * {@link org.jflux.api.service.ServiceDependency.UpdateStrategy#DYNAMIC}
	 */
	public T handleDependencyChange(T service, String changeType,
									String dependencyName, Object dependency,
									Map<String, Object> availableDependencies);

	/**
	 *
	 * @param service
	 * @param availableDependencies
	 */
	public void disposeService(T service, Map<String, Object> availableDependencies);

	/**
	 * Returns the names of the interfaces this service implements and makes
	 * available.
	 *
	 * @return names of the interfaces this service implements and makes available
	 */
	//TODO: Move or copy this in RegistrationStrategy.  RegistrationStrategy should decide what classes it is available as.
	//This is already part of DefaultRegistrationStrategy, and probably should be promoted to RegistrationStrategy.
	//Is there a reason the Lifecycle needs to declare the class names at runtime?  Any purpose it serves could be better handled as external bookkeeping.
	@Deprecated
	public String[] getServiceClassNames();
}
