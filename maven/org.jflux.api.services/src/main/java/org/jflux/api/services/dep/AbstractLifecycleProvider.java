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
package org.jflux.api.services.dep;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jflux.api.services.extras.PropertyChangeNotifier;

/**
 * Abstract implementation of ServiceLifecycleProvider.  Automatically validates
 * dependencies against the descriptors provided at creation.
 * 
 * @param <I> Interface or Base Class of the service managed by this lifecycle
 * @param <T> actual type of the service managed
 * @author Matthew Stevenson <www.robokind.org>
 */
public abstract class AbstractLifecycleProvider<I,T extends I> extends
        PropertyChangeNotifier implements ServiceLifecycleProvider<I> {
    private final static Logger theLogger = 
            Logger.getLogger(AbstractLifecycleProvider.class.getName());

    private List<DependencyDescriptor> myDescriptors;
    private List<DependencyDescriptor> myRuntimeDescriptors;
    private boolean myStaisfiedFlag;
    /**
     * Instance of the service being managed
     */
    protected T myService;
    /**
     * OSGi registration properties
     */
    protected Properties myRegistrationProperties;
    /**
     * Names of the classes provided by this service.
     */
    protected String[] myServiceClassNames;
    
    /**
     * Creates a new AbstractLifecycleProvider with the given 
     * DependencyDescriptors.
     * @param deps descriptors of the dependencies for this lifecycle
     */
    public AbstractLifecycleProvider(List<DependencyDescriptor> deps){
        if(deps == null){
            throw new NullPointerException();
        }
        myStaisfiedFlag = false;
        myDescriptors = deps;
    }

    @Override
    public synchronized void start(Map<String, Object> dependencies) {
        if(!Validator.validateServices(myDescriptors, dependencies)){
            throw new IllegalArgumentException(
                    "Invalid Dependency set for factory.");
        }
        myService = create(dependencies);
        myStaisfiedFlag = true;
    }
    
    @Override
    public synchronized void stop(){
        
    }
    /**
     * Called when all dependencies are available.  This should create and 
     * return the service for this lifecycle.
     * @param dependencies validated map of the lifecycle's dependencies
     * @return the service managed by this lifecycle, null if the service
     * cannot be created
     */
    protected abstract T create(Map<String,Object> dependencies);

    @Override
    public synchronized T getService() {
        if(!isSatisfied()){
            return null;
        }
        return myService;
    }
    
    @Override
    public Properties getRegistrationProperties(){
        return myRegistrationProperties;
    }

    @Override
    public List<DependencyDescriptor> getDependencyDescriptors() {
        return myDescriptors;
    }
    
    public boolean isSatisfied(){
        return myStaisfiedFlag;
    }
    /**
     * Validates the arguments before calling <code>handleChange</code>.
     * If the service is null and the dependencies are satisfied, a service is
     * created and <code>handleChange</code> is not called.
     * @param dependencyName name of the dependency changed
     * @param dependency new dependency
     * @param dependencies all available dependencies
     */
    @Override
    public synchronized void dependencyChanged(
            String dependencyName, Object dependency, 
            Map<String,Object> dependencies) {
        if(dependencyName == null){
            throw new NullPointerException();
        }
        if(!validate(dependencyName, dependency)){
            return;
        }
        myStaisfiedFlag = 
                Validator.validateServices(myDescriptors, dependencies);
        if(myService == null && isSatisfied()){
            myService = create(dependencies);
            return;
        }
        handleChange(dependencyName, dependency, dependencies);
    }
    /**
     * Called from <code>dependencyChanged</code> with validated values.
     * @param name name of the dependency changed 
     * @param dependency new dependency value, null if it was removed
     * @param availableDependencies a map of all available dependencies and
     * their names
     */
    protected abstract void handleChange(String name, Object dependency, 
            Map<String,Object> availableDependencies);
    
    private boolean validate(String id, Object req){
        List<DependencyDescriptor> reqs = getDependencyDescriptors();
        if(req != null){
            if(Validator.validateService(reqs, id, req)){
                return true;
            }
            theLogger.log(Level.WARNING,
                    "Invalid service or id.  id: {0}, service: {1}", 
                    new Object[]{id, req});
            return false;
        }
        if(Validator.validateServiceId(reqs, id)){
            return true;
        }
        theLogger.log(Level.WARNING, "Invalid service id: {0}.", id);
        return false;
    }
    
    /**
     * Returns the Class of the service managed by this lifecycle provider.
     * @return Class of the service managed by this lifecycle provider
     */
    protected abstract Class<I> getServiceClass();
    
    @Override
    public String[] getServiceClassNames(){
        if(myServiceClassNames == null){
            myServiceClassNames = new String[]{getServiceClass().getName()};
        }
        return myServiceClassNames;
    }
    
    /**
     * Adds a dependency after the lifecycle is initialized.
     * 
     * @param desc dependency to add
     * @return true if successful, false if the dependency name is in use
     */
    protected boolean addRuntimeDependency(DependencyDescriptor desc){
        if(desc == null){
            throw new NullPointerException();
        }
        for(DependencyDescriptor d : getDependencyDescriptors()){
            if(desc.getDescriptorName().equals(d.getDescriptorName())){
                theLogger.log(Level.WARNING,
                        "Unable to add depenedency, name already in use: {0}",
                        desc.getDescriptorName());
                return false;
            }
        }
        /*if(DependencyType.REQUIRED == desc.getDependencyType()){
            DependencyDescriptor optDesc = new DependencyDescriptor(
                    desc.getDependencyName(), 
                    desc.getServiceClass(), 
                    desc.getServiceFilter(), 
                    DependencyType.OPTIONAL);
            desc = optDesc;
        }*/
        if(myRuntimeDescriptors == null){
            myRuntimeDescriptors = new ArrayList<DependencyDescriptor>();
        }
        theLogger.log(Level.INFO, "Adding optional runtime depenedency {0}", 
                desc.getDescriptorName());
        myDescriptors.add(desc);
        myRuntimeDescriptors.add(desc);
        firePropertyChange(PROP_DEPENDENCY_ADDED, null, desc);
        return true;
    }
    
    /**
     * Removes a dependency after the lifecycle is initialized.
     * If the dependency was not added after initialization, it will not be
     * removed.
     * @param desc dependency to remove
     * @return true if successful, false if the dependency name is not found, or
     * does not belong to a runtime dependency.
     */
    protected boolean removeRuntimeDependency(String depName){
        if(depName == null){
            throw new NullPointerException();
        }
        if(myRuntimeDescriptors == null){
            return false;
        }
        DependencyDescriptor desc = getRuntimeDescriptor(depName);
        if(desc == null){
            theLogger.log(Level.WARNING, 
                    "Could not find optional runtime depenedency {0}", depName);
            return false;
        }
        theLogger.log(Level.INFO, 
                "Removing optional runtime depenedency {0}", depName);
        myRuntimeDescriptors.remove(desc);
        myDescriptors.remove(desc);
        firePropertyChange(PROP_DEPENDENCY_REMOVED, null, desc);
        return true;
    }
    
    private DependencyDescriptor getRuntimeDescriptor(String name){
        for(DependencyDescriptor d : myRuntimeDescriptors){
            if(name.equals(d.getDescriptorName())){
                return d;
            }
        }
        return null;
    }
}
