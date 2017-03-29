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

import java.util.Map;

/**
 * @author matt
 */
public class ServiceDependency {
	private final String myDependencyName;
	private final String myDependencyClassName;
	private final Cardinality myCardinality;
	private final UpdateStrategy myUpdateStrategy;
	private final Map<String, String> myDependencyProperties;


	/**
	 * Create a new required DependencyDescriptor with the given values.
	 *
	 * @param dependencyName dependency name within a ServiceLifecycleProvider
	 * @param className      Class name of the dependency
	 * @param props          Properties of the dependency
	 */
	public ServiceDependency(final String dependencyName, final String className,
							 final Cardinality cardinality, final UpdateStrategy updateStrategy,
							 final Map<String, String> props) {
		if (dependencyName == null || className == null
				|| cardinality == null || updateStrategy == null) {
			throw new NullPointerException();
		}
		myDependencyName = dependencyName;
		myDependencyClassName = className;
		myCardinality = cardinality;
		myUpdateStrategy = updateStrategy;
		myDependencyProperties = props;
	}

	/**
	 * Returns the dependency name used within a ServiceLifecycleProvider.
	 *
	 * @return dependency name used within a ServiceLifecycleProvider
	 */
	public String getDependencyName() {
		return myDependencyName;
	}

	/**
	 * Returns the name of the dependency's Class.
	 *
	 * @return the name of the dependency's Class
	 */
	public String getDependencyClassName() {
		return myDependencyClassName;
	}

	public Cardinality getCardinality() {
		return myCardinality;
	}

	public UpdateStrategy getUpdateStrategy() {
		return myUpdateStrategy;
	}

	public Map<String, String> getProperties() {
		return myDependencyProperties;
	}

	/**
	 * Dependency Cardinality has two parts requirement and count. All dependencies marked required
	 * must be available for the ServiceManager to make the service available. If a required
	 * dependency in lost and there is not replacement, then the service will become unavailable.
	 * The service will be notified of optional dependencies as they come and go.
	 *
	 * If a dependency has a singular (or unary) cardinality, then the service only uses and expects
	 * a single instance of the dependency. A multiple cardinality means the service uses and
	 * expects multiple instances of the dependency.
	 */
	public enum Cardinality {
		/**
		 * The service only uses and expects a single instance of the dependency. The service will
		 * be notified of this dependency as it comes and goes, but doesn't require it to function.
		 */
		OPTIONAL_UNARY(false, false),
		/**
		 * The service only uses and expects a single instance of the dependency. This dependency is
		 * required for the service to be available.
		 */
		MANDATORY_UNARY(true, false),
		/**
		 * The service uses and expects multiple instances of the dependency.  The service will
		 * be notified of this dependency as it comes and goes, but doesn't require it to function.
		 */
		OPTIONAL_MULTIPLE(false, true),
		/**
		 * The service uses and expects multiple instances of the dependency. This dependency is
		 * required for the service to be available.
		 */
		MANDATORY_MULTIPLE(true, true);

		private final boolean myRequiredFlag;
		private final boolean myMultiplicityFlag;

		Cardinality(final boolean required, final boolean multiple) {
			myRequiredFlag = required;
			myMultiplicityFlag = multiple;
		}

		public boolean isRequired() {
			return myRequiredFlag;
		}

		public boolean isMultiple() {
			return myMultiplicityFlag;
		}
	}

	/**
	 * How a {@link ServiceLifecycle} should be updated when a dependency changes.
	 */
	public enum UpdateStrategy {
		/**
		 * The {@link ServiceLifecycle} cannot update the service. The service needs to be disposed
		 * and recreated to handle the dependency change.
		 *
		 * {@link ServiceLifecycle}s for dependencies that are {@link UpdateStrategy#STATIC} will
		 * never call the {@link ServiceLifecycle#handleDependencyChange} method.
		 */
		STATIC,
		/**
		 * The {@link ServiceLifecycle} can dynamically update the service when a dependency changes
		 * via the {@link ServiceLifecycle#handleDependencyChange} method.
		 */
		DYNAMIC
	}
}
