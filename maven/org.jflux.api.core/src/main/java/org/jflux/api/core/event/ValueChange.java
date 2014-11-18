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
package org.jflux.api.core.event;

/**
 * Represents a difference in values
 * @author Matthew Stevenson
 * @param <T> type of value
 */
public interface ValueChange<T> {    

    /**
     * Get the original value
     * @return the original value
     */
    public T getOldValue();

    /**
     * Get the new value
     * @return the new value
     */
    public T getNewValue();
    
    /**
     * Basic implementation of a ValueChange
     * @param <T> value type
     */
    public static class DefaultValueChange<T> implements ValueChange<T> {
        private T myOldVal;
        private T myNewVal;
        
        /**
         * Builds a DefaultValueChange from old and new values
         * @param oldVal the old value
         * @param newVal the new value
         */
        public DefaultValueChange(T oldVal, T newVal){
            myOldVal = oldVal;
            myNewVal = newVal;
        }

        /**
         * Get the original value
         * @return the original value
         */
        @Override
        public T getOldValue() {
            return myOldVal;
        }

        /**
         * Get the new value
         * @return the new value
         */
        @Override
        public T getNewValue() {
            return myNewVal;
        }
    }
}
