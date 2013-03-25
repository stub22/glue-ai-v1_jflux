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
package org.jflux.api.services.dep;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jflux.api.service.DependencySpec;
import org.jflux.api.service.ServiceLifecycle;
import org.jflux.api.services.utils.DependencyValidator;

/**
 *
 * @author matt
 */
public class ServiceManager<T> {
    private final static Logger theLogger = 
            Logger.getLogger(ServiceManager.class.getName());
    private ServiceLifecycle<T> myLifecycle;
    private boolean myStaisfiedFlag;
    private T myService;
    
    /**
     * Creates a new AbstractLifecycleProvider with the given 
     * DependencyDescriptors.
     * @param deps descriptors of the dependencies for this lifecycle
     */
    public ServiceManager(ServiceLifecycle<T> lifecycle){
        if(lifecycle == null){
            throw new NullPointerException();
        }
        myLifecycle = lifecycle;
        myStaisfiedFlag = false;
    }

    public synchronized void createService(Map<String, Object> dependencies) {
        if(!DependencyValidator.validateServices(getDescriptors(), dependencies)){
            throw new IllegalArgumentException(
                    "Invalid Dependency set for factory.");
        }
        myService = myLifecycle.createService(dependencies);
        myStaisfiedFlag = true;
    }
    
    /**
     * Validates the arguments before calling <code>handleChange</code>.
     * If the service is null and the dependencies are satisfied, a service is
     * created and <code>handleChange</code> is not called.
     * @param dependencyName name of the dependency changed
     * @param dependency new dependency
     * @param dependencies all available dependencies
     */
    public synchronized void handleDependencyChange(
            String dependencyName, Object dependency, 
            Map<String,Object> dependencies) {
        if(dependencyName == null){
            throw new NullPointerException();
        }
        if(!validate(dependencyName, dependency)){
            return;
        }
        myStaisfiedFlag = DependencyValidator.validateServices(
                getDescriptors(), dependencies);
        if(myService == null && isSatisfied()){
            myService = myLifecycle.createService(dependencies);
            return;
        }
        myService = myLifecycle.handleDependencyChange(
                myService, "", dependencyName, dependency, dependencies);
    }
    
    public boolean isSatisfied(){
        return myStaisfiedFlag;
    }
    
    public T getService(){
        return isSatisfied() ? myService : null;
    }
    
    public ServiceLifecycle<T> getLifecycle(){
        return myLifecycle;
    }
    
    private boolean validate(String id, Object req){
        List<DependencySpec> reqs = getDescriptors();
        if(req == null){
            if(DependencyValidator.validateServiceId(reqs, id)){
                return true;
            }
            theLogger.log(Level.WARNING, "Invalid dependency id: {0}.", id);
            return false;
        }
        if(DependencyValidator.validateService(reqs, id, req)){
            return true;
        }
        theLogger.log(Level.WARNING, "Invalid dependency. "
                + "id: {0}, instance: {1}", new Object[]{id, req});
        return false;
    }

    private List<DependencySpec> getDescriptors() {
        return myLifecycle.getDependencyDescriptors();
    }
}
