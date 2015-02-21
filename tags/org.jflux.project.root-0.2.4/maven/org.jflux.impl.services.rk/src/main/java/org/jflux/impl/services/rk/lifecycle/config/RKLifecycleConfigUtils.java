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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.jflux.api.core.Adapter;
import org.jflux.api.core.Listener;
import org.jflux.api.core.config.Configuration;
import org.jflux.api.core.config.DefaultConfiguration;
import org.jflux.api.core.util.IndexedValue;
import org.jflux.api.core.util.MapAdapter;
import org.jflux.impl.services.rk.lifecycle.DependencyDescriptor;
import org.jflux.impl.services.rk.lifecycle.ServiceLifecycleProvider;
import org.jflux.impl.services.rk.lifecycle.config.GenericLifecycle.DependencyChange;
import org.jflux.impl.services.rk.lifecycle.utils.SimpleLifecycle;

/**
 *
 * @author Matthew Stevenson
 */
public class RKLifecycleConfigUtils {
    public final static String CONF_SERVICE_CLASSES = "lifecycleServiceClasses";
    public final static String CONF_SERVICE_PROPERTIES = "lifecycleRegistrationProperties";
    
    public final static String CONF_DEPENDENCY_CONFIGS = "genericLifecycleDependencyConfigs";
    public final static String CONF_SERVICE_FACTORY = "genericLifecycleServiceFactory";
    public final static String CONF_SERVICE_STOP_LISTENER = "genericLifecycleStopListener";
    
    public final static String CONF_SIMPLE_LIFECYCLE_SERVICE = "simpleLifecycleService";
    
    private static DefaultConfiguration<String> buildBaseConfig(
            String[] classNames, Properties props){
        DefaultConfiguration<String> conf = new DefaultConfiguration<String>();
        if(classNames == null){
            throw new NullPointerException();
        }
        conf.addProperty(String[].class, CONF_SERVICE_CLASSES, classNames);
        conf.addProperty(Properties.class, CONF_SERVICE_PROPERTIES, props);
        return conf;
    }
    
    public static <T> Configuration<String> buildSimpleLifecycleConfig(
            Class<T> clazz, String[] availableClassNames, 
            Properties props, T service){
        DefaultConfiguration<String> conf = 
                buildBaseConfig(availableClassNames, props);
        //conf.addProperty(clazz, CONF_SERVICE_CLASSES, service);
        return conf;
    }
    
    public static <T> Configuration<String> buildSimpleLifecycleConfig(
            Class<T> clazz, Properties props, T service){
        DefaultConfiguration<String> conf = 
                buildBaseConfig(new String[]{clazz.getName()}, props);
        //conf.addProperty(clazz, CONF_SERVICE_CLASSES, service);
        return conf;
    }
    
    public static <T> Configuration<String> buildSimpleLifecycleConfig(
            Class<T> clazz, String idKey, String idStr, Properties props, T service){
        DefaultConfiguration<String> conf = buildBaseConfig(
                new String[]{clazz.getName()}, 
                getProps(idKey, idStr, props));
        //conf.addProperty(clazz, CONF_SERVICE_CLASSES, service);
        return conf;
    }
    
    private static Properties getProps(String idKey, String idStr, Properties props){
        if(props == null){
            props = new Properties();
        }
        props.put(idKey, idStr);
        return props;
    }
    
    private static <T> Configuration<String> addGenericLifecycleProps(
            DefaultConfiguration<String> conf, 
            Iterable<Configuration<String>> dependencyConfigs,  
            Adapter<Map<String,Object>, T> serviceFactory,
            Listener<T> stopListener){
        conf.addProperty(Iterable.class, 
                CONF_DEPENDENCY_CONFIGS, dependencyConfigs);
        conf.addProperty(Adapter.class, CONF_SERVICE_FACTORY, serviceFactory);
        if(stopListener != null){
            conf.addProperty(Listener.class, CONF_SERVICE_STOP_LISTENER, stopListener);
        }
        return conf;
    }
    
    public static Configuration<String> buildGenericLifecycleConfig(
            String[] classNames, Properties props, 
            Iterable<Configuration<String>> dependencyConfigs,  
            Adapter<Map<String,Object>, ?> serviceFactory){
        DefaultConfiguration<String> conf = buildBaseConfig(classNames, props);
        return addGenericLifecycleProps(conf, dependencyConfigs, serviceFactory, null);
    }
    
    public static Configuration<String> buildGenericLifecycleConfig(
            String[] classNames, String idKey, String idStr, Properties props, 
            Iterable<Configuration<String>> dependencyConfigs,  
            Adapter<Map<String,Object>, ?> serviceFactory){
        return addGenericLifecycleProps(
                buildBaseConfig(classNames, getProps(idKey, idStr, props)), 
                dependencyConfigs, serviceFactory, null);
    }
    
    public static <T> Configuration<String> buildGenericLifecycleConfig(
            String[] classNames, String idKey, String idStr, Properties props, 
            Iterable<Configuration<String>> dependencyConfigs,  
            Adapter<Map<String,Object>, T> serviceFactory,
            Listener<T> stopListener){
        return addGenericLifecycleProps(
                buildBaseConfig(classNames, getProps(idKey, idStr, props)), 
                dependencyConfigs, serviceFactory, stopListener);
    }
    
    public static SimpleLifecycle buildSimpleLifecycle(
            Configuration<String> conf){
        String[] availableClassNames = 
                conf.getPropertyValue(String[].class, CONF_SERVICE_CLASSES);
        Properties props = 
                conf.getPropertyValue(Properties.class, CONF_SERVICE_PROPERTIES);
        Object service = conf.getPropertyValue(CONF_SIMPLE_LIFECYCLE_SERVICE);
        if(availableClassNames == null || service == null){
            throw new NullPointerException();
        }
        return new SimpleLifecycle(service, availableClassNames, props);
    }
    
    public static GenericLifecycle buildGenericLifecycle(
            Configuration<String> conf){
        String[] availableClassNames = 
                conf.getPropertyValue(String[].class, CONF_SERVICE_CLASSES);
        Properties props = 
                conf.getPropertyValue(Properties.class, CONF_SERVICE_PROPERTIES);
        Iterable<Configuration<String>> dependencyConfigs = 
                conf.getPropertyValue(Iterable.class, CONF_DEPENDENCY_CONFIGS);
        Adapter<Map<String,Object>, ?> serviceFactory = 
                conf.getPropertyValue(Adapter.class, CONF_SERVICE_FACTORY);
        Listener<?> stopListener = 
                conf.getPropertyValue(Listener.class, CONF_SERVICE_STOP_LISTENER);
        if(availableClassNames == null 
                || dependencyConfigs == null || serviceFactory == null){
            throw new NullPointerException();
        }
        List<IndexedValue<DependencyDescriptor, Listener<DependencyChange>>> 
                 indexedChangeListeners = 
                        buildLifecycleDependencies(dependencyConfigs);
        List<DependencyDescriptor> descs = 
                new ArrayList(indexedChangeListeners.size());
        Map<String,Listener<DependencyChange>> changeListenerMap = 
                new HashMap<String, Listener<DependencyChange>>(
                        indexedChangeListeners.size());
        for(IndexedValue<DependencyDescriptor, 
                Listener<DependencyChange>> v : indexedChangeListeners){
            DependencyDescriptor d = v.getIndex();
            Listener<DependencyChange> l = v.getValue();
            descs.add(d);
            if(l != null){
                changeListenerMap.put(d.getDependencyName(), l);
            }
        }
        Adapter<String,Listener<DependencyChange>> listenerFinder = 
                new MapAdapter(changeListenerMap);
        return new GenericLifecycle(
                availableClassNames, descs, props, 
                serviceFactory, listenerFinder, stopListener);
    }
    
    private static List<IndexedValue<DependencyDescriptor,
            Listener<DependencyChange>>> buildLifecycleDependencies(
                    Iterable<Configuration<String>> configs){
        List<IndexedValue<DependencyDescriptor,
                Listener<DependencyChange>>> changeListeners = new ArrayList();
        for(Configuration<String> conf : configs){
            IndexedValue<DependencyDescriptor,Listener<DependencyChange>> val = 
                    RKDependencyConfigUtils.buildLifecycleDependency(conf);
            changeListeners.add(val);
        }
        return changeListeners;
    }
    
    public static class SimpleLifecycleFactory<T> implements 
            Adapter<Configuration<String>,ServiceLifecycleProvider<T>>{
        @Override
        public ServiceLifecycleProvider<T> adapt(Configuration<String> a) {
            return buildSimpleLifecycle(a);
        }
    }
    
    public static class GenericLifecycleFactory<T> implements 
            Adapter<Configuration<String>,ServiceLifecycleProvider<T>>{
        @Override
        public ServiceLifecycleProvider<T> adapt(Configuration<String> a) {
            return buildGenericLifecycle(a);
        }
    }
}
