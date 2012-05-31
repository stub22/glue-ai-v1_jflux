/*
 * Copyright 2012 The Cogchar Project (www.cogchar.org).
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
package org.jflux.api.core.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.jflux.api.core.Listener;
import org.jflux.api.core.Notifier;
import org.jflux.api.core.Source;
import org.jflux.api.core.event.BasicHeader.HeaderSource;
import org.jflux.api.core.event.Event;
import org.jflux.api.core.event.EventUtils;
import org.jflux.api.core.event.Header;
import org.jflux.api.core.event.ValueChange;

/**
 *
 * @author Matthew Stevenson
 */
public class DefaultConfiguration<Time,K> implements Configuration<K> {
    public final static String PROP_CONFIG_CHANGE = "configurationChange";
    
    private Map<K,ConfigProperty<Time,?>> myPropertyMap;
    private Source<Time> myTimestampSource;
    
    public DefaultConfiguration(Source<Time> timestampSource){
        if(timestampSource == null){
            throw new NullPointerException();
        }
        myTimestampSource = timestampSource;
        myPropertyMap = new HashMap<K, ConfigProperty<Time,?>>();
    }
    
    public <T> void addProperty(Class<T> propClass, K key, T val){
        if(propClass == null || key == null){
            throw new NullPointerException();
        }if(myPropertyMap.containsKey(key)){
            throw new IllegalStateException("Unable to add property.  "
                    + "Key (" + key + ") already in use.");
        }
        ConfigProperty<Time,T> prop = 
                new ConfigProperty<Time,T>(propClass, val, headerSource(key));
        myPropertyMap.put(key, prop);
    }
    
    @Override
    public Set<K> getKeySet() {
        return myPropertyMap.keySet();
    }

    @Override
    public Source<?> getPropertySource(K key) {
        return getConfigProperty(key);
    }

    @Override
    public <T> Source<T> getPropertySource(Class<T> propClass, K key) {
        return getConfigProperty(propClass, key);
    }

    @Override
    public Notifier<?> getPropertyNotifier(K key) {
        ConfigProperty prop = getConfigProperty(key);
        if(prop == null){
            return null;
        }
        return prop.getNotifier();
    }

    @Override
    public <T> Notifier<T> getPropertyNotifier(Class<T> propClass, K key) {
        ConfigProperty prop = getConfigProperty(propClass, key);
        if(prop == null){
            return null;
        }
        return prop.getNotifier();
    }

    @Override
    public Listener<?> getPropertySetter(K key) {
        ConfigProperty prop = getConfigProperty(key);
        if(prop == null){
            return null;
        }
        return prop.getListener();
    }

    @Override
    public <T> Listener<T> getPropertySetter(Class<T> propClass, K key) {
        ConfigProperty prop = getConfigProperty(propClass, key);
        if(prop == null){
            return null;
        }
        return prop.getListener();
    }
    
    private ConfigProperty<Time,?> getConfigProperty(K key){
        if(!myPropertyMap.containsKey(key)){
            return null;
        }
        return myPropertyMap.get(key);
    }
    
    private <T> ConfigProperty<Time,T> getConfigProperty(Class<T> propClass, K key){
        ConfigProperty prop = myPropertyMap.get(key);
        if(prop == null){
            return null;
        }
        Class actualClass = prop.getPropertyClass();
        if(actualClass == null || !propClass.isAssignableFrom(actualClass)){
            return null;
        }
        return prop;
    }
    
    private Source<Header<Configuration<?>,Time>> headerSource(K key){
        return new HeaderSource<Configuration<?>, Time>(
                this, myTimestampSource, 
                PROP_CONFIG_CHANGE, key.toString(), null);
    }
    
    private static class ConfigProperty<Time, V> implements Source<V> {
        private V myValue;
        private Listener<V> myListener;
        private Notifier<Event<
                    Header<Configuration<?>,Time>,ValueChange<V>>> myNotifier;
        private Class<V> myValueClass;
        private Source<Header<Configuration<?>,Time>> myHeaderSource;

        ConfigProperty(Class<V> valueClass, V value,
                Source<Header<Configuration<?>,Time>> headerSource) {
            myValue = value;
            myValueClass = valueClass;
            myNotifier = new DefaultNotifier<Event<
                    Header<Configuration<?>,Time>,ValueChange<V>>>();
            myListener = new PropertySetter();
            myHeaderSource = headerSource;
        }
        
        @Override
        public V getValue(){
           return myValue; 
        }
        
        Notifier<Event<Header<Configuration<?>,Time>,
                ValueChange<V>>> getNotifier(){
            return myNotifier;
        }
        
        Listener<V> getListener(){
            return myListener;
        }
        
        public Class<V> getPropertyClass(){
            return myValueClass;
        }
    
        private class PropertySetter implements Listener<V> {
            @Override
            public void handleEvent(V event) {
                V old = myValue;
                myValue = event;
                Header<Configuration<?>,Time> header
                        = myHeaderSource.getValue();
                Event<Header<Configuration<?>,Time>,ValueChange<V>> evt = 
                        EventUtils.newChangeEvent(header, old, myValue);
                myNotifier.notifyListeners(evt);
            }
        }
    }
}
