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
package org.jflux.api.service.binding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jflux.api.core.Listener;
import org.jflux.api.registry.Descriptor;
import org.jflux.api.registry.Reference;
import org.jflux.api.registry.Registry;
import org.jflux.api.registry.RegistryEvent;

/**
 *
 * @author matt
 */
public class ReferenceTracker<T> {
    private final static Logger theLogger = Logger.getLogger(ReferenceTracker.class.getName());
    private Registry myRegistry;
    private List<Reference> myCachedReferences;
    private Map<Reference,T> myTrackedServices;
    private List<Reference> myTrackedReferences;
    private List<T> myServices;
    private RegistryListener myRegistryListener;
    private boolean myStartFlag;

    public ReferenceTracker(){
        myStartFlag = false;
        myCachedReferences = new ArrayList<Reference>();
        myTrackedReferences = new ArrayList<Reference>();
        myTrackedServices = new HashMap<Reference, T>();
        myServices = new ArrayList<T>();
    }
    
    public void start(Registry reg, Descriptor desc){
        if(myStartFlag){
            return;
        }
        myRegistry = reg;
        myRegistryListener = new RegistryListener();
        List<Reference> refs = myRegistry.findAll(desc);
        myRegistry.addListener(desc, myRegistryListener);
        if(refs != null){
            for(Reference ref : refs){
                addReference(ref);
            }
        }
        myStartFlag = true;
    }
    
    public void stop(){
        myRegistry.removeListener(myRegistryListener);
        Reference[] refs = myCachedReferences.toArray(new Reference[0]);
        for(Reference ref : refs){
            removeReference(ref);
        }
        myStartFlag = false;
    }
    
    public List<Reference> getAvailableReferences(){
        return Collections.unmodifiableList(myCachedReferences);
    }
    
    public List<Reference> getTrackedReferences(){
        return Collections.unmodifiableList(myTrackedReferences);
    }
    
    public synchronized T getTrackedService(Reference ref){
        return myTrackedServices.get(ref);
    }
    
    public synchronized T getService(Reference ref){
        return retrieve(ref);
    }
    
    public synchronized T getService(){
        if(!myTrackedReferences.isEmpty()){
            Reference ref = myTrackedReferences.get(0);
            return myTrackedServices.get(ref);
        }
        for(Reference ref : myCachedReferences){
            T t = getService(ref);
            if(t != null){
                return t;
            }
        }
        return null;
    }
    
    public synchronized List<T> retrieveAllServices(){
        List<T> items = new ArrayList<T>(myCachedReferences.size());
        for(Reference ref : myCachedReferences){
            items.add(retrieve(ref));
        }
        return items;
    }
    
    public List<T> getTrackedServices(){
        return Collections.unmodifiableList(myServices);
    }
    
    protected synchronized void addReference(Reference ref){
        if(!myCachedReferences.contains(ref)){
            myCachedReferences.add(ref);
        }
    }
    
    protected synchronized void removeReference(Reference ref){
        release(ref);
        myCachedReferences.remove(ref);
    }
    
    public synchronized void releaseReference(Reference ref){
        release(ref);
    }
    
    public int getTrackedCount(){
        return myServices.size();
    }
    
    public int getReferenceCount(){
        return myCachedReferences.size();
    }
    
    private T retrieve(Reference reference) {
        if(myRegistry == null || reference == null){
            return null;
        }
        T t = myTrackedServices.get(reference);
        if(t != null){
            return t;
        }
        if(!myCachedReferences.contains(reference)){
//            myCachedReferences.add(reference);
            return null;
        }
        Object obj = myRegistry.retrieve(reference);
        if(obj == null){
            return null;
        }
        try{
            t = (T)obj;
            myTrackedServices.put(reference, t);
            myTrackedReferences.add(reference);
            myServices.add(t);
            return t;
        }catch(ClassCastException ex){
            myRegistry.release(reference);
            theLogger.log(Level.SEVERE, 
                    "Unable to cast item to specified type.  "
                    + "Found: " + obj.getClass().getName(), ex);
            return null;
        }
    }
    
    public boolean isTracked(Reference ref){
        return myTrackedServices.get(ref) != null;
    }
    
    private void release(Reference ref){
        if(myRegistry == null){
            return;
        }
        T t = myTrackedServices.remove(ref);
        if(t == null){
            return;
        }
        int index = myTrackedReferences.indexOf(ref);
        if(index == -1){
            myTrackedReferences.remove(ref);
            myServices.remove(t);
        }else{
            myTrackedReferences.remove(index);
            myServices.remove(index);
        }
        myRegistry.release(ref);
    }
    
    private class RegistryListener implements Listener<RegistryEvent> {
        @Override public void handleEvent(RegistryEvent input) {
            if(input == null || input.getReference() == null){
                return;
            }
            Reference ref = input.getReference();
            switch(input.getEventType()){
                case RegistryEvent.REGISTERED :
                    addReference(ref); break;
                case RegistryEvent.UNREGISTERING :
                case RegistryEvent.MODIFIED_ENDMATCH :
                    removeReference(ref); break;
                case RegistryEvent.MODIFIED :
                default : break;    
            }
        }
    }
}
