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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.jflux.api.core.Source;
import org.jflux.api.registry.Descriptor;
import org.jflux.api.registry.Registry;
import org.jflux.api.registry.basic.BasicDescriptor;
import org.jflux.api.service.DependencySpec.Cardinality;
import org.jflux.api.service.binding.BindingSpec;
import org.jflux.api.service.binding.BindingSpec.BindingStrategy;
import org.jflux.api.service.binding.MultiDependencyTracker;
import org.jflux.api.service.binding.SingleDepencyTracker;

/**
 *
 * @author matt
 */
public class Manager<T> {
    private ServiceLifecycle<T> myLifecycle;
    private Map<String,BindingSpec> myBindings;
    private Map<String,Object> myCachedDependencies;
    private T myService;
    private Map<BindingSpec,DependencyTracker> myTrackerMap;
    private RegistrationStrategy myRegistrationStrategy;
    private Source<Boolean> myServiceCreatedSource;
    private DependencyChangeListener myChangeListener;
    private boolean myStartFlag;
    private boolean myListenFlag;
    
    public Manager(ServiceLifecycle<T> lifecycle, 
            Map<String,BindingSpec> bindings, 
            RegistrationStrategy<T> registration){
        if(lifecycle == null){
            throw new NullPointerException();
        }
        myLifecycle = lifecycle;
        myBindings = bindings;
        if(myBindings == null){
            myBindings = new HashMap<String, BindingSpec>();
        }
        myRegistrationStrategy = registration;
        if(myRegistrationStrategy == null){
            myRegistrationStrategy = new DefaultRegistrationStrategy(
                    myLifecycle.getServiceClassNames(), null);
        }
        myStartFlag = false;
        myListenFlag = false;
        myTrackerMap = new HashMap<BindingSpec, DependencyTracker>();
        myChangeListener = new DependencyChangeListener();
        myServiceCreatedSource = new Source<Boolean>() {
            @Override  public Boolean getValue() {
                return isSatisfied() && myService != null;
            }
        };
    }
    public Manager(ServiceLifecycle<T> lifecycle, 
            Map<String,BindingSpec> bindings, 
            Map<String,String> registrationProperties){
        this(lifecycle, bindings, 
                new DefaultRegistrationStrategy<T>(
                        lifecycle.getServiceClassNames(),
                        registrationProperties));
    }
    
    public synchronized void start(Registry registry){
        if(myStartFlag){
            return;
        }
        List<DependencySpec> deps = myLifecycle.getDependencyDescriptors();
        for(DependencySpec s : deps){
            if(myBindings.containsKey(s.getDependencyName())){
                continue;
            }
            Descriptor desc = new BasicDescriptor(
                    s.getDependencyName(), s.getDependencyClassName(), null);
            BindingSpec bind = new BindingSpec(s, desc, BindingStrategy.LAZY);
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
        myRegistrationStrategy.unregister();
        unbindDependencies();
        myStartFlag = false;
    }
    
    private void bindDependencies(Registry registry){
        if(myStartFlag){
            return;
        }
        for(BindingSpec s : myBindings.values()){
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
        for(Entry<BindingSpec,DependencyTracker> e : myTrackerMap.entrySet()){
            BindingSpec s = e.getKey();
            DependencyTracker t = e.getValue();
            t.stop();
            t.removePropertyChangeListener(myChangeListener);
            myTrackerMap.remove(s);
        }
    }
    
    private void tryCreate(){
        if(!isSatisfied()){
            return;
        }
        updateDependencyCache();
        T t = myLifecycle.createService(myCachedDependencies);
        if(t == null){
            return;
        }
        myService = t;
        myRegistrationStrategy.register(myService);
    }
    
    private void updateDependency(String changeType, String dependencyName, Object dependency){
        if(!isSatisfied()){
            
        }
        BindingSpec spec = myBindings.get(dependencyName);
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
        myRegistrationStrategy.updateRegistration(service);
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
           myRegistrationStrategy.updateRegistration(myService);
        }else{
            myRegistrationStrategy.updateRegistration(service);
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
        for(Entry<BindingSpec,DependencyTracker> e : myTrackerMap.entrySet()){
            BindingSpec spec = e.getKey();
            DependencyTracker tracker = e.getValue();
            Cardinality c = spec.getDependencySpec().getCardinality();
            if(c.isRequired() && tracker.getTrackedDependency() == null){
                return false;
            }
        }
        return true;
    }
    
    public boolean isAvailable(){
        return myRegistrationStrategy.isRegistered();
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
}
