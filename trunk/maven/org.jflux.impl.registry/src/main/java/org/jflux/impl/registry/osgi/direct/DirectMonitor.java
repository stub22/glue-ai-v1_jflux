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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jflux.api.core.Listener;
import org.jflux.api.core.Notifier;
import org.jflux.api.core.util.DefaultNotifier;
import org.jflux.api.registry.Monitor;
import org.jflux.api.registry.opt.Descriptor;
import org.jflux.impl.registry.osgi.util.OSGiRegistryUtil;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;

/**
 *
 * @author Matthew Stevenson
 */
public class DirectMonitor<Time> implements Monitor<
        Descriptor<String,String>, ServiceEvent> {
    private final static Logger theLogger = Logger.getLogger(DirectMonitor.class.getName());
    private BundleContext myContext;
    
    DirectMonitor(BundleContext context){
        if(context == null){
            throw new NullPointerException();
        }
        myContext = context;
    }

    @Override
    public Notifier<ServiceEvent> getNotifier(Descriptor<String,String> desc) {
        String filter = OSGiRegistryUtil.getFullFilter(desc);
        ServiceEventNotifier notifier = new ServiceEventNotifier();
        try{
            myContext.addServiceListener(notifier, filter);
            return notifier;
        }catch(InvalidSyntaxException ex){
            theLogger.log(Level.SEVERE, "Invalid LDAP filter: " + filter, ex);
            return null;
        }
    }

    @Override
    public Listener<Descriptor<String,String>> releaseNotifier() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private class ServiceEventNotifier extends 
            DefaultNotifier<ServiceEvent> implements ServiceListener{
        @Override
        public void serviceChanged(ServiceEvent event) {
            notifyListeners(event);
        }
        
    }
}