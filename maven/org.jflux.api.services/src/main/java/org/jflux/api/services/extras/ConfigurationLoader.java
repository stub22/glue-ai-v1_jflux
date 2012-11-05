/*
 * Copyright 2011 Hanson Robokind LLC.
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

package org.jflux.api.services.extras;

/**
 * An ConfigurationLoader loads a ServiceConfig with a specific version.  
 * The ServiceConfig is used to connect to some Service such as: 
 * Servos, Microphone, Text-to-Speech, Face Tracking, etc...
 * 
 * @param <ServiceConfig> Service Configuration Class returned by this loader
 * @param <Param> Parameter Class passed in to this loader
 * 
 * @author Matthew Stevenson <www.robokind.org>
 */
public interface ConfigurationLoader<ServiceConfig,Param> {
    /**
     * Specifies the Service Configuration Format version which can be loaded.
     * @return VersionProperty specifying the Service Configuration Format to 
     * load
     */
    public VersionProperty getConfigurationFormat();
    
    /**
     * Loads a ServiceConfig of the specified type from the given parameters.
     * @param param parameters needed to load the ServiceConfig
     * @return ServiceConfig of the specified type from the given parameters 
     * @throws Exception if there is an error loading the configuration
     */
    public ServiceConfig loadConfiguration(Param param) throws Exception;
    
    /**
     * Returns the Class of the ServiceConfig to be loaded.
     * @return Class of the ServiceConfig to be loaded
     */
    public Class<ServiceConfig> getConfigurationClass();
    
    /**
     * Returns the Class of the Parameter required to load a ServiceConfig.
     * @return Class of the Parameter required to load a ServiceConfig
     */
    public Class<Param> getParameterClass();
}
