    /*
 * Copyright 2012 Hanson Robokind LLC.
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
package org.jflux.impl.services.rk.lifecycle.config;

import java.util.List;
import org.jflux.api.core.Adapter;
import org.jflux.api.core.Source;
import org.jflux.api.core.config.ConfigValidator;
import org.jflux.api.core.config.Configuration;
import org.jflux.api.core.config.DefaultConfiguration;
import org.jflux.api.core.config.ValidatedConfig;
import org.jflux.api.core.config.ValidatedConfig.Validator;
import org.jflux.api.core.util.DefaultSource;

/**
 *
 * @author Matthew Stevenson
 */
public class SelfBuildingConfig {
    public final static String CONF_SERVICE_CONFIG = "selfBuildingConfigServiceConfig";
    public final static String CONF_CONFIG_VALIDATOR = "selfBuildingConfigServiceConfig";
    public final static String CONF_SERVICE_FACTORY = "selfBuildingCofigServiceFactory";
    public final static String CONF_SELF_BUILD_FACTORY = "selfBuildingConfigSelfBuildFactory";
    
    private static <T,V> DefaultConfiguration<String> buildBaseConfig(
            Configuration<T> serviceConfig, 
            Adapter<? extends Configuration<T>,V> serviceFactory,
            Adapter<Configuration<String>,V> selfBuildFactory){
        if(selfBuildFactory == null){
            throw new NullPointerException();
        }
        DefaultConfiguration<String> conf = new DefaultConfiguration<String>();
        conf.addProperty(Configuration.class, 
                CONF_SERVICE_CONFIG, serviceConfig);
        conf.addProperty(Adapter.class, 
                CONF_SERVICE_FACTORY, serviceFactory);
        conf.addProperty(Adapter.class, 
                CONF_SELF_BUILD_FACTORY, selfBuildFactory);
        return conf;
    }
    
    public static <T,V> Configuration<String> buildSelfBuildingConfig(
            Configuration<T> serviceConfig, 
            Adapter<Configuration<T>,V> serviceFactory,
            Adapter<Configuration<String>,V> selfBuildFactory){
        if(serviceConfig == null || serviceFactory == null){
            throw new NullPointerException();
        }
        return buildBaseConfig(
                serviceConfig, serviceFactory, selfBuildFactory);
    }
    
    public static <T,V,E,C extends ConfigValidator<T,E>> Configuration<String> 
            buildValidatingSelfBuildingConfig(
                    Configuration<T> serviceConfig, 
                    C validator,
                    Adapter<ValidatedConfig<T,C>,V> serviceFactory){
        if(serviceConfig == null 
                || validator == null || serviceFactory == null){
            throw new NullPointerException();
        }
        DefaultConfiguration<String> conf = new DefaultConfiguration<String>();
        conf.addProperty(Configuration.class, 
                CONF_SERVICE_CONFIG, serviceConfig);
        conf.addProperty(ConfigValidator.class, 
                CONF_CONFIG_VALIDATOR, validator);
        conf.addProperty(Adapter.class, 
                CONF_SERVICE_FACTORY, serviceFactory);
        conf.addProperty(Adapter.class, 
                CONF_SELF_BUILD_FACTORY, new ValidatingSelfBuilder<T, E>());
        return conf;
    }
    
    public static <T,V> Configuration<String> buildSelfBuildingConfig(
            Configuration<T> serviceConfig, 
            Adapter<Configuration<T>,V> serviceFactory){
        return buildSelfBuildingConfig(
                serviceConfig, serviceFactory, new DefaultSelfBuilder<V>());
    }
    
    public static <V> Configuration<String> buildEmptySelfBuildingConfig(
            V service){
        return buildBaseConfig(null, null, 
                new EmptySelfBuilder<V>(new DefaultSource<V>(service)));
    }
    
    public static <V> V selfBuild(Class<V> clazz, Configuration<String> config){
        if(config == null){
            throw new NullPointerException();
        }
        Adapter<Configuration<String>,V> selfBuildFactory = 
                config.getPropertyValue(Adapter.class, CONF_SELF_BUILD_FACTORY);
        if(selfBuildFactory == null){
            throw new NullPointerException();
        }
        return selfBuildFactory.adapt(config);
    }
    
    public static Object selfBuild(Configuration<String> config){
        if(config == null){
            throw new NullPointerException();
        }
        Adapter<Configuration<String>,?> selfBuildFactory = 
                config.getPropertyValue(Adapter.class, CONF_SELF_BUILD_FACTORY);
        if(selfBuildFactory == null){
            throw new NullPointerException();
        }
        return selfBuildFactory.adapt(config);
    }
    
    public static class DefaultSelfBuilder<V> implements 
            Adapter<Configuration<String>,V> {    
        
        private <T> V defaultInnerBuild(Configuration<String> config){
            Configuration<T> factConf = config.getPropertyValue(
                    Configuration.class, CONF_SERVICE_CONFIG);
            Adapter<Configuration<T>,V> factory = config.getPropertyValue(
                    Adapter.class, CONF_SERVICE_FACTORY);
            return factory.adapt(factConf);
        }
        
        @Override
        public V adapt(Configuration<String> a) {
            return defaultInnerBuild(a);
        }
    }
    
    public static class EmptySelfBuilder<V> implements 
            Adapter<Configuration<String>,V> {
        private Source<V> myValueSource;
        
        public EmptySelfBuilder(Source<V> valueSource){
            if(valueSource == null){
                throw new NullPointerException();
            }
            myValueSource = valueSource;
        }
        
        @Override
        public V adapt(Configuration<String> a) {
            return myValueSource.getValue();
        }
    }
    
    public static class ValidatingSelfBuilder<V,E> implements 
            Adapter<Configuration<String>,V> {    
        private List<E> myErrors;
        
        private V validatingInnerBuild(Configuration<String> config){
            Configuration uncheckedConf = config.getPropertyValue(
                    Configuration.class, CONF_SERVICE_CONFIG);
            ConfigValidator validator = config.getPropertyValue(ConfigValidator.class, CONF_CONFIG_VALIDATOR);
            Validator v = new Validator(validator);
            ValidatedConfig validConf = v.adapt(uncheckedConf);
            if(validConf == null){
                myErrors = v.getErrors();
                return null;
            }
            Adapter<ValidatedConfig,V> factory = config.getPropertyValue(
                    Adapter.class, CONF_SERVICE_FACTORY);
            
            return factory.adapt(validConf);
        }
        
        @Override
        public V adapt(Configuration<String> a) {
            return validatingInnerBuild(a);
        }
        
        public List<E> getErrors(){
            return myErrors;
        }
    }
    
    public static class DefaultSelfBuildingBuilder<V> implements 
            Adapter<Configuration<String>,V> {
        
        @Override
        public V adapt(Configuration<String> a) {
            return (V)selfBuild(a);
        }
    }
}
