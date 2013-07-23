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
package org.jflux.api.service;

import org.jflux.api.service.binding.DependencyTracker;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.jflux.api.core.Source;
import org.jflux.api.registry.Descriptor;
import org.jflux.api.registry.Registry;
import org.jflux.api.registry.basic.BasicDescriptor;
import org.jflux.api.service.ServiceDependency.Cardinality;
import org.jflux.api.service.binding.ServiceBinding;
import org.jflux.api.service.binding.ServiceBinding.BindingStrategy;
import org.jflux.api.service.binding.MultiDependencyTracker;
import org.jflux.api.service.binding.SingleDepencyTracker;

/**
 *
 * @author matt
 */
public class ServiceManager<T> implements Manager {
    private ServiceLifecycle<T> myLifecycle;
    private Map<String,ServiceBinding> myBindings;
    private Map<String,Object> myCachedDependencies;
    private T myService;
    private Map<ServiceBinding,DependencyTracker> myTrackerMap;
    private RegistrationStrategy myServiceRegistrationStrategy;
    private Source<Boolean> myServiceCreatedSource;
    private DependencyChangeListener myChangeListener;
    private boolean myStartFlag;
    private boolean myListenFlag;
    private RegistrationStrategy<ServiceManager> myManagerRegistrationStrat;
    
    public ServiceManager(ServiceLifecycle<T> lifecycle, 
            Map<String,ServiceBinding> bindings, 
            RegistrationStrategy<T> registration,
            RegistrationStrategy<ServiceManager> managerRegistrationStrat){
        if(lifecycle == null){
            throw new NullPointerException();
        }
        myLifecycle = lifecycle;
        myBindings = bindings;
        if(myBindings == null){
            myBindings = new HashMap<String, ServiceBinding>();
        }
        myServiceRegistrationStrategy = registration;
        if(myServiceRegistrationStrategy == null){
            myServiceRegistrationStrategy = new DefaultRegistrationStrategy(
                    myLifecycle.getServiceClassNames(), null);
        }
        myStartFlag = false;
        myListenFlag = false;
        myTrackerMap = new HashMap<ServiceBinding, DependencyTracker>();
        myChangeListener = new DependencyChangeListener();
        myServiceCreatedSource = new Source<Boolean>() {
            @Override  public Boolean getValue() {
                return isSatisfied() && myService != null;
            }
        };
        if(managerRegistrationStrat != null) {
            myManagerRegistrationStrat = managerRegistrationStrat;
        } else {
            String[] classNames = {ServiceManager.class.getName()};
            myManagerRegistrationStrat =
                    new DefaultRegistrationStrategy<ServiceManager>(
                    classNames, null);
        }
    }
    public ServiceManager(ServiceLifecycle<T> lifecycle, 
            Map<String,ServiceBinding> bindings, 
            Map<String,String> registrationProperties,
            RegistrationStrategy<ServiceManager> managerRegistrationStrat){
        this(lifecycle, bindings, 
                new DefaultRegistrationStrategy<T>(
                        lifecycle.getServiceClassNames(),
                        registrationProperties), managerRegistrationStrat);
    }
    
    /**
     * 
     * @param registry 
     */
    public synchronized void start(Registry registry){
        
        // Do nothing if already started.
        if(myStartFlag){
            return;
        }
        if(myServiceRegistrationStrategy instanceof DefaultRegistrationStrategy){
            ((DefaultRegistrationStrategy)myServiceRegistrationStrategy).setRegistry(registry);
        }
        
        if(!myManagerRegistrationStrat.isRegistered()){
            myManagerRegistrationStrat.register(this);
        }
        
        List<ServiceDependency> deps = myLifecycle.getDependencySpecs();
        
        // Ensures each dependency has a binding
        for(ServiceDependency s : deps){
            if(myBindings.containsKey(s.getDependencyName())){
                continue;
            }
            Descriptor desc = 
                    new BasicDescriptor(s.getDependencyClassName(), null);
            ServiceBinding bind = new ServiceBinding(s, desc, BindingStrategy.LAZY);
            myBindings.put(s.getDependencyName(), bind);
        }
        bindDependencies(registry);
        myStartFlag = true;
    }
    
    public synchronized void stop(){
        if(!myStartFlag){
            return;
        }
        myListenFlag = false;
        myServiceRegistrationStrategy.unregister();
        unbindDependencies();
        myStartFlag = false;
    }
    
    private void bindDependencies(Registry registry){
        if(myStartFlag){
            return;
        }
        for(ServiceBinding s : myBindings.values()){
            String name = s.getDependencyName();
            Cardinality c = s.getDependencySpec().getCardinality();
            BindingStrategy strat = s.getBindingStrategy();
            DependencyTracker t = c.isMultiple()
                    ? new MultiDependencyTracker(name, strat, myServiceCreatedSource)
                    : new SingleDepencyTracker(name, strat, myServiceCreatedSource);
            t.addPropertyChangeListener(myChangeListener);
            myTrackerMap.put(s, t);
            t.start(registry, s.getDescriptor());
        }
        myListenFlag = true;
        tryCreate();
    }
    
    private void unbindDependencies(){
        if(!myStartFlag){
            return;
        }
        myLifecycle.disposeService(myService, myCachedDependencies);
        for(Entry<ServiceBinding,DependencyTracker> e : myTrackerMap.entrySet()){
            ServiceBinding s = e.getKey();
            DependencyTracker t = e.getValue();
            t.stop();
            t.removePropertyChangeListener(myChangeListener);
            myTrackerMap.remove(s);
        }
    }
    
    private void tryCreate(){
        if(myService != null || !isSatisfied()){
            return;
        }
        updateDependencyCache();
        T t = myLifecycle.createService(myCachedDependencies);
        if(t == null){
            return;
        }
        myService = t;
        myServiceRegistrationStrategy.register(myService);
    }
    
    private void updateDependency(String changeType, String dependencyName, Object dependency){
        if(!isSatisfied()){
            
        }
        ServiceBinding spec = myBindings.get(dependencyName);
        switch(spec.getUpdateStrategy()){
            case STATIC : staticUpdate(); break;
            case DYNAMIC : dynamicUpdate(changeType, dependencyName, dependency); break;
        }
    }
    
    private void staticUpdate(){
        Map<String,Object> dependencies = myCachedDependencies;
        updateDependencyCache();
        if(dependencies.equals(myCachedDependencies)){
            return;
        }
        T service = myLifecycle.createService(myCachedDependencies);
        myServiceRegistrationStrategy.updateRegistration(service);
        myLifecycle.disposeService(myService, dependencies);
        myService = service;
    }
    
    private void dynamicUpdate(String changeType, String dependencyName, Object dependency){
        Map<String,Object> dependencies = myCachedDependencies;
        updateDependencyCache();
        T service = myLifecycle.handleDependencyChange(
                myService, changeType, dependencyName, 
                dependency, myCachedDependencies);
        if(service == myService){
           myServiceRegistrationStrategy.updateRegistration(myService);
        }else{
            myServiceRegistrationStrategy.updateRegistration(service);
            myLifecycle.disposeService(myService, dependencies);
            myService = service;
        }
    }
    
    private void updateDependencyCache(){
        Map<String, Object> dependencies = new HashMap<String, Object>(myBindings.size());
        for(DependencyTracker tracker : myTrackerMap.values()){
            Object dep = tracker.getTrackedDependency();
            if(dep == null){
                continue;
            }
            String name = tracker.getDependencyName();
            dependencies.put(name, dep);
        }
        myCachedDependencies = dependencies;
    }
    
    public boolean isSatisfied(){
        for(Entry<ServiceBinding,DependencyTracker> e : myTrackerMap.entrySet()){
            ServiceBinding spec = e.getKey();
            DependencyTracker tracker = e.getValue();
            Cardinality c = spec.getDependencySpec().getCardinality();
            if(c.isRequired() && tracker.getTrackedDependency() == null){
                return false;
            }
        }
        return true;
    }
    
    public boolean isAvailable(){
        return myServiceRegistrationStrategy.isRegistered();
    }
    
    class DependencyChangeListener implements PropertyChangeListener {
        @Override public void propertyChange(PropertyChangeEvent evt) {
            if(!myListenFlag){
                return;
            }
            if(myService == null){
                tryCreate();
                return;
            }
            String depName = (String)evt.getSource();
            updateDependency(evt.getPropertyName(), depName, evt.getNewValue());
        }
    }
    
    public void dispose() {
        stop();
        
        if(myManagerRegistrationStrat.isRegistered()) {
            myManagerRegistrationStrat.unregister();
        }
    }
    
    public Map<ServiceBinding,DependencyTracker> getDependencies(){
        return myTrackerMap;
    }
    
    public RegistrationStrategy<T> getRegistrationStrategy(){
        return myServiceRegistrationStrategy;
    }
    
    public ServiceLifecycle<T> getLifecycle(){
        return myLifecycle;
    }
}
