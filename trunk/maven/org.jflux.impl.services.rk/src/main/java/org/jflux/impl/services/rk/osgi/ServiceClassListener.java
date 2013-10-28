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

package org.jflux.impl.services.rk.osgi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jflux.impl.services.rk.property.PropertyChangeNotifier;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

/**
 *
 * @author Matthew Stevenson <www.robokind.org>
 */
public class ServiceClassListener<T> extends 
        PropertyChangeNotifier implements ServiceListener{
    private final static Logger theLogger = Logger.getLogger(ServiceClassListener.class.getName());
    /**
     * Fired when a new service is available
     */
    public final static String PROP_SERVICE_ADDED = "serviceAdded";
    /**
     * Fired when a service is no longer available
     */
    public final static String PROP_SERVICE_REMOVED = "serviceRemoved";
    
    private Class<T> myClass;
    private BundleContext myContext;
    private Map<ServiceReference,T> myReferenceMap;
    private List<ServiceReference> myReferences;
    private String myFilter;

    /**
     *
     * @param context
     * @param list
     */
    public ServiceClassListener(
            Class<T> clazz, BundleContext context, String serviceFilter){
        if(clazz == null || context == null){
            throw new NullPointerException();
        }
        myContext = context;
        myReferenceMap = new HashMap();
        myClass = clazz;
        String filter = 
                "(" + Constants.OBJECTCLASS + "=" + myClass.getName() + ")";
        boolean empty = (serviceFilter == null || serviceFilter.isEmpty());
        myFilter = empty ? filter : "(&" + filter + serviceFilter + ")";
    }
    
    public void start(){
        try{
            myContext.addServiceListener(this, myFilter);
        }catch(InvalidSyntaxException ex){
            theLogger.log(Level.WARNING, 
                    "Could not register ServiceListener.  "
                    + "Invalid filter syntax.", ex);
        }
        myReferences = new ArrayList<ServiceReference>();
        try{
            ServiceReference[] refs = 
                    myContext.getServiceReferences(myClass.getName(), myFilter);
            if(refs == null || refs.length == 0){
                return;
            }
            for(ServiceReference se : refs){
                addService(se);
            }
        }catch(InvalidSyntaxException ex){
            theLogger.log(Level.SEVERE, 
                    "There was an error fetching service references.", ex);
        }
    }
    
    public void stop(){
        if(myContext == null){
            return;
        }
        for(ServiceReference r : myReferences){
            try{
                myContext.ungetService(r);
            }catch(RuntimeException ex){}
        }
        try{
            myContext.removeServiceListener(this);
            
        }catch(RuntimeException ex){}
        myReferences.clear();
        myReferenceMap.clear();
    }

    @Override
    public void serviceChanged(ServiceEvent se) {
        ServiceReference ref = se.getServiceReference();
        switch(se.getType()){
            case ServiceEvent.REGISTERED: addService(ref); break;
            case ServiceEvent.MODIFIED_ENDMATCH:
            case ServiceEvent.UNREGISTERING: removeService(ref); break;
        }
    }

    private void addService(ServiceReference ref){
        T t = getService(ref);
        if(t == null){
            return;
        }
        if(!myReferenceMap.containsKey(ref)){
            myReferenceMap.put(ref,t);
            myReferences.add(ref);
            addService(t);
            firePropertyChange(PROP_SERVICE_ADDED, null, t);
        }
    }
    
    protected void addService(T t){};

    private void removeService(ServiceReference ref){
        T t = myReferenceMap.remove(ref);
        myReferences.remove(ref);
        if(t == null){
            return;
        }
        removeService(t);
        myContext.ungetService(ref);
        firePropertyChange(PROP_SERVICE_REMOVED, null, t);
    }
    
    protected void removeService(T t){};
    
    public List<ServiceReference> getServiceReferences(){
        return myReferences;
    }

    private T getService(ServiceReference se) {
        if (se == null || myContext == null) {
            return null;
        }
        Object obj = myContext.getService(se);
        if (!myClass.isInstance(obj)) {
            return null;
        }
        T t = (T) obj;
        return t;
    }
    
    public T getTopService(){
        if(myReferences.isEmpty()){
            return null;
        }
        return myReferenceMap.get(myReferences.get(0));
    }
}
