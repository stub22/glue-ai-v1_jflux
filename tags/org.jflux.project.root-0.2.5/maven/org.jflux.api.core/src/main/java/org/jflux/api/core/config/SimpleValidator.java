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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jflux.api.core.Adapter;
import org.jflux.api.core.config.ConfigValidator.AbstractConfigValidator;
import org.jflux.api.core.config.SimpleValidator.PropertyError;

/**
 * Basic implementation of an AbstractConfigValidator
 * @author Matthew Stevenson
 * @param <K>
 */
public class SimpleValidator<K> extends 
        AbstractConfigValidator<K, PropertyError<K,?>> {
    
    private Map<K, Adapter<?, List<PropertyError<K,?>>>> myPropertyValidators;

    /**
     * Get valid key Set
     * @return Set of valid keys
     */
    @Override
    public Set<K> getValidKeySet() {
        return myPropertyValidators.keySet();
    }

    /**
     * Get the validator for a particular key
     * @param key key to validate
     * @param config Configuration to operate on
     * @return validator for key
     */
    @Override
    //protected <V> Adapter<V, List<PropertyError<K, ?>>> getFieldValidator(
    protected Adapter<?, List<PropertyError<K, ?>>> getFieldValidator(
            K key, Configuration<K> config) {
        return (Adapter)myPropertyValidators.get(key);
    }

    /**
     * Get typed validator for a particular key
     * @param <V> type of value
     * @param clazz Class of value
     * @param key key to validate
     * @param config Configuration to operate on
     * @return validator for key
     */
    @Override
    protected <V> Adapter<V, List<PropertyError<K, ?>>> getFieldValidator(
            Class<V> clazz, K key, Configuration<K> config) {
        return (Adapter)myPropertyValidators.get(key);
    }    
    
    /**
     * Validates an individual property
     * @param <K> property key type
     * @param <V> property value type
     */
    public static class EnumeratedPropertyValidator<K,V> implements 
            Adapter<V,List<PropertyError<K,V>>> {
        private K myKey;
        private Set<V> myValidProperties;
        
        /**
         * Create a validator from a key and Set of valid values
         * @param propKey key to validate
         * @param validSet Set of valid values
         */
        public EnumeratedPropertyValidator(K propKey, Set<V> validSet){
            if(propKey == null || validSet == null){
                throw new NullPointerException();
            }else if(validSet == null || validSet.isEmpty()){
                throw new IllegalArgumentException(
                        "Valid Set must not be empty.");
            }
            myValidProperties = validSet;
        }
        
        /**
         * Validates a value
         * @param a value to validate
         * @return List of errors
         */
        @Override
        public List<PropertyError<K,V>> adapt(V a) {
            return myValidProperties.contains(a) ? null : 
                    Arrays.asList(new PropertyError<K,V>(
                            myKey, a, "Invalid property value."));
        }
    }
    
    /**
     * Validation error
     * @param <K> key type
     * @param <V> value type
     */
    public static class PropertyError<K,V> {
        private K myKey;
        private V myValue;
        private String myErrorMessage;

        /**
         * Create a PropertyError from key, value, and error message
         * @param key invalid key
         * @param value invalid value
         * @param errorMessage error message
         */
        public PropertyError(K key, V value, String errorMessage) {
            myKey = key;
            myValue = value;
            myErrorMessage = errorMessage;
        }

        /**
         * Get the key
         * @return the key
         */
        public K getKey() {
            return myKey;
        }

        /**
         * Get the value
         * @return the value
         */
        public V getValue() {
            return myValue;
        }

        /**
         * Get the error message
         * @return the error message
         */
        public String getErrorMessage() {
            return myErrorMessage;
        }
    }
}
