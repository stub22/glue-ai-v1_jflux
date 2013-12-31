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
package org.jflux.impl.services.rk.osgi.lifecycle;

import java.util.Properties;
import org.osgi.framework.BundleContext;
import org.jflux.impl.services.rk.lifecycle.ManagedService;
import org.jflux.impl.services.rk.lifecycle.ServiceLifecycleProvider;
import org.jflux.impl.services.rk.lifecycle.utils.ManagedServiceFactory;

/**
 *
 * @author Matthew Stevenson <www.robokind.org>
 */
public class OSGiComponentFactory implements ManagedServiceFactory{
    private BundleContext myContext;
    
    public OSGiComponentFactory(BundleContext context){
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
