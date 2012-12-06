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
import org.jflux.api.core.Notifier;
import org.jflux.api.registry.Accessor;
import org.jflux.api.registry.opt.Modification;
import org.jflux.api.registry.opt.RegistrationRequest;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * A bare OSGi accessor with no JFlux wrappers.
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
    public ServiceRegistration register(
            RegistrationRequest<?, String, String> request) {
        String[] classNames = null;
        Dictionary props = null;
        Object item = request.getItem();
        return myContext.registerService(classNames, item, props);
    }

    @Override
    public void unregister(ServiceRegistration cert) {
        cert.unregister();
    }

    @Override
    public ServiceRegistration modify(
            Modification<ServiceRegistration, String, String> request) {
        ServiceRegistration reg = request.getCertificate();
        Dictionary<String,String> props = 
                new Hashtable<String, String>(request.getProperties());
        reg.setProperties(props);
        return reg;
    }

    /**
     * Not supported yet.
     * @return
     */
    @Override
    public Adapter<RegistrationRequest<?, String, String>, Notifier<ServiceRegistration>> registerAsync() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Not supported yet.
     * @return
     */
    @Override
    public Adapter<Modification<ServiceRegistration, String, String>, Notifier<ServiceRegistration>> modifyAsync() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
