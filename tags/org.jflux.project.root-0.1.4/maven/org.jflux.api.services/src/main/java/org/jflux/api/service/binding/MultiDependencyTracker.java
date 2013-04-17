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
import java.util.List;
import org.jflux.api.core.Source;
import org.jflux.api.registry.Reference;
import org.jflux.api.service.binding.BindingSpec.BindingStrategy;
import static org.jflux.api.service.ServiceLifecycle.*;

/**
 *
 * @author matt
 */
public class MultiDependencyTracker<T> extends DependencyTracker<T> {
    
    public MultiDependencyTracker(
            String dependencyName, BindingStrategy binding, 
            Source<Boolean> serviceCreated){
        super(dependencyName, binding, serviceCreated);
    }
    
    @Override
    protected synchronized void eagerAdd(Reference ref){
        T t = myTracker.getService(ref);
        if(t == null){
            return;
        }
        firePropertyChange(PROP_DEPENDENCY_AVAILABLE, null, t);
    }
    
    @Override
    protected synchronized void lazyAdd(Reference ref){
        if(myCreatedFlag.getValue()){
            return;
        }
        T t = myTracker.getService(ref);
        if(t == null){
            return;
        }
        firePropertyChange(PROP_DEPENDENCY_AVAILABLE, null, t);
    }
    
    @Override
    protected synchronized void dependencyRemoved(Reference ref){
        T service = myTracker.getTrackedService(ref);
        if(service == null){
            return;
        }
        firePropertyChange(PROP_DEPENDENCY_UNAVAILABLE, service, null);
    }

    @Override
    public synchronized Object getTrackedDependency() {
        return findDependency(myTracker.getTrackedReferences());
    }
    
    private List<T> findDependency(List<Reference> refs) {
        if(refs.isEmpty()){
            return null;
        }
        if(myBindingStrategy == BindingStrategy.EAGER ){
            refs = new ArrayList<Reference>(refs);
            Collections.reverse(refs);
        }
        List<T> l = new ArrayList<T>();
        for(Reference r : refs){
            T t = myTracker.getService(r);
            if(t != null){
                l.add(t);
            }
        }
        return l.isEmpty() ? null : l;
    }
}