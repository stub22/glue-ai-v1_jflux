/*
 * Copyright 2012 The JFlux Project (www.jflux.org).
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
package org.jflux.api.core.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.jflux.api.core.Listener;
import org.jflux.api.core.Notifier;
import org.jflux.api.core.Source;
import org.jflux.api.core.event.ValueChange;
import org.jflux.api.core.util.DefaultSource;

/**
 * A read-only implementation of a Configuration
 * @author Matthew Stevenson
 * @param <K>
 */
public class ReadOnlyConfig<K> extends AbstractConfiguration<K> {
    private final Map<K,ReadOnlyProperty> myProperties;
    
    ReadOnlyConfig(Map<K,ConfigProperty> properties){
        if(properties == null){
            throw new NullPointerException();
        }
        Map<K,ReadOnlyProperty> props = new HashMap(properties.size());
        for(Entry<K,ConfigProperty> e : properties.entrySet()){
            props.put(e.getKey(), new ReadOnlyProperty(e.getValue()));
        }
        myProperties = Collections.unmodifiableMap(props);
    }
    
    ReadOnlyConfig(Configuration<K> config){
        if(config == null){
            throw new NullPointerException();
        }
        Set<K> keys = config.getKeySet();
        Map<K,ReadOnlyProperty> props = new HashMap(keys.size());
        for(K key : keys){
            Class c = config.getPropertyClass(key);
            Object obj = config.getPropertyValue(key);
            props.put(key, new ReadOnlyProperty(c,obj));
        }
        myProperties = Collections.unmodifiableMap(props);
    }
    
    @Override
    public <T> Listener<T> getPropertySetter(K key) {
        return null;
    }

    @Override
    public <T> Listener<T> getPropertySetter(Class<T> propertyClass, K key) {
        return null;
    }

    /**
     * Get the value associated with a key
     * @param <T> type of the property
     * @param key the key
     * @return value associated with the key
     */
    @Override
    protected final <T> ConfigProperty<T> getConfigProperty(K key) {
        return myProperties.get(key);
    }

    @Override
    public Set<K> getKeySet() {
        return myProperties.keySet();
    }
    
    /**
     * Represents an immutable property
     * @param <V> type of the property
     */
    public final static class ReadOnlyProperty<V> implements ConfigProperty<V>{
        private final Class<V> myValueClass;
        private final Source<V> mySource;

        /**
         * Initializes a property with type and value
         * @param valueClass type of the property
         * @param value value of the property
         */
        public ReadOnlyProperty(Class<V> valueClass, V value) {
            if(valueClass == null){
                throw new NullPointerException();
            }
            myValueClass = valueClass;
            mySource = new DefaultSource<V>(value);
        }

        /**
         * Initializes a property from another property
         * @param prop the new property
         */
        public ReadOnlyProperty(ConfigProperty<V> prop){
            this(prop.getPropertyClass(), 
                    prop.getSource() == null 
                            ? null : prop.getSource().getValue());
        }

        @Override
        public final Source<V> getSource(){
           return mySource; 
        }

        @Override
        public Notifier<ValueChange<V>> getNotifier(){
            return null;
        }

        @Override
        public Listener<V> getSetter() {
            return null;
        }

        @Override
        public Class<V> getPropertyClass(){
            return myValueClass;
        }
    }
}
