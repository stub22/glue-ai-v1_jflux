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

import java.util.Properties;
import org.jflux.api.core.Adapter;
import org.jflux.api.core.config.Configuration;
import org.jflux.api.core.config.DefaultConfiguration;
import org.jflux.impl.services.rk.lifecycle.ManagedService;
import org.jflux.impl.services.rk.lifecycle.ServiceLifecycleProvider;
import org.jflux.impl.services.rk.lifecycle.utils.ManagedServiceFactory;

/**
 *
 * @author Matthew Stevenson
 */
public class RKManagedServiceConfigUtils {
    public final static String CONF_SERVICE_PROPERTIES = "managedServiceRegistrationProperties";
    public final static String CONF_UNIQUE_SERVICE_PROPERTIES = "managedServiceUniqueRegistrationProperties";
    
    public final static String CONF_SELFBUILD_LIFCEYCLE_CONFIG = "managedServiceSelfBuildingLifecycleConfig";
    public final static String CONF_SERVICE_LIFCEYCLE = "managedServiceLifecycle";
    
    private static DefaultConfiguration<String> buildBaseConfig(
            Properties props, Properties uniqueProps){
        DefaultConfiguration<String> conf = new DefaultConfiguration<String>();
        conf.addProperty(Properties.class, CONF_SERVICE_PROPERTIES, props);
        conf.addProperty(Properties.class, 
                CONF_UNIQUE_SERVICE_PROPERTIES, uniqueProps);
        return conf;
    }
    
    public static Configuration<String> buildManagedServiceConfig(
            Properties props, Properties uniqueProps, 
            Configuration<String> selfBuildLifecycleConf){
        DefaultConfiguration<String> conf = buildBaseConfig(props, uniqueProps);
        conf.addProperty(Configuration.class, 
                CONF_SELFBUILD_LIFCEYCLE_CONFIG, selfBuildLifecycleConf);
        return conf;
    }
    
    public static Configuration<String> buildManagedServiceConfig(
            Properties props, Properties uniqueProps, 
            ServiceLifecycleProvider lifecycle){
        DefaultConfiguration<String> conf = buildBaseConfig(props, uniqueProps);
        conf.addProperty(ServiceLifecycleProvider.class, 
                CONF_SERVICE_LIFCEYCLE, lifecycle);
        return conf;
    }
    
    public static class GenericManagedServiceFactory<T> implements
            Adapter<Configuration<String>, ManagedService<T>> {
        ManagedServiceFactory myFactory;
        
        public GenericManagedServiceFactory(ManagedServiceFactory factory){
            myFactory = factory;
        }
        
        @Override
        public ManagedService<T> adapt(Configuration<String> a) {
            return buildManagedService(myFactory, a);
        }
        
        private static <T> ManagedService<T> buildManagedService(
                ManagedServiceFactory factory,
                Configuration<String> config){
            ServiceLifecycleProvider<T> lifecycle = getLifecycle(config);
            if(lifecycle == null){
                throw new NullPointerException();
            }
            Properties props = config.getPropertyValue(
                    Properties.class, CONF_SERVICE_PROPERTIES); 
            Properties uniqueProps = config.getPropertyValue(
                    Properties.class, CONF_UNIQUE_SERVICE_PROPERTIES);
            return factory.createService(lifecycle, props, uniqueProps);
        }

        private static <T> ServiceLifecycleProvider<T> getLifecycle(
                Configuration<String> config){
            ServiceLifecycleProvider<T> lifecycle = config.getPropertyValue(
                    ServiceLifecycleProvider.class, CONF_SERVICE_LIFCEYCLE);
            if(lifecycle != null){
                return lifecycle;
            }
            Configuration<String> lifecycleConfig = config.getPropertyValue(
                    Configuration.class, CONF_SELFBUILD_LIFCEYCLE_CONFIG);
            if(lifecycleConfig == null){
                throw new NullPointerException();
            }
            return (ServiceLifecycleProvider)
                    SelfBuildingConfig.selfBuild(lifecycleConfig);
        }
    }
}
