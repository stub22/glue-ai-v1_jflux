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
package org.jflux.impl.services.osgi.lifecycle;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jflux.api.registry.opt.RegistryContext;
import org.jflux.api.services.DependencyDescriptor;
import org.jflux.api.services.DependencyDescriptor.DependencyType;
import org.jflux.api.services.extras.PropertyChangeNotifier;
import org.jflux.impl.services.osgi.SingleServiceListener;

/**
 * Monitors the OSGi Service Registry for a set of service dependencies.  
 * Fires property change events when all of the dependencies are available, and 
 * as the dependencies change.
 * 
 * Used with DynamicServiceLauncher and ServiceLifecycleProvider to create 
 * services with OSGi-driven lifecycles.
 * 
 * @author Matthew Stevenson <www.robokind.org>
 */
public class ServiceDependenciesTracker extends PropertyChangeNotifier{
    private final static Logger theLogger = 
            Logger.getLogger(ServiceDependenciesTracker.class.getName());
    /**
     * Property change event name for a dependency becoming available.
     */
    public final static String PROP_DEPENDENCY_AVAILABLE = "dependencyAvailable";
    /**
     * Property change event name for a dependency changing.
     */
    public final static String PROP_DEPENDENCY_CHANGED = "dependencyChanged";
    /**
     * Property change event name for a dependency becoming unavailable.
     */
    public final static String PROP_DEPENDENCY_UNAVAILABLE = "dependencyUnavailable";
    /**
     * Property change event name for all dependencies being available.
     */
    public final static String PROP_ALL_DEPENDENCIES_AVAILABLE = "allDependenciesAvailable";
    
    private RegistryContext myContext;
    private int myRequiredCount;
    private Map<String,SingleServiceListener> myDependencyTrackers;
    private Map<String,DependencyDescriptor> myDependencyDescMap;
    private Map<String,Object> myAvailableDependencies;
    private Map<String,Object> myRequiredDependencies;
    private boolean myListeningFlag;
    
    /**
     * Creates an empty ServiceDependenciesTracker with the given BundleContext.
     * @param context 
     */
    public ServiceDependenciesTracker(RegistryContext context){
        if(context == null){
            throw new NullPointerException();
        }
        myContext = context;
        myDependencyTrackers = new HashMap<String, SingleServiceListener>();
        myAvailableDependencies = new HashMap<String, Object>();
        myDependencyDescMap = new HashMap<String, DependencyDescriptor>();
        myRequiredDependencies = new HashMap<String, Object>();
        myListeningFlag = false;
    }
    
    /**
     * Returns true if all dependencies are available.
     * @return true if all dependencies are available
     */
    public boolean dependenciesSatisfied(){
        return myRequiredCount == myRequiredDependencies.size();
    }
    /**
     * Returns a map of dependency Ids dependencies.  Returns null unless all
     * dependencies are available.
     * @return map of dependency Ids dependencies, null unless all
     * dependencies are available
     */
    public Map<String,Object> getAvailableDependencies(){
        return myAvailableDependencies;
    }
    /**
     * Returns a map of dependency Ids and required dependencies.  Returns null 
     * unless all
     * dependencies are available.
     * @return map of dependency Ids dependencies, null unless all
     * dependencies are available
     */
    public Map<String,Object> getRequiredDependencies(){
        return myRequiredDependencies;
    }
    
    /**
     * Returns the dependency matching the given id, null if unavailable.
     * @param dependencyId local id used with a ServiceLifecycleProvider
     * @return service matching the DependencyDescriptor with the given id, null
     * if the dependency is unavailable
     */
    public Object getDependency(String dependencyId){
        return myAvailableDependencies.get(dependencyId);
    }
    /**
     * Adds the description to the list of dependency to listen for.
     * @param descriptor dependency description to listen for
     * @return 
     * @throws IllegalStateException if the tracker has already been started
     * @throws IllegalArgumentException if the given dependencyId already exists
     */
    public boolean addDependencyDescription(DependencyDescriptor descriptor){
        if(descriptor == null){
            throw new NullPointerException();
        }
        String dependencyName = descriptor.getDescriptorName(); 
        if(myDependencyDescMap.containsKey(dependencyName)){
            theLogger.log(Level.WARNING, 
                    "Unable to add dependency, name already in use: {0}.", 
                    dependencyName);
            return false;
        }
        SingleServiceListener ssl =
                new SingleServiceListener(myContext, descriptor);
        ssl.addPropertyChangeListener(new RequirementListener(dependencyName));
        myDependencyTrackers.put(dependencyName, ssl);
        myDependencyDescMap.put(dependencyName, descriptor);
        if(isRunning()){
            return ssl.start();
        }
        return true;
    }
    /**
     * Removes a tracker.
     * @param name the name of the tracker to remove
     * @return true if the removal was successful
     */
    public boolean removeDependencyTracker(String name){
        if(name == null){
            throw new NullPointerException();
        }
        SingleServiceListener ssl = myDependencyTrackers.remove(name);
        if(ssl == null){
            return false;
        }
        ssl.dispose();
        myDependencyDescMap.remove(name);
        myAvailableDependencies.remove(name);
        myRequiredDependencies.remove(name);
        return true;
    }
    /**
     * Start tracking dependencies.
     */
    public void start(){
        myListeningFlag = true;
        myRequiredCount = 0;
        for(DependencyDescriptor desc : myDependencyDescMap.values()){
            if(DependencyType.REQUIRED == desc.getDependencyType()){
                myRequiredCount++;
            }
        }
        for(SingleServiceListener ssl : myDependencyTrackers.values()){
            ssl.start();
        }
    }
    
    /**
     * Determines if the tracker is running.
     * @return true if the tracker is running
     */
    public boolean isRunning(){
        return myListeningFlag;
    }
    /**
     * Stop tracking dependencies.
     */
    public void stop(){
        myListeningFlag = false;
        myAvailableDependencies.clear();
        for(SingleServiceListener ssl : myDependencyTrackers.values()){
            ssl.stop();
        }
    }
    
    /**
     * Destroys this tracker.
     */
    public void dispose(){
        myListeningFlag = false;
        this.clearAllListeners();
        for(SingleServiceListener ssl : myDependencyTrackers.values()){
            ssl.dispose();
        }
        myAvailableDependencies.clear();
        myRequiredDependencies.clear();
        myDependencyDescMap.clear();
        myDependencyTrackers.clear();
        myRequiredCount = 0;
        myContext = null;
    }
    
    private synchronized void dependencyFound(String requirementId, Object req){
        if(requirementId == null || req == null){
            throw new NullPointerException();
        }
        DependencyDescriptor desc = myDependencyDescMap.get(requirementId);
        if(desc == null){
            return;
        }else if(DependencyType.REQUIRED == desc.getDependencyType()){
            theLogger.log(Level.INFO, 
                    "Found required dependency: {0}", requirementId);
            myRequiredDependencies.put(requirementId, req);
        }else{
            theLogger.log(Level.INFO, 
                    "Found optional dependency: {0}", requirementId);
        }
        myAvailableDependencies.put(requirementId, req);
        firePropertyChange(PROP_DEPENDENCY_AVAILABLE, requirementId, req);
        checkRequiredDependencies();
    }
    
    private void checkRequiredDependencies(){
        if(!dependenciesSatisfied()){
            return;
        }
        theLogger.log(Level.INFO, "All requirements present: {0}", 
                Arrays.toString(myDependencyDescMap.keySet().toArray()));
        firePropertyChange(PROP_ALL_DEPENDENCIES_AVAILABLE, null, getAvailableDependencies());
        
    }
    
    private synchronized void dependencyChanged(String requirementId, Object req){
        if(requirementId == null || req == null){
            throw new NullPointerException();
        }DependencyDescriptor desc = myDependencyDescMap.get(requirementId);
        if(desc == null){
            return;
        }else if(DependencyType.REQUIRED == desc.getDependencyType()){
            theLogger.log(Level.INFO, 
                    "Required dependency changed: {0}", requirementId);
            myRequiredDependencies.put(requirementId, req);
        }else{
            theLogger.log(Level.INFO, 
                    "Optional dependency changed: {0}", requirementId);
        }
        myAvailableDependencies.put(requirementId, req);
        firePropertyChange(PROP_DEPENDENCY_CHANGED, requirementId, req);
    }
    
    private synchronized void dependencyLost(String requirementId){
        if(requirementId == null){
            throw new NullPointerException();
        }
        DependencyDescriptor desc = myDependencyDescMap.get(requirementId);
        if(desc == null){
            return;
        }else if(DependencyType.REQUIRED == desc.getDependencyType()){
            myRequiredDependencies.remove(requirementId);
            theLogger.log(Level.INFO, 
                    "Lost required dependency: {0}", requirementId);
        }else{
            theLogger.log(Level.INFO, 
                    "Lost optional dependency: {0}", requirementId);
        }
        myAvailableDependencies.remove(requirementId);
        firePropertyChange(PROP_DEPENDENCY_UNAVAILABLE, requirementId, null);
    }
    
    class RequirementListener implements PropertyChangeListener {
        private String myRequirementId;
        
        public RequirementListener(String requirementId){
            myRequirementId = requirementId;
        }
        
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if(SingleServiceListener.PROP_SERVICE_TRACKED.equals(evt.getPropertyName())){
                track(evt.getOldValue(), evt.getNewValue());
            }else if(SingleServiceListener.PROP_SERVICE_REMOVED.equals(evt.getPropertyName())){
                untrack(evt.getNewValue());
            }
        }
        
        private void track(Object oldVal, Object newVal){
            if(oldVal == null){
                dependencyFound(myRequirementId, newVal);
            }else{
                dependencyChanged(myRequirementId, newVal);
            }
        }
        
        private void untrack(Object obj){
            dependencyLost(myRequirementId);
        }
    }
}
