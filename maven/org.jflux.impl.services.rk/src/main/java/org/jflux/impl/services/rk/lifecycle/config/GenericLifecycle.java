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
package org.jflux.impl.services.rk.lifecycle.config;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.jflux.api.core.Adapter;
import org.jflux.api.core.Listener;
import org.jflux.impl.services.rk.lifecycle.AbstractLifecycleProvider;
import org.jflux.impl.services.rk.lifecycle.DependencyDescriptor;

/**
 *
 * @author Matthew Stevenson
 */
@Deprecated
public class GenericLifecycle<T> extends AbstractLifecycleProvider<T,T> {
    private Adapter<Map<String,Object>, T> myFactory;
    private Adapter<String, Listener<DependencyChange>> myChangeListeners;
    private Listener<T> myStopListener;

    public GenericLifecycle(
            String[] serviceClassNames,
            List<DependencyDescriptor> deps,
            Properties props,
            Adapter<Map<String,Object>, T> serviceFactory,
            Adapter<String,Listener<DependencyChange>> changeListeners,
            Listener<T> stopListener) {
        super(deps);
        if(serviceClassNames == null ||
                serviceFactory == null || changeListeners == null){
            throw new NullPointerException();
        }
        if(myRegistrationProperties == null){
            myRegistrationProperties = props;
        }else if(props != null){
            myRegistrationProperties.putAll(props);
        }
        myFactory = serviceFactory;
        myChangeListeners = changeListeners;
        myServiceClassNames = serviceClassNames;
        myStopListener = stopListener;
    }

    @Override
    protected T create(Map<String, Object> dependencies) {
        return myFactory.adapt(dependencies);
    }

    @Override
    protected void handleChange(
            String name, Object dependency,
            Map<String, Object> availableDependencies) {
        if(myChangeListeners == null){
            return;
        }
        Listener<DependencyChange> listener = myChangeListeners.adapt(name);
        if(listener == null){
            return;
        }
        DependencyChange change =
                new DependencyChange(this, dependency, availableDependencies);
        listener.handleEvent(change);
    }

    @Override
    public void stop(){
        if(myStopListener != null && myService != null){
            myStopListener.handleEvent(myService);
        }
        myService = null;
    }

    @Override
    public Class<T> getServiceClass() {
        return null;
    }

    public static class DependencyChange<T,D>{
        private GenericLifecycle<T> myLifecycle;
        private D myDependency;
        private Map<String,Object> myAvailableDependencies;

        protected DependencyChange(
                GenericLifecycle<T> lifecycle,
                D dependency, Map<String, Object> availableDependencies) {
            myDependency = dependency;
            myAvailableDependencies = availableDependencies;
            myLifecycle = lifecycle;
        }

        public Map<String, Object> getAvailableDependencies() {
            return myAvailableDependencies;
        }

        public D getDependency() {
            return myDependency;
        }

        public GenericLifecycle<T> getLifecycle() {
            return myLifecycle;
        }

        public T getService() {
            if(myLifecycle == null){
                return null;
            }
            return myLifecycle.myService;
        }
    }
}
