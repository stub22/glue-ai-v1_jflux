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

import java.util.List;
import java.util.Set;
import org.jflux.api.core.Adapter;

/**
 *
 * @author Matthew Stevenson
 */
public interface ConfigValidator<K, E> extends 
        Adapter<Configuration<K>,List<E>> {
    
    @Override
    public List<E> adapt(Configuration<K> a);
    
    public abstract class AbstractConfigValidator<K,E> implements 
            ConfigValidator<K, E> {
        public abstract Set<K> getValidKeySet();
        
        
        //protected abstract <V> Adapter<V,List<E>> getFieldValidator(  
                //does not compile in JDK >= 1.6.25
        protected abstract Adapter getFieldValidator(
                K key, Configuration<K> config);

        protected abstract <V> Adapter<V,List<E>> getFieldValidator(
                Class<V> clazz, K key, Configuration<K> config);

        @Override
        public List<E> adapt(Configuration<K> a) {
            List<E> allErrors = null;
            for(K k : getValidKeySet()){
                List<E> errors = validateField(k, a);
                if(errors != null && !errors.isEmpty()){
                    if(allErrors == null){
                        allErrors = errors;
                    }else{
                        allErrors.addAll(errors);
                    }
                }
            }
            return allErrors;
        }

        //private <V> List<E> validateField(K k, Configuration<K> a){  
                //does not compile in JDK >= 1.6.25
        private List<E> validateField(K k, Configuration<K> a){
            //Adapter<V,List<E>> fieldAdapter = getFieldValidator(k, a);
            //V v = a.getPropertyValue(k);
            Adapter<Object,List<E>> fieldAdapter = getFieldValidator(k, a);
            Object v = a.getPropertyValue(k);
            return fieldAdapter.adapt(v);
        }
    }
}
