/*
 * Copyright 2012 by The JFlux Project (www.jflux.org).
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

import org.jflux.api.core.Adapter;
import java.util.HashMap;
import java.util.Map;

/**
 * Adapter that converts items per an internal Map
 * @author Matthew Stevenson <www.jflux.org>
 * @param <K> input data type
 * @param <V> output data type
 */
public class MapAdapter<K,V> implements Adapter<K,V>{
    private Map<K,V> myMap;
    
    /**
     * Creates an empty MapAdapter
     */
    public MapAdapter(){
        myMap = new HashMap<K, V>();
    }
    
    /**
     * Creates a MapAdapter with existing Map
     * @param map Map to use
     */
    public MapAdapter(Map<K,V> map){
        if(map == null){
            throw new NullPointerException();
        }
        myMap = map;
    }
    
    /**
     * Converts values using the internal Map as a table
     * @param a input value
     * @return output value
     */
    @Override
    public V adapt(K a) {
        return myMap.get(a);
    }
    
    /**
     * Gets the internal Map
     * @return the internal Map
     */
    public Map<K,V> getMap(){
        return myMap;
    }
    
    /**
     * Adapter to yank and process a value out of a Map given a pre-set key
     * @param <K> key type
     * @param <V> map value type
     * @param <T> output type
     */
    public static class MapValueAdapter<K,V,T> implements Adapter<Map<K,V>,T> {
        private K myKey;
        private Adapter<V,T> myAdapter;
        
        /**
         * Builds a MapValueAdapter from a key and Adapter
         * @param key the key
         * @param adapter the internal Adapter
         */
        public MapValueAdapter(K key, Adapter<V,T> adapter){
            if(key == null || adapter == null){
                throw new NullPointerException();
            }
            myKey = key;
            myAdapter = adapter;
        }
        
        /**
         * Yank a Map's value per the internal key and run it through the
         * internal Adapter
         * @param a input map
         * @return processed value
         */
        @Override
        public T adapt(Map<K, V> a) {
            if(a == null){
                return null;
            }
            V val = a.get(myKey);
            return myAdapter.adapt(val);
        }
        
    }
}
