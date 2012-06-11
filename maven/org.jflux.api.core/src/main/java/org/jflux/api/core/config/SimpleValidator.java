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
import java.util.Set;
import org.jflux.api.core.Adapter;

/**
 *
 * @author Matthew Stevenson
 */
public class SimpleValidator {
    public static class EnumeratedPropertyValidator<K,V> implements 
            Adapter<V,List<PropertyError<K,V>>> {
        private K myKey;
        private Set<V> myValidProperties;
        
        public EnumeratedPropertyValidator(K propKey, Set<V> validSet){
            if(propKey == null || validSet == null){
                throw new NullPointerException();
            }else if(validSet == null || validSet.isEmpty()){
                throw new IllegalArgumentException(
                        "Valid Set must not be empty.");
            }
            myValidProperties = validSet;
        }
        
        @Override
        public List<PropertyError<K,V>> adapt(V a) {
            return myValidProperties.contains(a) ? null : 
                    Arrays.asList(new PropertyError<K,V>(
                            myKey, a, "Invalid property value."));
        }
    }
    
//    public static class PropertyStateValidator<K,V,E> implements 
//            Adapter<V,List<PropertyError<K,V>>> {
//        private Configuration<K> myConfiguration;
//        
//        @Override
//        public List<PropertyError<K,V>> adapt(V a) {
//            throw new UnsupportedOperationException("Not supported yet.");
//        }
//    }
    
    public static class PropertyError<K,V> {
        private K myKey;
        private V myValue;
        private String myErrorMessage;

        public PropertyError(K key, V value, String errorMessage) {
            myKey = key;
            myValue = value;
            myErrorMessage = errorMessage;
        }

        public K getKey() {
            return myKey;
        }

        public V getValue() {
            return myValue;
        }

        public String getErrorMessage() {
            return myErrorMessage;
        }
    }
}
