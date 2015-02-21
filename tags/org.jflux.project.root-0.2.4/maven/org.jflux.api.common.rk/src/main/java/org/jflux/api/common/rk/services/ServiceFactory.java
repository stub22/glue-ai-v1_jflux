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

/**
 * A ServiceFactory creates some service with the given Class and
 * the given VersionProperty, using a ServiceConfig
 * 
 * @param <ServiceConfig> Type of Configuration class this factory takes
 * @param <ServiceClass> Type of Service this factory creates
 * 
 * @author Matthew Stevenson <www.jflux.org>
 */
public interface ServiceFactory<ServiceClass,ServiceConfig> {
    /**
     * Returns the VersionProperty of the Service this connects to.
     * @return VersionProperty of the Service this connects to
     */
    public VersionProperty getServiceVersion();
    
    /**
     * Builds a new instance of a Service from the given ServiceConfig.  The
     * Service built is defined by the ServiceVersion.     * 
     * Examples: Servo Controllers, Microphones, Text-to-speech, Face Tracking.
     * 
     * @param config Configuration parameters used to build to the IO.
     * @return input and/or output interface specified by the config
     */
    public ServiceClass build(ServiceConfig config) throws Exception;
    
    /**
     * Returns the Class of the Service connected to.
     * @return Class of the Service connected to
     */
    public Class<ServiceClass> getServiceClass();
    
    /**
     * Returns the Class of ServiceConfig required to build to this type of
     * Service.
     * @return Class of ServiceConfig required to build to this type of Service
     */
    public Class<ServiceConfig> getConfigurationClass();
}
