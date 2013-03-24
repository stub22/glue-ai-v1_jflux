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
import java.util.Map;
import java.util.Map.Entry;
import org.jflux.api.core.Source;
import org.jflux.api.registry.Registry;
import org.jflux.api.registry.Reference;
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
    private Map<String,Object> myCachedDependencies;
    private ServiceLifecycle<T> myLifecycle;
    private T myService;
    private Map<String,BindingSpec> myBindings;
    private Map<BindingSpec,DependencyTracker> myTrackerMap;
    private RegistrationStrategy myRegistrationStrategy;
    private Source<Boolean> myServiceCreatedSource;
    private DependencyChangeListener myChangeListener;
    
    public Manager(){
        myChangeListener = new DependencyChangeListener();
        myServiceCreatedSource = new Source<Boolean>() {
            @Override  public Boolean getValue() {
                return isSatisfied() && myService != null;
            }
        };
    }
    
    private void bindDependencies(Registry registry){
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
    }
    
    private void updateDependency(int changeType, String dependencyName, Object dependency){
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
        myRegistrationStrategy.register(service, myBindings);
        myRegistrationStrategy.unregister(myService);
        myLifecycle.disposeService(myService, dependencies);
        myService = service;
    }
    
    private void dynamicUpdate(int changeType, String dependencyName, Object dependency){
        Map<String,Object> dependencies = myCachedDependencies;
        updateDependencyCache();
        T service = myLifecycle.handleDependencyChange(
                myService, changeType, dependencyName, 
                dependency, myCachedDependencies);
        if(service == myService){
           myRegistrationStrategy.updateRegistration(myService, myBindings);
        }else{
            myRegistrationStrategy.register(service, myBindings);
            myRegistrationStrategy.unregister(myService);
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
    
    
    class DependencyChangeListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String depName = (String)evt.getSource();
            T oldVal = evt.getOldValue() == null ? null : (T)evt.getOldValue();
            T newVal = evt.getNewValue() == null ? null : (T)evt.getNewValue();
            if(oldVal == null){
                //added
            }else if(newVal == null){
                    //item removed
            }else{
                //change
            }
        }
    }
}
