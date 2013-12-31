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

import java.util.LinkedList;
import java.util.List;
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
 * Listens to the OSGi Service Registry for a single service, and provides
 * service registration hooks.
 * 
 * @param <T> type of service to listen for
 * @author Matthew Stevenson <www.robokind.org>
 */
public class SingleServiceListener<T> 
        extends PropertyChangeNotifier implements ServiceListener{
    private final static Logger theLogger = Logger.getLogger(SingleServiceListener.class.getName());
    
    /**
     * Property change event name for the service becoming available
     */
    public final static String PROP_SERVICE_TRACKED = "serviceTracked";
    /**
     * Property change event name for the service being modified
     */
    public final static String PROP_SERVICE_MODIFIED = "serviceModified";
    /**
     * Property change event name for the service becoming unavailabe
     */
    public final static String PROP_SERVICE_REMOVED = "serviceRemoved";
    
    private Class<T> myClass;
    private String myFilter;
    private T myTrackedClass;
    private ServiceReference myReference;
    private BundleContext myContext;
    private List<ServiceReference> myReferences;
    private boolean myStartFlag;

    /**
     * Creates a new SingleServiceListener.
     * @param clazz class of the service
     * @param context BundleContext for accessing the Service Registry
     * @param serviceFilter optional OSGi filter String to match
     * 
     * @throws NullPointerException if clazz or context are null
     */
    public SingleServiceListener(
            Class<T> clazz, BundleContext context, String serviceFilter){
        if(clazz == null || context == null){
            throw new NullPointerException();
        }
        myContext = context;
        myReferences = new LinkedList<ServiceReference>();
        myClass = clazz;
        myFilter = serviceFilter;
        myStartFlag = false;
        
    }
    
    /**
     * Returns a reference to the service being tracked.  Returns null if the
     * service is not available.
     * @return reference to the service being tracked, null if the
     * service is not available
     */
    public ServiceReference getServiceReference(){
        ensureTracking();
        return myReference;
    }
    
    /**
     * Returns the class of the service 
     * @return class of the service
     */
    public Class<T> getServiceClass(){
        return myClass;
    }
    
    /**
     * Returns the optional OSGi filter string, null if not set.
     * @return optional OSGi filter string, null if not set
     */
    public String getFilterString(){
        return myFilter;
    }
    
    /**
     * Returns the service being tracked, null if no service is found.
     * @return service being tracked, null if no service is found
     */
    public T getService(){
        if(!ensureTracking()){
            return null;
        }
        return myTrackedClass;
    }
    
    private boolean ensureTracking(){
        if(myTrackedClass != null){
            return true;
        }
        if(myReference != null){
            ServiceReference ref = myReference;
            myReference = null;
            if(track(ref)){
                return true;
            }
        }
        for(ServiceReference ref : myReferences){
            if(track(ref)){
                return true;
            }
        }
        return false;
    }
    
    /**
     * Releases the service, decrementing the usage count for the 
     * ServiceReference being tracked.
     * Notifies listeners that the service is no longer available.
     */
    public void releaseService(){
        if(myReference != null){
            untrackWithEvent(myReference);
        }
    }
    
    /**
     * Starts listening for the desired services.
     * @return true is successful, false if it failed
     */
    public boolean start(){
        if(myContext == null){
            return false;
        }else if(myStartFlag){
            return true;
        }
        myStartFlag = startListening() && collectServiceReferences();
        return myStartFlag;
    }
    
    private boolean startListening(){
        String listenerFilter = "(" + Constants.OBJECTCLASS + "=" + 
                myClass.getName() + ")";
        boolean empty = (myFilter == null || myFilter.isEmpty());
        listenerFilter = empty ? listenerFilter : "(&" + listenerFilter + 
                myFilter + ")";
        try{
            myContext.addServiceListener(this, listenerFilter);
            return true;
        }catch(InvalidSyntaxException ex){
            theLogger.log(Level.WARNING, "Could not register ServiceListener. "
                    + "Invalid filter syntax.", ex);
            return false;
        }
    }
    
    private boolean collectServiceReferences(){
        ServiceReference[] refs;
        try{ 
            refs = myContext.getServiceReferences(myClass.getName(),myFilter);
        }catch(InvalidSyntaxException ex){
            theLogger.log(Level.SEVERE, 
                    "There was an error fetching service references.", ex);
            return false;
        }
        if(refs == null){
            return true;
        }
        for(ServiceReference ref : refs){
            if(!myReferences.contains(ref)){
                addService(ref);
            }
        }
        return true;
    }
    
    /**
     * Releases the service and stops listening.
     */
    public void stop(){
        stopListening();
        releaseService();
        myReferences.clear();
        myStartFlag = false;
    }
    
    /**
     * Releases the service, stops listening to the Serivce Registry, and
     * removes all PropertyChangeListeners.
     */
    public void dispose(){
        stop();
        clearAllListeners();
    }

    private void stopListening(){
        try{
            myContext.removeServiceListener(this);
        }catch(IllegalStateException ex){
            theLogger.log(Level.WARNING, "BundleContext not valid.", ex);
        }
    }
    
    @Override
    public void serviceChanged(ServiceEvent se) {
        ServiceReference ref = se.getServiceReference();
        switch(se.getType()){
            case ServiceEvent.REGISTERED: addService(ref); break;
            case ServiceEvent.MODIFIED_ENDMATCH:
            case ServiceEvent.UNREGISTERING: removeService(ref); break;
            case ServiceEvent.MODIFIED: modified(ref); break;
        }
    }

    private void addService(ServiceReference ref){
        if(!myReferences.contains(ref)){
            myReferences.add(ref);
        }
        if(myReference == null){
            track(ref);
        }
    }
    
    private void removeService(ServiceReference ref){
        myReferences.remove(ref);
        if(myReferences.isEmpty()){
            untrackWithEvent(ref);
        }else{
            track(myReferences.get(0));
        }
    }
    
    private void modified(ServiceReference ref){
        if(ref == null || !ref.equals(myReference)){
            return;
        }
        firePropertyChange(PROP_SERVICE_MODIFIED, null, myTrackedClass);
    }
    
    private void untrackWithEvent(ServiceReference ref){
        if(myReference == null || !myReference.equals(ref)){
            return;
        }
        try{
            myContext.ungetService(myReference);
        }catch(Exception ex){
            theLogger.log(Level.WARNING, "Error ungetting service", ex);
        }
        myReference = null;
        T old = myTrackedClass;
        myTrackedClass = null;
        firePropertyChange(PROP_SERVICE_REMOVED, null, old);
    }
    
    private void untrack(ServiceReference ref){
        if(myReference == null || !myReference.equals(ref)){
            return;
        }
        try{
            myContext.ungetService(myReference);
        }catch(Exception ex){
            theLogger.log(Level.WARNING, "Error ungetting service", ex);
        }
        myReference = null;
        myTrackedClass = null;
    }
    
    private boolean track(ServiceReference ref){
        if(ref == null){
            throw new NullPointerException();
        }
        if(ref.equals(myReference)){
            return true;
        }
        T tracked = OSGiUtils.getService(myClass, myContext, ref);
        if(tracked == null){
            return false;
        }
        T old = myTrackedClass;
        untrack(myReference);
        myReference = ref;
        myTrackedClass = tracked;
        firePropertyChange(PROP_SERVICE_TRACKED, old, myTrackedClass);
        return true;
    }
}
