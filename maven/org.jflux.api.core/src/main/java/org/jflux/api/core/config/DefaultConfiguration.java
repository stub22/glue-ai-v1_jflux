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
package org.jflux.api.core.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.jflux.api.core.util.IndexedValue;

/**
 *
 * @author Matthew Stevenson
 */
public class DefaultConfiguration<K> extends AbstractConfiguration<K> {
    private Map<K,ConfigProperty<?>> myPropertyMap;
    
    public DefaultConfiguration(){
        myPropertyMap = new HashMap<K, ConfigProperty<?>>();
    }
    
    public <T> void addProperty(Class<T> propClass, K key, T val){
        if(propClass == null || key == null){
            throw new NullPointerException();
        }if(myPropertyMap.containsKey(key)){
            throw new IllegalStateException("Unable to add property.  "
                    + "Key (" + key + ") already in use.");
        }
        ConfigProperty<T> prop = new DefaultConfigProperty<T>(propClass, val);
        myPropertyMap.put(key, prop);
    }
    
    public void addProperty(IndexedValue<K,ConfigProperty> indexedProperty){
        if(indexedProperty == null){
            throw new NullPointerException();
        }
        K key = indexedProperty.getIndex();
        ConfigProperty prop = indexedProperty.getValue();
        if(key == null || prop == null){
            throw new NullPointerException();
        }if(myPropertyMap.containsKey(key)){
            throw new IllegalStateException("Unable to add property.  "
                    + "Key (" + key + ") already in use.");
        }
        myPropertyMap.put(key, prop);
    }
    
    @Override
    public Set<K> getKeySet() {
        return myPropertyMap.keySet();
    }
    
    @Override
    protected ConfigProperty getConfigProperty(K key){
        if(!myPropertyMap.containsKey(key)){
            return null;
        }
        return myPropertyMap.get(key);
    }
}
