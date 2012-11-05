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
import java.util.*;
import java.util.Map.Entry;
import org.jflux.api.services.extras.PropertyChangeNotifier;
import org.jflux.api.services.lifecycle.DependencyDescriptor;
import org.jflux.api.services.lifecycle.ManagedService;
import org.jflux.api.services.lifecycle.ServiceLifecycleProvider;
import static org.jflux.api.services.lifecycle.ServiceLifecycleProvider.PROP_DEPENDENCY_ADDED;
import static org.jflux.api.services.lifecycle.ServiceLifecycleProvider.PROP_DEPENDENCY_REMOVED;
import org.jflux.api.services.lifecycle.ServiceLifecycleProvider.Validator;
import org.jflux.impl.services.osgi.OSGiUtils;
import static org.jflux.impl.services.osgi.lifecycle.ServiceDependenciesTracker.*;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An OSGi Service managed with a ServiceLifecycleProvider.  
 * Creates a ServiceDependenciesTracker to drive the service lifecycle from the 
 * OSGi Service Registry.  The the service lifecycle changes, the OSGiComponent 
 * registers/unregisters the managed service in the OSGi ServiceRegistry.
 * An OSGiComponent Registers itself to the OSGi ServiceRegistry when started.
 * 
 * @param <T> type of the service to be launched
 * @author Matthew Stevenson <www.robokind.org>
 */
public class OSGiComponent<T> extends 
        PropertyChangeNotifier implements ManagedService<T>{
    private final static Logger theLogger = LoggerFactory.getLogger(OSGiComponent.class);
    private BundleContext myContext;
    private ServiceLifecycleProvider<T> myLifecycleProvider;
    private LifecycleDependencyListener myLifecycleListener;
    private ServiceDependenciesTracker myDependenciesTracker;
    private T myService;
    private ServiceRegistration myServiceRegistration;
    private ServiceRegistration mySelfRegistration;
    private Properties myRegistrationProperties;
    private Properties myUniqueProperties;
    private String[] myRegistrationClassNames;
    private boolean myInitializedFlag;
    private boolean myRegistrationEnabledFlag;
    private boolean mySelfRegistrationEnabledFlag;
    
    /**
     * Creates a new OSGiComponent from the given lifecycle provider.
     * @param context BundleContext for accessing the OSGi Service Registry
     * @param lifecycle lifecycle provider for the managed service 
     */
    public OSGiComponent(BundleContext context, 
            ServiceLifecycleProvider<T> lifecycle){
        this(context, lifecycle, null, null, true);
    }
    /**
     * Creates a new OSGiComponent from the given lifecycle provider.
     * Uses the given registration properties when registering the managed
     * service to the OSGi Service Registry.
     * @param context BundleContext for accessing the OSGi Service Registry
     * @param lifecycle lifecycle provider for the managed service 
     * @param registrationProps optional properties to be used when registering 
     * the managed service to the OSGi Service Registry.  These are combined
     * with any properties from the ServiceLifecycleProvider
     */
    public OSGiComponent(BundleContext context, 
            ServiceLifecycleProvider<T> lifecycle, 
            Properties registrationProps){
        this(context, lifecycle, registrationProps, null, true);
    }
    /**
     * Creates a new OSGiComponent from the given lifecycle provider.
     * Uses the given registration class name and properties when registering 
     * the managed service to the OSGi Service Registry.
     * @param context BundleContext for accessing the OSGi Service Registry
     * @param lifecycle lifecycle provider for the managed service 
     * @param registrationProps optional properties to be used when registering 
     * the managed service to the OSGi Service Registry.  These are combined
     * with any properties from the ServiceLifecycleProvider
     * @param selfRegister determines if this OSGiComponent should register
     * itself when started, by default this is true
     */
    public OSGiComponent(BundleContext context, 
            ServiceLifecycleProvider<T> lifecycle, 
            Properties registrationProps, boolean selfRegister){
        this(context, lifecycle, registrationProps, null, selfRegister);
    }
    /**
     * Creates a new OSGiComponent from the given lifecycle provider.
     * Uses the given registration class name and properties when registering 
     * the managed service to the OSGi Service Registry.
     * @param context BundleContext for accessing the OSGi Service Registry
     * @param lifecycle lifecycle provider for the managed service 
     * @param registrationProps optional properties to be used when registering 
     * the managed service to the OSGi Service Registry.  These are combined
     * with any properties from the ServiceLifecycleProvider
     * @param registrationClassNames class names to be used when registering the
     * managed service to the OSGi Service Registry.  By default, this value is
     * taken from the ServiceLifecycleProvider's getServiceClass().
     */
    public OSGiComponent(BundleContext context, 
            ServiceLifecycleProvider<T> lifecycle, 
            Properties registrationProps, 
            Properties uniqueProps,
            boolean selfRegister){
        if(context == null || lifecycle == null){
            throw new NullPointerException();
        }
        myContext = context;
        myLifecycleProvider = lifecycle;
        List<DependencyDescriptor> descriptors = 
                myLifecycleProvider.getDependencyDescriptors();
        myRegistrationClassNames = myLifecycleProvider.getServiceClassNames();
        myRegistrationProperties = registrationProps;
        myUniqueProperties = uniqueProps;
        myInitializedFlag = false;
        myRegistrationEnabledFlag = true;
        mySelfRegistrationEnabledFlag = selfRegister;
        if(descriptors == null || descriptors.isEmpty()){
            return;
        }
        myDependenciesTracker = new ServiceDependenciesTracker(myContext);
        for(DependencyDescriptor dd : descriptors){
            myDependenciesTracker.addDependencyDescription(
                    dd.getServiceClass(), 
                    dd.getDependencyName(), 
                    dd.getServiceFilter(),
                    dd.getDependencyType());
        }
        myDependenciesTracker.addPropertyChangeListener(
                new DependencyStatusListener());
    }
    /**
     * Starts the OSGiComponent.  Begins tracker dependencies and
     * notifies the lifecycle provider of dependency changes.
     * 
     * The OSGiComponent, by default, adds itself to the OSGi Service Registry.
     * This can be disabled by calling setSelfRegistrationEnabled(false).
     * The OSGiComponent can be unregistered by calling unregisterSelf(), and
     * can be registered by calling registerSelf()
     */
    @Override
    public void start(){
        if(myLifecycleListener == null && myLifecycleProvider != null){
            myLifecycleListener = new LifecycleDependencyListener();
            myLifecycleProvider.addPropertyChangeListener(myLifecycleListener);
        }
        if(mySelfRegistrationEnabledFlag){
            registerSelf();
        }
        if(myDependenciesTracker == null){
            handleAllDependencies(Collections.EMPTY_MAP);
            return;
        }
        if(!myDependenciesTracker.isRunning()){
            myDependenciesTracker.start();
        }
        
    }
    /**
     * Stops tracking dependency changes.  Does not modify the existing service
     * or its OSGi registration state.  Call unregister() to unregister
     * the service being managed.
     */
    @Override
    public void stop(){
        if(myDependenciesTracker != null && myDependenciesTracker.isRunning()){
            myDependenciesTracker.stop();
        }
        stopLifecycle();
    }

    private void stopLifecycle() {
        if(myLifecycleProvider == null){
            return;
        }
        myLifecycleProvider.stop();
        if(myLifecycleListener != null){
            myLifecycleProvider.removePropertyChangeListener(
                    myLifecycleListener);
            myLifecycleListener = null;
        }
    }
    
    @Override
    public void dispose(){
        theLogger.info("Disposing of OSGi Component: {}", this);
        stopLifecycle();
        clearAllListeners();
        if(myDependenciesTracker != null){
            myDependenciesTracker.dispose();
            myDependenciesTracker = null;
        }
        unregister();
        unregisterSelf();
        myContext = null;
        if(myRegistrationProperties != null){
            myRegistrationProperties.clear();
        }
        if(myUniqueProperties != null){
            myUniqueProperties.clear();
        }
        myRegistrationClassNames = null;
        myLifecycleProvider = null;
        myService = null;
    }
    /**
     * If set true, this OSGiComponent will register itself when start() is
     * called, or immediately if start has already been called.  If set false
     * and the component is already registered, it will be unregistered.
     * By default this is set to true.
     * @param enabled true to enable self-registration of the OSGiComponent
     */
    public void setSelfRegistrationEnabled(boolean enabled){
        mySelfRegistrationEnabledFlag = enabled;
        if(mySelfRegistrationEnabledFlag && mySelfRegistration == null){
            registerSelf();
        }else if(!mySelfRegistrationEnabledFlag && 
                myServiceRegistration != null){
            unregisterSelf();
        }
    }
    /**
     * Returns true if this OSGiComponent should register itself.
     * @return true if this OSGiComponent should register itself
     */
    public boolean getSelfRegistrationEnabled(){
        return mySelfRegistrationEnabledFlag;
    }
    /**
     * Add this OSGiComponent to the OSGi Service Registry.
     */
    public void registerSelf(){
        if(mySelfRegistration != null){
            return;
        }
        Properties props = new Properties();
        props.put(ManagedService.PROP_SERVICE_TYPE, getServiceClassNames());
        mySelfRegistration = myContext.registerService(
                ManagedService.class.getName(), this, props);
    }
    /**
     * Removes this OSGiComponent from the OSGi Service Registry
     */
    public void unregisterSelf(){
        if(mySelfRegistration == null){
            return;
        }
        mySelfRegistration.unregister();
        mySelfRegistration = null;
    }
    
    @Override
    public void setRegistrationEnabled(boolean enabled){
        myRegistrationEnabledFlag = enabled;
        if(myRegistrationEnabledFlag && myService != null && 
                myServiceRegistration == null){
            register();
        }else if(!myRegistrationEnabledFlag && myServiceRegistration != null){
            unregister();
        }
    }
    
    @Override
    public boolean getRegistrationEnabled(){
        return myRegistrationEnabledFlag;
    }
    
    @Override
    public void unregister(){
        if(myServiceRegistration != null){
            try{
                myServiceRegistration.unregister();
                myServiceRegistration = null;
                if(myService != null){
                    getLogger().info( 
                            "Service Successfully Unregistered: {}", 
                            myService.toString());
                }else{
                    getLogger().info("Service Successfully Unregistered.");
                }
            }catch(IllegalStateException ex){
                getLogger().info("Service not registered.");
            }
        }
        Object changeKey = new Object();
        try{
            firePropertyChange(PROP_SERVICE_CHANGED, changeKey, this);
        }catch(RuntimeException ex){
            getLogger().warn(
                    "Runtime exception in event handling for "
                    + "service unregistration of " + myService.toString(), ex);
        }
    }
    
    @Override
    public void register(){
        if(myService == null || 
                myServiceRegistration != null || !myRegistrationEnabledFlag){
            return;
        }
        Properties props = getRegistrationProperties();
        if(!uniquePropertiesAvailable()){
            getLogger().warn("Unable to register service.  "
                    + "One or more unique property is in use.");
            return;
        }
        ServiceRegistration reg = myContext.registerService(
                myRegistrationClassNames, myService, props);
        myServiceRegistration = reg;
        getLogger().warn(
                "Service Successfully Registered: {}", myService.toString());
        Object changeKey = new Object();
        try{
            firePropertyChange(PROP_SERVICE_CHANGED, changeKey, this);
        }catch(RuntimeException ex){
            getLogger().warn(
                    "Runtime exception in event handling for "
                    + "service registration of " + myService.toString(), ex);
        }
    }
    
    private boolean uniquePropertiesAvailable(){
        if(myUniqueProperties == null){
            return true;
        }
        for(Entry e : myUniqueProperties.entrySet()){
            String key = e.getKey().toString();
            String val = e.getValue().toString();
            String filter = OSGiUtils.createFilter(key, val);
            if(OSGiUtils.serviceExists(
                    myContext, myRegistrationClassNames, filter)){
				getLogger().warn(
                        "Unique service property already in use: ({}={}).", 
                        key, val);
                return false;
            }
        }
        return true;
    }
    
    private void handleAllDependencies(Map<String,Object> requiredServices){
        if(!ServiceLifecycleProvider.Validator.validateServices(
                myLifecycleProvider.getDependencyDescriptors(), 
                requiredServices)){
            throw new IllegalArgumentException(
                    "Invalid dependency set for service.");
        }
        myLifecycleProvider.start(requiredServices);
        myService = myLifecycleProvider.getService();
        if(myService == null){
            getLogger().warn("The lifecycle failed to create a service.");
        }else{
            getLogger().info(
                    "Service created of type(s): {}", 
                    Arrays.toString(
                            myLifecycleProvider.getServiceClassNames()));
            register();
        }
        myLifecycleListener.flush();
    }
    
    private void handleChanged(String id, Object newDependency){
        if(id == null){
            throw new NullPointerException();
        }else if(!validate(id, newDependency)){
            throw new IllegalArgumentException("Invalid id or dependency.  "
                    + "id: " + id + ", dependency: " + newDependency);
        }
        myLifecycleProvider.dependencyChanged(id, newDependency, 
                myDependenciesTracker.getAvailableDependencies());
        checkForModification();
        myLifecycleListener.flush();
    }
    
    private boolean validate(String id, Object req){
        List<DependencyDescriptor> reqs = 
                myLifecycleProvider.getDependencyDescriptors();
        return (req == null && Validator.validateServiceId(reqs, id)) ||
                Validator.validateService(reqs, id, req);
    }
    
    private void checkForModification(){
        T service = myLifecycleProvider.getService();
        if(myService == null && service == null){
            return;
        }
        if(service == null && myService != null){
			getLogger().info("Required Service change stopped this service.  "
                    + "Service is being unregistered.");
            unregister();
            myService = null;
        }else if(service != null && myService == null){
            getLogger().info(
                    "Required Service change has started this service.");
            if(myServiceRegistration != null){
                unregister();
            }
            myService = service;
            register();
        }else if(service != myService){
            getLogger().info(
                    "Required Service change has changed this service.");
            ServiceRegistration oldReg = myServiceRegistration;
            myService = service;
            register();
            if(oldReg != null){
                oldReg.unregister();
            }
        }
    }

    @Override
    public List<DependencyDescriptor> getDependencies() {
        if(myLifecycleProvider == null){
            return null;
        }
        return myLifecycleProvider.getDependencyDescriptors();
    }

    @Override
    public boolean getDependencyStatus(String dependencyId) {
        if(myDependenciesTracker == null){
            return false;
        }
        return myDependenciesTracker.getDependency(dependencyId) != null;
    }

    @Override
    public Properties getRegistrationProperties() {
        Properties props = myLifecycleProvider.getRegistrationProperties();
        Properties allProps = new Properties();
        if(props != null){
            allProps.putAll(props);
        }
        if(myRegistrationProperties != null){
            allProps.putAll(myRegistrationProperties);
        }
        if(myUniqueProperties != null){
            allProps.putAll(myUniqueProperties);
        }
        return allProps;
    }

    @Override
    public boolean isAvailable() {
        return myService != null;
    }

    @Override
    public boolean isRegistered() {
        return isAvailable() && myServiceRegistration != null;
    }

    @Override
    public String[] getServiceClassNames() {
        return myLifecycleProvider.getServiceClassNames();
    }

    @Override
    public int getDependencyCount() {
        List deps = getDependencies();
        if(deps == null){
            return 0;
        }
        return deps.size();
    }

    @Override
    public int getAvailableDependencyCount() {
        if(myDependenciesTracker == null){
            return 0;
        }
        Map map = myDependenciesTracker.getAvailableDependencies();
        if(map == null){
            return 0;
        }
        return map.size();
    }
    
    class DependencyStatusListener implements PropertyChangeListener{
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if(evt == null){
                return;
            }
            String name = evt.getPropertyName();
            if(PROP_ALL_DEPENDENCIES_AVAILABLE.equals(name)){
                handleAllAvailable(evt);
            }else if(PROP_DEPENDENCY_CHANGED.equals(name)){
                dependencyChange(evt);
            }else if(PROP_DEPENDENCY_AVAILABLE.equals(name)){
                dependencyChange(evt);
            }else if(PROP_DEPENDENCY_UNAVAILABLE.equals(name)){
                dependencyChange(evt);
            }
        }
        
        private void dependencyChange(PropertyChangeEvent evt){
            String id = evt.getOldValue().toString();
            if(myInitializedFlag){
                Object obj = evt.getNewValue();
                handleChanged(id, obj);
            }
            firePropertyChange(PROP_DEPENDENCY_CHANGED, null, id);
        }
        
        private void handleAllAvailable(PropertyChangeEvent evt){
            if(myInitializedFlag){
                return;
            }
            myInitializedFlag = true;
            getLogger().info("All requirements available, "
                    + "attempting to create service.");
            Object obj = evt.getNewValue();
            if(obj == null || !(obj instanceof Map)){
                getLogger().warn(
                        "Invalid requirement map, cannot create service.");
                myInitializedFlag = false;
                return;
            }try{
                Map<String,Object> reqs = (Map<String,Object>)obj;
                handleAllDependencies(reqs);
            }catch(ClassCastException ex){
                getLogger().warn("Improper requirement Map type.", ex);
                myInitializedFlag = false;
                return;
            }
            return;
        }
    }
	public Logger getLogger() {
		return theLogger;
	}
    /**
     * Listens for dependency descriptors being added or removed from the
     * lifecycle.  
     * Additions are queued until the lifecycle is finished updating, 
     * otherwise the lifecycle could be called before the update is complete.
     * Removal happens immediately and the lifecycle is not notified.
     * Descriptors may be removed from the queue.
     */
    class LifecycleDependencyListener implements PropertyChangeListener{
        private Queue<DependencyDescriptor> myAddQueue 
                = new LinkedList<DependencyDescriptor>();
        
        public LifecycleDependencyListener(){
            myAddQueue = new LinkedList<DependencyDescriptor>();
        }
        
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if(evt == null){
                return;
            }
            String eventName = evt.getPropertyName();
            Object obj = evt.getNewValue();
            if(obj == null || !(obj instanceof DependencyDescriptor)){
                return;
            }
            DependencyDescriptor desc = (DependencyDescriptor)obj;
            if(PROP_DEPENDENCY_ADDED.equals(eventName)){
                addDesc(desc);
            }else if(PROP_DEPENDENCY_REMOVED.equals(eventName)){
                remove(eventName);
            }
        }
        /**
         * Checks if the dependency name is in use
         * @param desc 
         */
        private synchronized void addDesc(DependencyDescriptor desc){
            for(DependencyDescriptor d : myAddQueue){
                if(desc.getDependencyName().equals(d.getDependencyName())){
                    return;
                }
            }
            myAddQueue.add(desc);
        }
        /**
         * If not found in the tracker, attempts to remove from queue
         * @param name 
         */
        private synchronized void remove(String name){
            if(myDependenciesTracker.removeDependencyTracker(name)){
                return;
            }
            DependencyDescriptor remove = null;
            for(DependencyDescriptor desc : myAddQueue){
                if(name.equals(desc.getDependencyName())){
                    remove = desc;
                    break;
                }
            }
            if(remove != null){
                myAddQueue.remove(remove);
            }
        }
        /**
         * Adds queued descriptors to the tracker, and removes them from the 
         * queue.
         */
        private synchronized void flush(){
            while(!myAddQueue.isEmpty()){
                DependencyDescriptor desc = myAddQueue.poll();
                if(desc != null){
                    myDependenciesTracker.addDependencyDescription(desc);
                }
            }
        }
        
    }
}
