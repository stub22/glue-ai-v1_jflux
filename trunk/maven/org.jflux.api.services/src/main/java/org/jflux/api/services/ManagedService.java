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
package org.jflux.api.services;

import java.util.List;
import java.util.Properties;
import org.jflux.api.services.extras.PropertyChangeSource;

/**
 * Manages a service throughout its lifetime of the service.
 * A ManagedService provides information about the service's dependencies and
 * tracks their availability.  As dependencies become available, unavailable, or
 * change, the ManagedService updates the services and its availability within
 * the ServiceRegistry.
 * @param <T> type of service being managed
 * @author Matthew Stevenson <www.robokind.org>
 */
public interface ManagedService<T> extends PropertyChangeSource{
    /**
     * Property name for the type of service managed.
     */
    public final static String PROP_SERVICE_TYPE = "serviceType";
    /**
     * Property change event name for the managed service being modified.
     */
    public final static String PROP_SERVICE_CHANGED = "serviceChanged";
    /**
     * Property change event name for a dependency's status changing
     */
    public final static String PROP_DEPENDENCY_CHANGED = "dependencyChanged";
    /**
     * Starts tracking dependencies and managing the service.
     */
    public void start();
    /**
     * Stops tracking dependencies.
     */
    public void stop();
    
    /**
     * Stops and unregisters the service, cleans up the service lifecycle and
     * management, and stops and unregisters the ManagedService.
     */
    public void dispose();
    
    /**
     * Returns a list describing the service dependencies of this 
     * ManagedService.
     * @return list describing the service dependencies of this ManagedService.
     */
    public List<DependencyDescriptor> getDependencies();
    /**
     * Returns the number of dependencies for this ManagedService.
     * @return number of dependencies for this ManagedService
     */
    public int getDependencyCount();
    /**
     * Returns the number of satisfied dependencies.
     * @return number of satisfied dependencies
     */
    public int getAvailableDependencyCount();
    /**
     * Returns true if the specified dependency is available.
     * @param dependencyName name of the dependency to check
     * @return true if the specified dependency is available
     */
    public boolean getDependencyStatus(String dependencyName);
    /**
     * If set true, the Service being managed will be automatically added to 
     * the Service Registry when available.
     * The default value is true.
     * @param enabled if true the service will be automatically registered to 
     * the Service Registry when available.
     */
    public void setRegistrationEnabled(boolean enabled);
    /**
     * Returns true if automatic service registration is enabled.
     * @return true if automatic service registration is enabled
     */
    public boolean getRegistrationEnabled();
    /**
     * Registers the service if it is available.
     */
    public void register();
    /**
     * Unregisters the service if it is registered.
     * Call setRegistrationEnabled(false) to prevent the service from being
     * registered when dependencies change.
     */
    public void unregister();
    /**
     * Returns the registration properties to use when registering the service.
     * These may change throughout the lifetime of the service.
     * @return registration properties to use when registering the service
     */
    public Properties getRegistrationProperties();
    /**
     * Returns the class names of service being managed.
     * @return class names of service being managed
     */    
    public String[] getServiceClassNames();
    /**
     * Returns true if the service is available and ready to use.
     * @return true if the service is available and ready to use
     */
    public boolean isAvailable();
    /**
     * Returns true if the service has been added to the Service Registry.
     * @return true if the service has been added to the Service Registry
     */
    public boolean isRegistered();
}
