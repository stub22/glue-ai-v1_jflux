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
package org.jflux.api.core.util;

import org.jflux.api.core.Adapter;

/**
 * Index/value pair
 * @author Matthew Stevenson
 * @param <I> index type
 * @param <V> value type
 */
public interface IndexedValue<I,V> {

    /**
     * Get the index
     * @return the index
     */
    public I getIndex();

    /**
     * Get the value
     * @return the value
     */
    public V getValue();
    
    /**
     * Simple implementation of IndexedValue
     * @param <I> index type
     * @param <V> value type
     */
    public static class BasicIndexedValue<I,V> implements IndexedValue<I, V> {
        private I myIndex;
        private V myValue;
        
        /**
         * Build a BasicIndexedValue from index and value
         * @param index the index
         * @param value the value
         */
        public BasicIndexedValue(I index, V value){
            if(index == null){
                throw new NullPointerException();
            }
            myIndex = index;
            myValue = value;
        }
        
        /**
         * Get the index
         * @return the index
         */
        @Override
        public I getIndex() {
            return myIndex;
        }

        /**
         * Get the value
         * @return the value
         */
        @Override
        public V getValue() {
            return myValue;
        }
        
    }
    
    /**
     *
     * @param <I>
     */
    public static class IndexAdapter<I> implements Adapter<IndexedValue<I,?>,I>{

        /**
         *
         * @param a
         * @return
         */
        @Override
        public I adapt(IndexedValue<I, ?> a) {
            return a.getIndex();
        }
    }
    
    /**
     * Adapter to strip the index off an IndexedValue
     * @param <V> type of value
     */
    public static class ValueAdapter<V> implements Adapter<IndexedValue<?,V>,V>{

        /**
         * Strips the index off an IndexedValue
         * @param a an IndexedValue
         * @return the value
         */
        @Override
        public V adapt(IndexedValue<?, V> a) {
            return a.getValue();
        }
    }
}
