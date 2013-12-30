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
 *
 * @param <T> 
 * @author Matthew Stevenson
 */
public interface ValueChange<T> {    
    /**
     *
     * @return
     */
    public T getOldValue();
    /**
     *
     * @return
     */
    public T getNewValue();
    
    /**
     *
     * @param <T>
     */
    public static class DefaultValueChange<T> implements ValueChange<T> {
        private T myOldVal;
        private T myNewVal;
        
        /**
         *
         * @param oldVal
         * @param newVal
         */
        public DefaultValueChange(T oldVal, T newVal){
            myOldVal = oldVal;
            myNewVal = newVal;
        }

        /**
         *
         * @return
         */
        @Override
        public T getOldValue() {
            return myOldVal;
        }

        /**
         *
         * @return
         */
        @Override
        public T getNewValue() {
            return myNewVal;
        }
    }
}
