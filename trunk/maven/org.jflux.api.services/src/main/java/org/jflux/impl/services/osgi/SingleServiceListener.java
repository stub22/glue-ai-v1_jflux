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
package org.jflux.impl.services.osgi;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jflux.api.core.Listener;
import org.jflux.api.registry.basic.BasicRegistryEvent;
import org.jflux.api.registry.Descriptor;
import org.jflux.api.registry.Reference;
import org.jflux.api.registry.Registry;
import org.jflux.api.registry.RegistryEvent;
import org.jflux.api.services.extras.PropertyChangeNotifier;

/**
 * Listens to the OSGi Service Registry for a single service, and provides
 * service registration hooks.
 * 
 * @param <T> type of service to listen for
 * @author Matthew Stevenson <www.robokind.org>
 */
public class SingleServiceListener<T> 
        extends PropertyChangeNotifier implements Listener<RegistryEvent>{
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
    
    private Descriptor myDescriptor;
    private T myTrackedClass;
    private Reference myReference;
    private Registry myContext;
    private List<Reference> myReferences;
    private boolean myStartFlag;

    /**
     * Creates a new SingleServiceListener.
     * @param descriptor the Descriptor of the service to listen for
     * @param context BundleContext for accessing the Service Registry
     * @throws NullPointerException if descriptor or context are null
     */
    public SingleServiceListener(
            Registry context, Descriptor descriptor){
        if(descriptor == null || context == null){
            throw new NullPointerException();
        }
        myContext = context;
        myReferences = new LinkedList<Reference>();
        myDescriptor = descriptor;
        myStartFlag = false;
        
    }
    
    /**
     * Returns a reference to the service being tracked.
     * @return reference to the service being tracked, null if the service is
     * not available
     */
    public Reference getServiceReference(){
        ensureTracking();
        return myReference;
    }
    
    /**
     * Returns the descriptor of the service 
     * @return descriptor of the service
     */
    public Descriptor getServiceDescriptor(){
        return myDescriptor;
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
            Reference ref = myReference;
            myReference = null;
            if(track(ref)){
                return true;
            }
        }
        for(Reference ref : myReferences){
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
        myContext.addListener(myDescriptor, this);
        return true;
    }
    
    private boolean collectServiceReferences(){
        List<Reference> refs = myContext.findAll(myDescriptor);
        if(refs == null){
            return true;
        }
        for(Reference ref : refs){
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
            myContext.removeListener(this);
        }catch(IllegalStateException ex){
            theLogger.log(Level.WARNING, "BundleContext not valid.", ex);
        }
    }

    /**
     * Responds to a service changing state.
     * @param input the state change
     */
    @Override
    public void handleEvent(RegistryEvent input) {
        if(input == null || input.getReference() == null){
            return;
        }
        int type = input.getEventType();
        if(RegistryEvent.REGISTERED == type) {
            addService(input.getReference());
        } else if(RegistryEvent.UNREGISTERING == type
                || RegistryEvent.MODIFIED_ENDMATCH == type) {
            removeService(input.getReference());
        } else if(RegistryEvent.MODIFIED == type) {
            modified(input.getReference());
        }
    }

    private void addService(Reference ref){
        if(!myReferences.contains(ref)){
            myReferences.add(ref);
        }
        if(myReference == null){
            track(ref);
        }
    }
    
    private void removeService(Reference ref){
        myReferences.remove(ref);
        if(myReferences.isEmpty()){
            untrackWithEvent(ref);
        }else{
            track(myReferences.get(0));
        }
    }
    
    private void modified(Reference ref){
        if(ref == null || !ref.equals(myReference)){
            return;
        }
        firePropertyChange(PROP_SERVICE_MODIFIED, null, myTrackedClass);
    }
    
    private void untrackWithEvent(Reference ref){
        if(myReference == null || !myReference.equals(ref)){
            return;
        }
        try{
            myContext.release(myReference);
        }catch(Exception ex){
            theLogger.log(Level.WARNING, "Error ungetting service", ex);
        }
        myReference = null;
        T old = myTrackedClass;
        myTrackedClass = null;
        firePropertyChange(PROP_SERVICE_REMOVED, null, old);
    }
    
    private void untrack(Reference ref){
        if(myReference == null || !myReference.equals(ref)){
            return;
        }
        try{
            myContext.release(myReference);
        }catch(Exception ex){
            theLogger.log(Level.WARNING, "Error ungetting service", ex);
        }
        myReference = null;
        myTrackedClass = null;
    }
    
    private boolean track(Reference ref){
        if(ref == null){
            throw new NullPointerException();
        }
        if(ref.equals(myReference)){
            return true;
        }
        T tracked = (T)myContext.retrieve(ref);
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
