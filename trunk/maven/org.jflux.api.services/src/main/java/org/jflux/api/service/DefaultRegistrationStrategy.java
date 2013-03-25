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
import java.util.Set;
import org.jflux.api.registry.Certificate;
import org.jflux.api.registry.Reference;
import org.jflux.api.registry.RegistrationRequest;
import org.jflux.api.registry.Registry;
import org.jflux.api.registry.basic.BasicRegistrationRequest;

/**
 *
 * @author matt
 */
public class DefaultRegistrationStrategy<T> implements RegistrationStrategy<T> {
    private Map<String,String> myRegistrationProperties;
    private Registry myRegistry;
    private T myRegisteredService;
    private Certificate<Reference> myCertificate;
    private Set<String> myRegistrationClassNames;

    public DefaultRegistrationStrategy(
            Set<String> classNames, Map<String,String> registrationProperties){
        if(classNames == null){
            throw new NullPointerException();
        }
        myRegistrationClassNames = classNames;
        myRegistrationProperties = registrationProperties;
    }
    
    @Override
    public Map<String, String> getRegistrationProperties(T service) {
        return myRegistrationProperties;
    }

    @Override
    public void register(T service) {
        if(myCertificate != null || service == null){
            return;
        }
        RegistrationRequest req = new BasicRegistrationRequest<T>(
                service, myRegistrationClassNames, 
                getRegistrationProperties(service));
        myCertificate = myRegistry.register(req);
        if(myCertificate != null){
            myRegisteredService = service;
        }
    }

    @Override
    public void updateRegistration(T service) {
        if(service == null){
            unregister();
            return;
        }else if(myCertificate == null){
            register(service);
            return;
        }else if(myRegisteredService == service){
            return;
        }
        RegistrationRequest req = new BasicRegistrationRequest<T>(
                service, myRegistrationClassNames, 
                getRegistrationProperties(service));
        Certificate cert = myRegistry.register(req);
        unregister();
        myCertificate = cert;
        myRegisteredService = myCertificate == null ? null : service;
    }

    @Override
    public void unregister() {
        myRegistry.unregister(myCertificate);
        myCertificate = null;
    }
    
    @Override
    public boolean isRegistered(){
        return myRegisteredService != null;
    }
}
