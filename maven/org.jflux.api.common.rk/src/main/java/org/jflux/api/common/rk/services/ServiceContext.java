/*
 * Copyright 2014 the JFlux Project.
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

package org.jflux.api.common.rk.services;

import org.jflux.api.common.rk.config.VersionProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An ServiceContext keeps track of an attempt to create a new Service.  It
 * contains a complementary pair of ConfigurationLoader and
 * ServiceFactory where:
 * the loader takes the given Param type
 * the loader and factory share the same ServiceConfig class
 * the factory returns the given Service class
 *
 * @param <ServiceClass>  Class of Service created in this ServiceContext
 * @param <ServiceConfig> Class of ServiceConfig used in this ServiceContext
 * @param <Param>         Class of the parameter used by this ServiceContext
 * @author Matthew Stevenson <www.jflux.org>
 */
public class ServiceContext<ServiceClass, ServiceConfig, Param> {
	private static final Logger theLogger = LoggerFactory.getLogger(ServiceContext.class);
	private ConfigurationLoader<ServiceConfig, Param> myLoader;
	private ServiceFactory<ServiceClass, ServiceConfig> myFactory;
	private Param myLoadParameter;
	private ServiceConfig myConfig;
	private ServiceClass myService;

	/**
	 * Creates an empty ServiceContext
	 */
	public ServiceContext() {
	}

	/**
	 * Creates a new ServiceContext from the given
	 * ConfigurationLoader and ServiceFactory.
	 *
	 * @param loader  ConfigurationLoader of the correct type
	 * @param factory ServiceFactory of the correct type
	 */
	public ServiceContext(
			ConfigurationLoader<ServiceConfig, Param> loader,
			ServiceFactory<ServiceClass, ServiceConfig> factory) {
		if (loader == null || factory == null) {
			throw new NullPointerException();
		}
		myLoader = loader;
		myFactory = factory;
	}

	/**
	 * Returns the ConfigurationLoader for this ServiceContext.
	 *
	 * @return ConfigurationLoader for this ServiceContext
	 */
	public ConfigurationLoader<ServiceConfig, Param> getServiceConfigurationLoader() {
		return myLoader;
	}

	/**
	 * Sets the ConfigurationLoader for this ServiceContext.
	 *
	 * @param loader ConfigurationLoader to use
	 */
	public void setServiceConfigurationLoader(ConfigurationLoader<ServiceConfig, Param> loader) {
		myLoader = loader;
	}

	/**
	 * Return the ServiceFactory used by this ServiceContext.
	 *
	 * @return ServiceFactory used by this ServiceContext
	 */
	public ServiceFactory<ServiceClass, ServiceConfig> getServiceFactory() {
		return myFactory;
	}

	/**
	 * Sets the ServiceFactory to be used by this ServiceContext.
	 *
	 * @param factory ServiceFactory to be used by this ServiceContext
	 */
	public void setServiceFactory(ServiceFactory<ServiceClass, ServiceConfig> factory) {
		myFactory = factory;
	}

	/**
	 * Returns the load parameter.
	 *
	 * @return load parameter
	 */
	public Param getLoadParameter() {
		return myLoadParameter;
	}

	/**
	 * Sets the load parameter.  This is passed to the
	 * ConfigurationLoader to load the ServiceConfig.
	 *
	 * @param param parameter passed to the ConfigurationLoader to load the ServiceConfig
	 */
	public void setLoadParameter(Param param) {
		myLoadParameter = param;
	}

	/**
	 * Loads the ServiceConfig using the ConfigurationLoader and
	 * LoadParameter.
	 *
	 * @return true if the config is not null (regardless of when and where it was set)
	 */
	public boolean loadConfiguration() {
		if (myConfig != null) {
			return true;
		}
		if (myLoader == null || myLoadParameter == null) {
			return false;
		}
		try {
			myConfig = myLoader.loadConfiguration(myLoadParameter);
		} catch (Exception ex) {
			String msg = String.format(
					"There was an error loading the configuration.\n"
							+ "Config class: %s,\n"
							+ "Config format: %s,\n"
							+ "Loader class: %s,\n"
							+ "Param class: %s",
					myLoader.getConfigurationClass(),
					myLoader.getConfigurationFormat(),
					myLoader.getClass(),
					myLoader.getParameterClass());
			theLogger.warn(msg, ex);
		}
		return myConfig != null;
	}

	/**
	 * Returns the ServiceConfig loaded from the LoadParameter.
	 * loadConfiguration must be called for the configuration to be available.
	 *
	 * @return ServiceConfig loaded from the LoadParameter
	 */
	public ServiceConfig getServiceConfiguration() {
		return myConfig;
	}

	/**
	 * Sets the ServiceConfig to use instead of loading a config.  If this is
	 * set to null, this will attempt to load a config when building the
	 * service.
	 *
	 * @param config ServiceConfig to set
	 */
	public void setServiceConfiguration(ServiceConfig config) {
		myConfig = config;
	}

	/**
	 * Builds a Service using the ServiceFactory and ServiceConfig.  Attempts
	 * to load the ServiceConfig if it has not been loaded.
	 */
	public boolean buildService() {
		if (myService != null) {
			return true;
		}
		if (myFactory == null) {
			return false;
		}
		if (myConfig == null) {
			loadConfiguration();
			if (myConfig == null) {
				return false;
			}
		}
		try {
			myService = myFactory.build(myConfig);
		} catch (Exception ex) {
			String msg = String.format(
					"There was an error loading the configuration.\n"
							+ "Service class: %s,\n"
							+ "Service version: %s,\n"
							+ "Factory class: %s,\n"
							+ "Config class: %s.",
					myFactory.getServiceClass(),
					myFactory.getServiceVersion(),
					myFactory.getClass(),
					myFactory.getConfigurationClass());
			theLogger.warn(msg, ex);
		}
		return myService != null;
	}

	/**
	 * Returns the Service from this context.  buildService() must be called
	 * fore the service to be available.
	 *
	 * @return Service from this context
	 */
	public ServiceClass getService() {
		return myService;
	}

	public VersionProperty getServiceVersion() {
		return myFactory.getServiceVersion();
	}

	public Class<ServiceClass> getServiceClass() {
		return myFactory.getServiceClass();
	}

	public VersionProperty getConfigFormat() {
		if (myLoader == null) {
			return null;
		}
		return myLoader.getConfigurationFormat();
	}

	public Class<ServiceConfig> getConfigurationClass() {
		return myFactory.getConfigurationClass();
	}
}
