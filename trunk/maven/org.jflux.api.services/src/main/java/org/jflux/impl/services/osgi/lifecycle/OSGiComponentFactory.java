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
package org.jflux.impl.services.osgi.lifecycle;

import java.util.Properties;
import org.jflux.api.registry.opt.RegistryContext;
import org.jflux.api.services.ManagedService;
import org.jflux.api.services.ServiceLifecycleProvider;
import org.jflux.api.services.lifecycle.utils.ManagedServiceFactory;

/**
 *
 * @author Matthew Stevenson <www.robokind.org>
 */
public class OSGiComponentFactory implements ManagedServiceFactory{
    private RegistryContext myContext;
    
    /**
     * Creates an OSGi component factory.
     * @param context the registry context
     */
    public OSGiComponentFactory(RegistryContext context){
        if(context == null){
            throw new NullPointerException();
        }
        myContext = context;
    }
    
    @Override
    public <T> ManagedService<T> createService(
            ServiceLifecycleProvider<T> lifecycle, 
            Properties registrationPropeties) {
        return new OSGiComponent<T>(myContext, lifecycle, registrationPropeties);
    }
    
    @Override
    public <T> ManagedService<T> createService(
            ServiceLifecycleProvider<T> lifecycle, 
            Properties registrationPropeties, 
            Properties uniqueRegistrationProperties) {
        return new OSGiComponent<T>(myContext, lifecycle, 
                registrationPropeties, uniqueRegistrationProperties, true);
    }
    
}
