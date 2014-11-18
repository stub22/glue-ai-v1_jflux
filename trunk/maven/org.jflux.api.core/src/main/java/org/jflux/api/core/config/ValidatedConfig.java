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
import org.jflux.api.core.Adapter;

/**
 * Configuration guaranteed to be valid
 * @author Matthew Stevenson
 * @param <K> key type
 * @param <C> validator type
 */
public class ValidatedConfig<K,C extends ConfigValidator> extends 
        ReadOnlyConfig<K> {
    private C myValidator;

    private ValidatedConfig(Configuration<K> config, C validator) {
        super(config);
        if(validator == null){
            throw new NullPointerException();
        }
        
        myValidator = validator;
    }
    
    /**
     * Get the validator for proof of validation
     * @return ConfigValidator used to validate the Configuration
     */
    public C getValidator(){
        return myValidator;
    }
    
    /**
     * Runs a Configuration through a ConfigValidator and generates a
     * ValidatedConfig
     * @param <K> key type
     * @param <E> error type
     * @param <C> validator type
     */
    public final static class Validator<K,E,C extends ConfigValidator<K,E>> implements
            Adapter<Configuration<K>,ValidatedConfig<K,C>> {
        private C myValidator;
        private List<E> myErrors;
        
        /**
         * Create a Validator from a ConfigValidator
         * @param configValidator the ConfigValidator
         */
        public Validator(C configValidator){
            if(configValidator == null){
                throw new NullPointerException();
            }
            myValidator = configValidator;
        }
        
        /**
         * Validates a Configuration
         * @param a Configuration to validate
         * @return ValidatedConfig
         */
        @Override
        public ValidatedConfig<K,C> adapt(Configuration<K> a) {
            List<E> errors = myValidator.adapt(a);
            if(errors != null && !errors.isEmpty()){
                myErrors = errors;
                return null;
            }
            return new ValidatedConfig<K,C>(a, myValidator);
        }
        
        /**
         * Get validation errors
         * @return List of errors
         */
        public List<E> getErrors(){
            return myErrors;
        }
    }
}
