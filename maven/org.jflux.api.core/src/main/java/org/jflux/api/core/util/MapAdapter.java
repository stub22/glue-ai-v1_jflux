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
 *
 * @param <K> 
 * @param <V> 
 * @author Matthew Stevenson <www.jflux.org>
 */
public class MapAdapter<K,V> implements Adapter<K,V>{
    private Map<K,V> myMap;
    
    /**
     *
     */
    public MapAdapter(){
        myMap = new HashMap<K, V>();
    }
    
    /**
     *
     * @param map
     */
    public MapAdapter(Map<K,V> map){
        if(map == null){
            throw new NullPointerException();
        }
        myMap = map;
    }
    
    /**
     *
     * @param a
     * @return
     */
    @Override
    public V adapt(K a) {
        return myMap.get(a);
    }
    
    /**
     *
     * @return
     */
    public Map<K,V> getMap(){
        return myMap;
    }
    
    /**
     *
     * @param <K>
     * @param <V>
     * @param <T>
     */
    public static class MapValueAdapter<K,V,T> implements Adapter<Map<K,V>,T> {
        private K myKey;
        private Adapter<V,T> myAdapter;
        
        /**
         *
         * @param key
         * @param adapter
         */
        public MapValueAdapter(K key, Adapter<V,T> adapter){
            if(key == null || adapter == null){
                throw new NullPointerException();
            }
            myKey = key;
            myAdapter = adapter;
        }
        
        /**
         *
         * @param a
         * @return
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
