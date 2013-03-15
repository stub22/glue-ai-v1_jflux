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
package org.jflux.impl.registry;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jflux.api.core.Listener;
import org.jflux.api.registry.Registry;
import org.jflux.api.registry.opt.Descriptor;
import org.jflux.api.registry.opt.Modification;
import org.jflux.api.registry.opt.RegistrationRequest;
import org.jflux.api.registry.util.FinderUtils;
import org.jflux.impl.registry.osgi.util.OSGiRegistryUtil;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * A bare OSGi registry with no JFlux wrappers.
 * @author Matthew Stevenson
 */
public class OSGiDirectRegistry<Time> implements Registry<
        Descriptor<String,String>, 
        ServiceReference,
        RegistrationRequest<?, String, String>,
        ServiceRegistration,
        Modification<String ,String>, 
        ServiceEvent> {
    private final static Logger theLogger = Logger.getLogger(OSGiDirectRegistry.class.getName());
    
    private BundleContext myContext;
    private Map<Listener<ServiceEvent>,ServiceListener> myListenerMap;

    public OSGiDirectRegistry(BundleContext context){
        if(context == null){
            throw new NullPointerException();
        }
        myContext = context;
        myListenerMap = new HashMap<Listener<ServiceEvent>, ServiceListener>();
    }
    
    @Override
    public ServiceReference findSingle(Descriptor<String, String> desc) {
        ServiceReference[] refs = find(desc);
        if(refs == null){
            return null;
        }
        return refs[0];
    }

    @Override
    public List<ServiceReference> findAll(Descriptor<String, String> desc) {
        ServiceReference[] refs = find(desc);
        if(refs == null){
            return null;
        }
        return Arrays.asList(refs);
    }

    @Override
    public List<ServiceReference> findCount(
            Descriptor<String, String> desc, int max) {
        return FinderUtils.findCount(this, desc, max);
    }
    
    private ServiceReference[] find(Descriptor<String, String> a) {
        String className = a.getClassName();
        String filter = OSGiRegistryUtil.getPropertiesFilter(a);
        try{
            return myContext.getServiceReferences(className, filter);
        }catch(InvalidSyntaxException ex){
            theLogger.log(Level.SEVERE, "Invalid LDAP filter: " + filter, ex);
            return null;
        }
    }
    
    @Override
    public <T> T retrieve(Class<T> clazz, ServiceReference reference) {
        Object obj = myContext.getService(reference);
        if(obj == null){
            return null;
        }
        try{
            return (T)obj;
        }catch(ClassCastException ex){
            theLogger.log(Level.SEVERE, 
                    "Unable to cast item to specified type.  "
                    + "Expected: " + clazz.getName() + ",  "
                    + "Found: " + obj.getClass().getName(), ex);
            return null;
        }
    }

    @Override
    public Object retrieve(ServiceReference reference) {
        return myContext.getService(reference);
    }

    @Override
    public void release(ServiceReference reference) {
        myContext.ungetService(reference);
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
    public void modify(
            ServiceRegistration cert,
            Modification<String, String> request) {
        Dictionary<String,String> props = 
                new Hashtable<String, String>(request.getProperties());
        cert.setProperties(props);
    }

    @Override
    public synchronized void addListener(Descriptor<String, String> desc, Listener<ServiceEvent> listener) {
        String filter = OSGiRegistryUtil.getFullFilter(desc);
        ServiceListener sl = wrapServiceListener(listener);
        try{
            myContext.addServiceListener(sl, filter);
        }catch(InvalidSyntaxException ex){
            theLogger.log(Level.SEVERE, 
                    "Invalid LDAP filter: " + filter, ex);
        }
    }

    @Override
    public synchronized void removeListener(Listener<ServiceEvent> listener) {
        ServiceListener sl = wrapServiceListener(listener);
        myContext.removeServiceListener(sl);
    }
    
    private ServiceListener wrapServiceListener(final Listener<ServiceEvent> listener){
        ServiceListener sl = myListenerMap.get(listener);
        if(sl == null){
            sl = new ServiceListener() {
                @Override public void serviceChanged(ServiceEvent se) {
                    listener.handleEvent(se);
                }};
            myListenerMap.put(listener, sl);
        }
        return sl;
    }
}
