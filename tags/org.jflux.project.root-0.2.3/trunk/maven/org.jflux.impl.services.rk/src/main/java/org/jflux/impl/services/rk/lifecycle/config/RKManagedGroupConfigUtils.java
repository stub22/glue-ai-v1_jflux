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
import java.util.Properties;
import org.jflux.api.core.Adapter;
import org.jflux.api.core.config.Configuration;
import org.jflux.api.core.config.DefaultConfiguration;
import org.jflux.api.core.util.BatchAdapter;
import org.jflux.impl.services.rk.lifecycle.config.SelfBuildingConfig.DefaultSelfBuildingBuilder;
import org.jflux.impl.services.rk.lifecycle.ServiceLifecycleProvider;
import org.jflux.impl.services.rk.lifecycle.utils.ManagedServiceFactory;
import org.jflux.impl.services.rk.lifecycle.utils.ManagedServiceGroup;
import org.jflux.impl.services.rk.lifecycle.utils.SimpleLifecycle;

/**
 *
 * @author Matthew Stevenson
 */
public class RKManagedGroupConfigUtils {
    public final static String CONFIG_GROUP_ID = "managedServiceGroupGroupId";
    public final static String CONFIG_GROUP_PROPERTIES = "managedServiceGroupRegistrationProperties";
    public final static String CONF_SELFBUILD_LIFCEYCLES = "managedServiceGroupSelfBuildingLifecycles";
    
    public final static String CONF_GENERIC_SELF_BUILDER = "managedServiceGroupGenericSelfBuildingBuilder";
    public final static String CONF_MANAGED_SERVICE_FACTORY = "managedServiceGroupManagedServiceFactory";
        
    private static DefaultConfiguration<String> buildBaseConfig(
            String groupId, Properties props,
            List<Configuration<String>> selfBuildLifecycles,
            Adapter<Configuration<String>,ServiceLifecycleProvider> genericSelfBuilder){
        DefaultConfiguration<String> conf = new DefaultConfiguration<String>();
        if(groupId == null){
            throw new NullPointerException();
        }
        if(selfBuildLifecycles == null || selfBuildLifecycles.isEmpty()){
            throw new IllegalArgumentException(
                    "Must specify at least one lifecycle");
        }
        conf.addProperty(String.class, CONFIG_GROUP_ID, groupId);
        conf.addProperty(Properties.class, CONFIG_GROUP_PROPERTIES, props);
        conf.addProperty(List.class, CONF_SELFBUILD_LIFCEYCLES, selfBuildLifecycles);
        conf.addProperty(Adapter.class, CONF_GENERIC_SELF_BUILDER, genericSelfBuilder);
        
        return conf;
    }
    
    public static Configuration<String> buildManagedGroupConfig(
            String groupId, Properties props,
            List<Configuration<String>> selfBuildLifecycles,
            Adapter<Configuration<String>,ServiceLifecycleProvider> genericSelfBuilder){
        if(genericSelfBuilder == null){
            throw new NullPointerException();
        }
        return buildBaseConfig(
                groupId, props, selfBuildLifecycles, genericSelfBuilder);
    }
    
    public static Configuration<String> buildManagedGroupConfig(
            String groupId, Properties props,
            List<Configuration<String>> selfBuildLifecycles){
        return buildManagedGroupConfig(groupId, props, selfBuildLifecycles, 
                new DefaultSelfBuildingBuilder());
    }
    
    public static Configuration<String> makeSelfBuildingLifecycle(
            Configuration<String> lifecycleConfig){
        return SelfBuildingConfig.buildSelfBuildingConfig(lifecycleConfig, 
                new RKLifecycleConfigUtils.GenericLifecycleFactory());
    }
    
    public static <T> Configuration<String> makeSelfBuildingLifecycle(
            ServiceLifecycleProvider<T> lifecycle){
        return SelfBuildingConfig.buildEmptySelfBuildingConfig(lifecycle);
    }
    
    public static ManagedServiceGroup buildGroup(
            ManagedServiceFactory factory, Configuration<String> conf){
        String groupId = conf.getPropertyValue(
                String.class, CONFIG_GROUP_ID);
        Properties props = conf.getPropertyValue(
                Properties.class, CONFIG_GROUP_PROPERTIES);
        List<Configuration<String>> lifecycleConfigs = conf.getPropertyValue(
                List.class, CONF_SELFBUILD_LIFCEYCLES);
        Adapter<Configuration<String>,ServiceLifecycleProvider> selfBuilder = 
                conf.getPropertyValue(Adapter.class, CONF_GENERIC_SELF_BUILDER);
        List<ServiceLifecycleProvider> lifecycles = 
                new BatchAdapter(selfBuilder).adapt(lifecycleConfigs);
        return new ManagedServiceGroup(factory, lifecycles, groupId, props);
    }
    
    public static class ManagedGroupFactory implements 
            Adapter<Configuration<String>,ManagedServiceGroup> {
        private ManagedServiceFactory myFactory;
        private boolean myStartFlag;
        
        public ManagedGroupFactory(
                ManagedServiceFactory fact, boolean startGroups){
            if(fact == null){
                throw new NullPointerException();
            }
            myFactory = fact;
            myStartFlag = startGroups;
        }
        
        public ManagedGroupFactory(ManagedServiceFactory fact){
            this(fact, true);
        }
        
        @Override
        public ManagedServiceGroup adapt(Configuration<String> a) {
            ManagedServiceGroup g = buildGroup(myFactory, a);
            if(myStartFlag){
                g.start();
            }
            return g;
        }
    }
}
