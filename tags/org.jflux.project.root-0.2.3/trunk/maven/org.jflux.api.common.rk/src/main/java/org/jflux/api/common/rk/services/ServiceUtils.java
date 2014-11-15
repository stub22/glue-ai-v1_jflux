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
package org.jflux.api.common.rk.services;

import java.util.Properties;
import java.util.logging.Logger;
import org.jflux.impl.services.rk.lifecycle.ManagedService;
import org.jflux.impl.services.rk.lifecycle.utils.SimpleLifecycle;
import org.jflux.impl.services.rk.osgi.OSGiUtils;
import org.jflux.impl.services.rk.osgi.SingleServiceListener;
import org.jflux.impl.services.rk.osgi.lifecycle.OSGiComponent;
import org.osgi.framework.BundleContext;
import org.jflux.api.common.rk.config.VersionProperty;

/**
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public class ServiceUtils {
    private final static Logger theLogger = 
            Logger.getLogger(ServiceUtils.class.getName());
    
    /**
     * Registers the given ServiceFactory with the factory's Service Version, 
     * Service class, and Config class.     * 
     * @param context BundleContext for registering the factory
     * @param fact ServiceFactory to register
     * @return ServiceRegistration from registering the factory
     */
    public static ManagedService registerFactory(
            BundleContext context, ServiceFactory fact){
        return registerFactory(context, fact, null);
    }
    
    public static ManagedService registerFactory(
            BundleContext context, ServiceFactory fact, Properties props){
        if(context == null || fact == null){
            throw new NullPointerException();
        }
        if(props == null){
            props = new Properties();
        }
        props.put(Constants.SERVICE_VERSION, 
                fact.getServiceVersion().toString());
        props.put(Constants.SERVICE_CLASS, 
                fact.getServiceClass().getName());
        props.put(Constants.CONFIG_CLASS, 
                fact.getConfigurationClass().getName());
        ManagedService ms = new OSGiComponent(context, 
                new SimpleLifecycle(fact, ServiceFactory.class), props);
        ms.start();
        return ms;
    }
    
    public static ManagedService registerConfigLoader(
            BundleContext context, ConfigurationLoader loader){
        return registerConfigLoader(context, loader, null);
    }
    
    public static ManagedService registerConfigLoader(
            BundleContext context, ConfigurationLoader loader, 
            Properties props){
        if(context == null || loader == null){
            throw new NullPointerException();
        }
        if(props == null){
            props = new Properties();
        }
        props.put(Constants.CONFIG_FORMAT_VERSION, 
                loader.getConfigurationFormat().toString());
        props.put(Constants.CONFIG_CLASS, 
                loader.getConfigurationClass().getName());
        props.put(Constants.CONFIG_PARAM_CLASS, 
                loader.getParameterClass().getName());
        ManagedService ms = new OSGiComponent(context, 
                new SimpleLifecycle(loader, ConfigurationLoader.class), props);
        ms.start();
        return ms;
    }
    
    public static ManagedService registerConfigWriter(
            BundleContext context, ConfigurationWriter writer){
        return registerConfigWriter(context, writer, null);
    }
    
    public static ManagedService registerConfigWriter(
            BundleContext context, ConfigurationWriter writer, 
            Properties props){
        if(context == null || writer == null){
            throw new NullPointerException();
        }
        if(props == null){
            props = new Properties();
        }
        props.put(Constants.CONFIG_FORMAT_VERSION, 
                writer.getConfigurationFormat().toString());
        props.put(Constants.CONFIG_CLASS, 
                writer.getConfigurationClass().getName());
        props.put(Constants.CONFIG_PARAM_CLASS, 
                writer.getParameterClass().getName());
        ManagedService ms = new OSGiComponent(context, 
                new SimpleLifecycle(writer, ConfigurationWriter.class), props);
        ms.start();
        return ms;
    }
    
    public static <Conf,Param> 
            SingleServiceListener<ConfigurationWriter<Conf,Param>> 
            createWriterServiceListener(BundleContext context, 
                    Class<Conf> configClass,
                    Class<Param> paramClass,
                    VersionProperty configFormat,
                    String serviceFilter){
        Properties props = new Properties();
        props.put(Constants.CONFIG_FORMAT_VERSION, 
                configFormat.toString());
        props.put(Constants.CONFIG_CLASS, 
                configClass.getName());
        props.put(Constants.CONFIG_PARAM_CLASS, 
                paramClass.getName());
        String writerFilter = OSGiUtils.createServiceFilter(props);
        if(serviceFilter != null && !serviceFilter.isEmpty()){
            writerFilter = "(&"+writerFilter+"("+serviceFilter+"))";
        }
        return new SingleServiceListener(
                ConfigurationWriter.class, context, serviceFilter);
    }
}
