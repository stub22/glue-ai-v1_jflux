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
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * The ServiceConnectionDirectory helps locate the proper
 * ConfigurationLoader and ServiceFactory to build the right Service from
 * the given parameters.
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public class ServiceConnectionDirectory {
	private static final org.slf4j.Logger theLogger = LoggerFactory.getLogger(ServiceConnectionDirectory.class);

	/**
	 * Locates the appropriate ConfigurationLoader and ServiceFactory
	 * matching the given versions, and able to create Service instance of the
	 * ServiceClass using the given param.
	 *
	 * @param <ServiceClass> Class of the Service built
	 * @param <Param>        Class of the parameter required to build the Service
	 * @param context        BundleContext used to located ServiceConfigurationLoaders and
	 *                       ServiceFactories
	 * @param serviceVersion Version of the Service to be built.  This is used to locate the proper
	 *                       ServiceFactory
	 * @param configFormat   Version of the ServiceConfiguration data format. This is used to locate
	 *                       the proper ConfigurationLoader
	 * @param param          Parameter to load a ServiceConfig from.  This is passed to a
	 *                       ConfigurationLoader
	 * @param paramClass     Class of the Param to be read.  This is needed in addition to the Param
	 *                       instance in order to resolve the correct class when dealing with
	 *                       inheritance.
	 * @param serviceClass   Class of the Service to be built
	 * @return Service matching the given type and version, built from the given param
	 */
	public static <ServiceClass, Param> ServiceClass buildService(
			BundleContext context,
			VersionProperty serviceVersion,
			VersionProperty configFormat,
			Param param,
			Class<Param> paramClass,
			Class<ServiceClass> serviceClass) throws Exception {
		if (context == null || serviceVersion == null || configFormat == null ||
				param == null || paramClass == null || serviceClass == null) {
			throw new NullPointerException();
		}
		List<ServiceFactory<ServiceClass, ?>> factories =
				getServiceFactories(context, serviceVersion, serviceClass);
		if (factories == null) {
			theLogger.warn("Unable to find ServiceFactories matching version: "
							+ "{}, class: {}.",
					serviceVersion.display(),
					serviceClass.getName());
			return null;
			/*theLogger.log(Level.INFO,
					"Using EmptyFactory for {}", serviceClass.getName());
            factories = new ArrayList<ServiceFactory<ServiceClass, ?>>();
            ServiceFactory<ServiceClass, ?> fact = 
                    new EmptyFactory(serviceClass, serviceVersion);
            factories.add(fact);*/
		}
		for (ServiceFactory factory : factories) {
			Class configClass = factory.getConfigurationClass();
			List<ConfigurationLoader<?, Param>> loaders =
					getConfigLoaders(
							context, configFormat, configClass, paramClass);
			if (loaders == null) {
				theLogger.warn("Unable to find ServiceLoader matching configFormat: "
								+ "{}, config class: {}, param class: {}.",
						configFormat.display(),
						configClass.getName(),
						paramClass.getName());
				continue;
			}
			for (ConfigurationLoader<?, Param> loader : loaders) {
				Object config = loader.loadConfiguration(param);
				if (config == null) {
					theLogger.warn("Loader returned null configuration.  "
							+ "Trying next loader if available.");
					continue;
				}
				Object serviceObj = factory.build(config);
				if (serviceObj != null) {
					return (ServiceClass) serviceObj;
				}
			}
		}
		return null;
	}

	/**
	 * Locates an appropriate ConfigurationLoader and ServiceFactory
	 * matching the given versions, and able to create Service instance of the
	 * ServiceClass using a parameter of the given Param Class.
	 *
	 * @param <ServiceClass> Class of the Service built
	 * @param <Param>        Class of the parameter required to build the Service
	 * @param context        BundleContext used to located ServiceConfigurationLoaders and
	 *                       ServiceFactories
	 * @param serviceVersion Version of the Service to be built.  This is used to locate the proper
	 *                       ServiceFactory
	 * @param configFormat   Version of the ServiceConfiguration data format. This is used to locate
	 *                       the proper ConfigurationLoader
	 * @param serviceClass   Class of the Service to be built
	 * @param paramClass     Class of the parameter required by the ConfigurationLoader
	 * @return ServiceContext containing appropriate ConfigurationLoader and ServiceFactory matching
	 * the given versions, and able to create Service instance of the ServiceClass using a parameter
	 * of the given Param Class
	 */
	public static <ServiceClass, Param>
	ServiceContext<ServiceClass, ?, Param> buildServiceContext(
			BundleContext context,
			VersionProperty serviceVersion,
			VersionProperty configFormat,
			Class<ServiceClass> serviceClass,
			Class<Param> paramClass) {
		if (context == null) {
			return null;
		}
		List<ServiceFactory<ServiceClass, ?>> factories =
				getServiceFactories(context, serviceVersion, serviceClass);
		if (factories == null) {
			return null;
		}
		for (ServiceFactory factory : factories) {
			if (factory == null) {
				continue;
			}
			Class configClass = factory.getConfigurationClass();
			List<ConfigurationLoader<?, Param>> loaders =
					getConfigLoaders(
							context, configFormat, configClass, paramClass);
			if (loaders == null) {
				continue;
			}
			for (ConfigurationLoader<?, Param> loader : loaders) {
				if (loader == null) {
					continue;
				}
				ServiceContext<ServiceClass, ?, Param> serviceContext =
						new ServiceContext(loader, factory);
				return serviceContext;
			}
		}
		return null;
	}

	/**
	 * Retrieves all ServiceFactories with the given ServiceClass and Service
	 * Version.
	 *
	 * @param <ServiceClass> Class of the Service built
	 * @param context        BundleContext used to located the ServiceFactories
	 * @param serviceVersion Version of the Service to be built.  This is used to locate the proper
	 *                       ServiceFactory
	 * @param serviceClass   Class of the Service to be built
	 * @return all ServiceFactories with the given ServiceClass and Service Version
	 */
	public static <ServiceClass>
	List<ServiceFactory<ServiceClass, ?>> getServiceFactories(
			BundleContext context,
			VersionProperty serviceVersion,
			Class<ServiceClass> serviceClass) {
		if (context == null || serviceVersion == null || serviceClass == null) {
			return null;
		}
		try {
			ServiceReference[] refs = context.getServiceReferences(
					ServiceFactory.class.getName(),
					"(&(" + Constants.SERVICE_VERSION + "=" +
							serviceVersion + ")(" + Constants.SERVICE_CLASS +
							"=" + serviceClass.getName() + "))");
			if (refs == null || refs.length == 0) {
				theLogger.warn("Unable to find ServiceFactory with version={} "
								+ "and class={}",
						serviceVersion.toString(), serviceClass.getName());
				return null;
			}
			List<ServiceFactory<ServiceClass, ?>> factories =
					new ArrayList(refs.length);
			for (ServiceReference ref : refs) {
				ServiceFactory<ServiceClass, ?> factory =
						(ServiceFactory<ServiceClass, ?>) context.getService(ref);
				if (factory != null) {
					factories.add(factory);
				}
			}
			return factories;
		} catch (InvalidSyntaxException ex) {
			theLogger.error("There was an error fetching service references. {}", ex);
			return null;
		}
	}

	/**
	 * Retrieves all ServiceConfigurationLoaders with the given Param Class,
	 * ServiceConfig Class, and Configuration Format Version.
	 *
	 * @param <ServiceConfig> Class of ServiceConfig to be returned
	 * @param <Param>         Class of the parameter accepted by the loaders
	 * @param context         BundleContext used to locate the loaders
	 * @param configFormat    Version of the ServiceConfiguration data format. This is used to
	 *                        locate the proper ConfigurationLoader
	 * @param configClass     Class of ServiceConfig to be returned
	 * @param paramClass      Class of the parameter accepted by the loaders
	 * @return all ServiceConfigurationLoaders with the given Param Class, ServiceConfig Class, and
	 * Configuration Format Version
	 */
	public static <ServiceConfig, Param>
	List<ConfigurationLoader<ServiceConfig, Param>> getConfigLoaders(
			BundleContext context,
			VersionProperty configFormat,
			Class<ServiceConfig> configClass,
			Class<Param> paramClass) {
		if (configFormat == null || configClass == null || paramClass == null) {
			return null;
		}
		try {
			ServiceReference[] refs = context.getServiceReferences(
					ConfigurationLoader.class.getName(),
					"(&(" + Constants.CONFIG_FORMAT_VERSION +
							"=" + configFormat + ")(" +
							Constants.CONFIG_CLASS + "=" +
							configClass.getName() + ")(" +
							Constants.CONFIG_PARAM_CLASS + "=" +
							paramClass.getName() + "))");
			if (refs == null || refs.length == 0) {
				theLogger.warn("Unable to find ServiceConfigurationLoader with "
								+ "formatVersion={}, configClass={} "
								+ "and paramClass={}",
						configFormat.toString(),
						configClass.getName(), paramClass.getName());
				return null;
			}
			List<ConfigurationLoader<ServiceConfig, Param>> loaders =
					new ArrayList(refs.length);
			for (ServiceReference ref : refs) {
				ConfigurationLoader<ServiceConfig, Param> loader;
				loader = (ConfigurationLoader<ServiceConfig, Param>)
						context.getService(ref);
				if (loader != null) {
					loaders.add(loader);
				}
			}
			return loaders;
		} catch (InvalidSyntaxException ex) {
			theLogger.error("There was an error fetching service references. {}", ex);
			return null;
		}
	}

	/**
	 * Finds ServiceConfigurationWriters matching the given criteria.
	 *
	 * @param <ServiceConfig> type of configuration to write
	 * @param <Param>         parameter type used for writing
	 * @param context         BundleContext used to locate writers
	 * @param configFormat    Version of the ServiceConfiguration data format
	 * @param configClass     Class of ServiceConfig to be returned
	 * @param paramClass      Class of the parameter accepted by the writers
	 * @return all ServiceConfigurationWriters with the given Param Class, ServiceConfig Class, and
	 * Configuration Format Version
	 */
	public static <ServiceConfig, Param>
	List<ConfigurationWriter<ServiceConfig, Param>> getConfigWriters(
			BundleContext context,
			VersionProperty configFormat,
			Class<ServiceConfig> configClass,
			Class<Param> paramClass) {
		if (configFormat == null || configClass == null || paramClass == null) {
			return null;
		}
		try {
			ServiceReference[] refs = context.getServiceReferences(
					ConfigurationWriter.class.getName(),
					"(&(" + Constants.CONFIG_FORMAT_VERSION +
							"=" + configFormat + ")(" +
							Constants.CONFIG_CLASS + "=" +
							configClass.getName() + ")(" +
							Constants.CONFIG_PARAM_CLASS + "=" +
							paramClass.getName() + "))");
			if (refs == null || refs.length == 0) {
				theLogger.warn("Unable to find ServiceConfigurationWriter with "
								+ "formatVersion={}, configClass={} "
								+ "and paramClass={}",
						configFormat.toString(),
						configClass.getName(), paramClass.getName());
				return null;
			}
			List<ConfigurationWriter<ServiceConfig, Param>> writers =
					new ArrayList(refs.length);
			for (ServiceReference ref : refs) {
				ConfigurationWriter<ServiceConfig, Param> loader;
				loader = (ConfigurationWriter<ServiceConfig, Param>)
						context.getService(ref);
				if (loader != null) {
					writers.add(loader);
				}
			}
			return writers;
		} catch (InvalidSyntaxException ex) {
			theLogger.error("There was an error fetching service references. {}", ex);
			return null;
		}
	}
}
