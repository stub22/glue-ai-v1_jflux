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
package org.jflux.api.services.lifecycle.utils;

import java.util.Properties;
import org.jflux.api.services.ManagedService;
import org.jflux.api.services.ServiceLifecycleProvider;

/**
 *
 * @author Matthew Stevenson <www.robokind.org>
 */
public interface ManagedServiceFactory {
    /**
     * Creates a managed service.
     * @param lifecycle the lifecycle provider
     * @param registrationPropeties the service's properties
     * @return the created service
     */
    public <T> ManagedService<T> createService(
            ServiceLifecycleProvider<T> lifecycle, 
            Properties registrationPropeties);
    
    /**
     * Creates a managed service with unique properties.
     * @param lifecycle the lifecycle provider
     * @param registrationPropeties the service's properties
     * @param uniqueRegistrationPropeties the service's unique properties
     * @return the created service
     */
    public <T> ManagedService<T> createService(
            ServiceLifecycleProvider<T> lifecycle, 
            Properties registrationPropeties, 
            Properties uniqueRegistrationPropeties);
}
