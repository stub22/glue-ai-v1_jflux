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

import org.jflux.api.core.Source;
import org.jflux.api.registry.Descriptor;
import org.jflux.api.registry.Reference;
import org.jflux.api.registry.Registry;
import org.jflux.api.service.binding.BindingSpec.BindingStrategy;
import org.jflux.api.services.extras.PropertyChangeNotifier;

/**
 *
 * @author matt
 */
public abstract class DependencyTracker<T> extends PropertyChangeNotifier {    
    protected Source<Boolean> myCreatedFlag;
    protected BindingStrategy myBindingStrategy;
    protected DepRefTracker<T> myTracker;
    private String myDependencyName;

    public DependencyTracker(
            String dependencyName, BindingStrategy binding, Source<Boolean> serviceCreated){
        if(binding == null || serviceCreated == null){
            throw new NullPointerException();
        }
        myBindingStrategy = binding;
        myCreatedFlag = serviceCreated;
        myTracker = new DepRefTracker();
        myDependencyName = dependencyName;
    }
    
    public String getDependencyName(){
        return myDependencyName;
    }
    
    public void start(Registry registry, Descriptor desc){
        myTracker.start(registry, desc);
    }
    
    public void stop(){
        myTracker.stop();
    }

    protected void dependencyAdded(Reference ref){
        if(myBindingStrategy == BindingStrategy.EAGER){
            eagerAdd(ref);
        }else{
            lazyAdd(ref);
        }
    }
    
    public abstract Object getTrackedDependency();
    
    protected abstract void eagerAdd(Reference ref);
    
    protected abstract void lazyAdd(Reference ref);
    
    protected abstract void dependencyRemoved(Reference ref);
    
    protected class DepRefTracker<T> extends ReferenceTracker<T>{
        @Override protected synchronized void addReference(Reference ref) {
            super.addReference(ref);
            dependencyAdded(ref);
        }

        @Override protected synchronized void removeReference(Reference ref) {
            dependencyRemoved(ref);
            super.removeReference(ref);
        }
    }
}
