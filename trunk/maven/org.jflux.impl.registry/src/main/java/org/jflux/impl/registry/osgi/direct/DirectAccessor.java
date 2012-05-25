/*
 * Copyright 2012 The JFlux Project (www.jflux.org).
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
package org.jflux.impl.registry.osgi.direct;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.logging.Logger;
import org.jflux.api.core.Adapter;
import org.jflux.api.core.Listener;
import org.jflux.api.registry.Accessor;
import org.jflux.api.registry.opt.Modification;
import org.jflux.api.registry.opt.RegistrationRequest;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 *
 * @author Matthew Stevenson
 */
public class DirectAccessor implements Accessor<
            RegistrationRequest<?, String, String>, 
            ServiceRegistration, 
            Modification<ServiceRegistration, String, String>>{
    private final static Logger theLogger = Logger.getLogger(DirectAccessor.class.getName());
    private BundleContext myContext;
    
    DirectAccessor(BundleContext context){
        if(context == null){
            throw new NullPointerException();
        }
        myContext = context;
    }

    @Override
    public Adapter<
            RegistrationRequest<?, String, String>, 
            ServiceRegistration> register() {
        return new Adapter<
                RegistrationRequest<?, String, String>, 
                ServiceRegistration>() {

            @Override
            public ServiceRegistration adapt(
                    RegistrationRequest<?, String, String> a) {
                String[] classNames = null;
                Dictionary props = null;
                Object item = a.getItem();
                return myContext.registerService(classNames, item, props);
            }
        };
    }

    @Override
    public Listener<ServiceRegistration> unregister() {
        return new Listener<ServiceRegistration>() {
            @Override
            public void handleEvent(ServiceRegistration event) {
                event.unregister();
            }
        };
    }

    @Override
    public Adapter<
            Modification<ServiceRegistration, String, String>, 
            ServiceRegistration> modify() {
        return new Adapter<
                Modification<ServiceRegistration, String, String>, 
                ServiceRegistration>() {

            @Override
            public ServiceRegistration adapt(
                    Modification<ServiceRegistration, String, String> a) {
                ServiceRegistration reg = a.getCertificate();
                Dictionary<String,String> props = 
                        new Hashtable<String, String>(a.getProperties());
                reg.setProperties(props);
                return reg;
            }
        };
    }
}
