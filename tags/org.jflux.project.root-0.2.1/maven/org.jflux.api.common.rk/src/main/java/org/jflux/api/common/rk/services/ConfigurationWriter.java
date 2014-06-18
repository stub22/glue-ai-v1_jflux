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
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public interface ConfigurationWriter<ServiceConfig,Param> {
    /**
     * Specifies the Service Configuration Format version to write.
     * @return VersionProperty specifying the Service Configuration Format to 
     * write
     */
    public VersionProperty getConfigurationFormat();
    
    /**
     * Writes a ServiceConfig of the specified type using the given parameters.
     * @param config ServiceConfig to write
     * @param param parameters needed to write the ServiceConfig, such as a File
     * @throws Exception if there is an error writing the configuration
     */
    public boolean writeConfiguration(ServiceConfig config, Param param) 
            throws Exception;
    
    /**
     * Returns the Class of the ServiceConfig to write.
     * @return Class of the ServiceConfig to write
     */
    public Class<ServiceConfig> getConfigurationClass();
    
    /**
     * Returns the Class of the Parameter required to write a ServiceConfig.
     * @return Class of the Parameter required to write a ServiceConfig
     */
    public Class<Param> getParameterClass();
}
