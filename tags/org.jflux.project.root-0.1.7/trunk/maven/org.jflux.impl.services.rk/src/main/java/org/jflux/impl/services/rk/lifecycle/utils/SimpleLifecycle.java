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
package org.jflux.impl.services.rk.lifecycle.utils;

import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.jflux.impl.services.rk.lifecycle.DependencyDescriptor;
import org.jflux.impl.services.rk.lifecycle.ServiceLifecycleProvider;

/**
 * Defines an empty lifecycle for managing a service with no dependencies.
 * @author Matthew Stevenson <www.robokind.org>
 */
public class SimpleLifecycle<T> implements ServiceLifecycleProvider<T> {
    private T myService;
    private String[] myServiceClassNames;
    private Properties myRegistrationProperties;
    /**
     * Creates a SimpleLifecycle for the given service.
     * @param service service for the lifecycle to handle
     * @param clazz class the service should be available as
     */
    public SimpleLifecycle(T service, Class<T> clazz){
        this(service, clazz, null);
    }
    /**
     * Creates a SimpleLifecycle for the given service.
     * @param service service for the lifecycle to handle
     * @param className class the service should be available as
     */
    public SimpleLifecycle(T service, String className){
        this(service, className, null);
    }
    /**
     * Creates a SimpleLifecycle for the given service.
     * @param service service for the lifecycle to handle
     * @param clazz class the service should be available as
     * @param props Properties to use when registering the service
     */
    public SimpleLifecycle(T service, Class<T> clazz, Properties props){
        this(service, new String[]{clazz.getName()}, props);
    }
    
    public SimpleLifecycle(T service, Class<T> clazz, 
            String idKey, String idStr, Properties props){
        this(service, new String[]{clazz.getName()}, idKey, idStr, props);
    }
    /**
     * Creates a SimpleLifecycle for the given service.
     * @param service service for the lifecycle to handle
     * @param className class the service should be available as
     * @param props Properties to use when registering the service
     */
    public SimpleLifecycle(T service, String className, Properties props){
        this(service, new String[]{className}, props);
    }
    
    public SimpleLifecycle(
            T service, String[] serviceClassNames, 
            String idKey, String idStr, Properties props){
        this(service, serviceClassNames, getProps(idKey, idStr, props));
    }
    
    private static Properties getProps(String k, String v, Properties p){
        if(k == null || v == null){
            throw  new NullPointerException();
        }
        if(p == null){
            p = new Properties();
        }
        p.put(k, v);
        return p;
    }
    
    public SimpleLifecycle(
            T service, String[] serviceClassNames, Properties props){
        if(service == null || serviceClassNames == null){
            throw new NullPointerException();
        }else if(serviceClassNames.length == 0){
            throw new IllegalArgumentException("Found empty class name array, "
                    + "must specify at least one registration class.");
        }
        myService = service;
        myServiceClassNames = serviceClassNames;
        myRegistrationProperties = props;
    }
    
    @Override
    public void start(Map<String, Object> dependencyMap) {}
    
    @Override
    public void stop() {}

    @Override
    public void dependencyChanged(
            String dependencyId, Object dependency, 
            Map<String,Object> dependencies) {}

    @Override
    public T getService() {
        return myService;
    }

    @Override
    public List<DependencyDescriptor> getDependencyDescriptors() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Properties getRegistrationProperties() {
        return myRegistrationProperties;
    }
    
    @Override
    public String[] getServiceClassNames() {
        return myServiceClassNames;
    }
    
    /**
     * A SimpleLifecycle has no dependencies and does not fire events.
     * Listeners are unused.
     * @param listener 
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener){}
    /**
     * A SimpleLifecycle has no dependencies and does not fire events.
     * Listeners are unused.
     * @param listener 
     */
    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener){}
    /**
     * A SimpleLifecycle has no dependencies and does not fire events.
     * Listeners are unused.
     * @param propertyName
     * @param listener 
     */
    @Override
    public void addPropertyChangeListener(
            String propertyName, PropertyChangeListener listener){}
    /**
     * A SimpleLifecycle has no dependencies and does not fire events.
     * Listeners are unused.
     * @param propertyName
     * @param listener 
     */
    @Override
    public void removePropertyChangeListener(
            String propertyName, PropertyChangeListener listener) {}
    
}
