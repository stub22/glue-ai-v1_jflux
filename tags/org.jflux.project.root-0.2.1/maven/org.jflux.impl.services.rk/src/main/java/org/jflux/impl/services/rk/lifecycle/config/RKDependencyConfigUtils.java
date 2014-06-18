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

import java.util.Properties;
import org.jflux.api.core.Adapter;
import org.jflux.api.core.Listener;
import org.jflux.api.core.config.Configuration;
import org.jflux.api.core.config.DefaultConfiguration;
import org.jflux.api.core.util.IndexedValue;
import org.jflux.api.core.util.IndexedValue.BasicIndexedValue;
import org.jflux.impl.services.rk.lifecycle.DependencyDescriptor;
import org.jflux.impl.services.rk.lifecycle.config.GenericLifecycle.DependencyChange;
import org.jflux.impl.services.rk.osgi.OSGiUtils;

/**
 *
 * @author Matthew Stevenson
 */
public class RKDependencyConfigUtils {
    public final static String CONF_DEPENDENCY_NAME = "lifecycleDependencyLocalName";
    public final static String CONF_DEPENDENCY_CLASS = "lifecycleDependencyClass";
    public final static String CONF_DEPENDENCY_PROPERTIES = "lifecycleDependencyProperties";
    
    public final static String CONFIG_DEPENDENCY_DESCRIPTOR = "lifecycleDependencyDescriptorConfig";
    public final static String CONF_DEPENDENCY_CHANGE_LISTENER = "lifecycleDependencyChangeListener";
        
    private static DefaultConfiguration<String> buildBaseDescriptorConfig(
            String name, Class clazz, Properties props){
        DefaultConfiguration<String> conf = new DefaultConfiguration<String>();
        if(name == null || clazz == null){
            throw new NullPointerException();
        }
        conf.addProperty(String.class, CONF_DEPENDENCY_NAME, name);
        conf.addProperty(Class.class, CONF_DEPENDENCY_CLASS, clazz);
        conf.addProperty(Properties.class, CONF_DEPENDENCY_PROPERTIES, props);
        return conf;
    }
    
    public static Configuration<String> buildDescriptorConfig(
            String name, Class clazz, Properties props){
        return buildBaseDescriptorConfig(name, clazz, props);
    }
    
    public static <T,D> Configuration<String> buildLifecycleDependencyConfig(
            String name, Class<D> clazz, Properties props, 
            Listener<DependencyChange<T,D>> changeListener){
        return buildLifecycleDependencyConfig(
                buildBaseDescriptorConfig(name, clazz, props), changeListener);
    }
    
    public static <T,D> Configuration<String> buildLifecycleDependencyConfig(
            String name, Class<D> clazz, String idKey, String idStr, 
            Properties props, Listener<DependencyChange<T,D>> changeListener){
        return buildLifecycleDependencyConfig(
                buildBaseDescriptorConfig(name, clazz, 
                        getProps(idKey, idStr, props)), changeListener);
    }
    
    private static Properties getProps(String idKey, String idStr, Properties props){
        if(props == null){
            props = new Properties();
        }
        props.put(idKey, idStr);
        return props;
    }
    
    public static <T,D> Configuration<String> buildLifecycleDependencyConfig(
            Configuration<String> descriptorConfig, 
            Listener<DependencyChange<T,D>> changeListener){
        DefaultConfiguration<String> conf = new DefaultConfiguration<String>();
        if(descriptorConfig == null){
            throw new NullPointerException();
        }
        conf.addProperty(Configuration.class, CONFIG_DEPENDENCY_DESCRIPTOR, descriptorConfig);
        conf.addProperty(Listener.class, CONF_DEPENDENCY_CHANGE_LISTENER, changeListener);
        return conf;
    }
    
    public static DependencyDescriptor buildDescriptor(Configuration<String> a) {
        if(a == null){
            throw new NullPointerException();
        }
        String name = a.getPropertyValue(
                String.class, CONF_DEPENDENCY_NAME);
        Class clazz = a.getPropertyValue(
                Class.class, CONF_DEPENDENCY_CLASS);
        Properties props = a.getPropertyValue(
                Properties.class, CONF_DEPENDENCY_PROPERTIES);
        String filter = OSGiUtils.createServiceFilter(props);
        
        return new DependencyDescriptor(name, clazz, filter);
    }
    
    public static IndexedValue<DependencyDescriptor,Listener<DependencyChange>> 
            buildLifecycleDependency(Configuration<String> a) {
        if(a == null){
            throw new NullPointerException();
        }
        Configuration<String> conf = a.getPropertyValue(
                Configuration.class, CONFIG_DEPENDENCY_DESCRIPTOR);
        Listener changeListener = a.getPropertyValue(
                Listener.class, CONF_DEPENDENCY_CHANGE_LISTENER);
        if(conf == null){
            throw new NullPointerException();
        }
        DependencyDescriptor desc = buildDescriptor(conf);
        if(desc == null){
            throw new NullPointerException();
        }
        return new BasicIndexedValue(desc, changeListener);
    }
    
    public static class DependencyDescriptorBuilder implements 
            Adapter<Configuration<String>, DependencyDescriptor> {

        @Override
        public DependencyDescriptor adapt(Configuration<String> a) {
            return buildDescriptor(a);
        }
    }
}
