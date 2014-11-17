/*
 * Copyright 2014 the JFlux Project.
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

package org.jflux.api.common.rk.utils;

import org.jflux.api.core.Source;

/**
 * Defines a class which can get and set an object of the specified type.
 * @param <T> type of object to get and set
 * 
 * @author Matthew Stevenson <www.jflux.org>
 */
public interface RKSource<T> extends Source<T>{
    /**
     * Set the object value.
     * @param t the value to set
     */
    public void set(T t);

    /**
     * Generic Source implementation.
     * @param <T> object class
     */
    public static class SourceImpl<T> implements RKSource<T>{
        private T myT;
        /**
         * Creates a new SourceImpl with a null value.
         */
        public SourceImpl(){}
        /**
         * Creates a new SourceImpl with the given value.
         * @param t value to set
         */
        public SourceImpl(T t){
            myT = t;
        }
        
        @Override
        public T getValue(){
            return myT;
        }
        
        @Override
        public void set(T t){
            myT = t;
        }
    }
}
