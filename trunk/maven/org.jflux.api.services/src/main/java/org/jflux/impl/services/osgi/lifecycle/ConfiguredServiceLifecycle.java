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
package org.jflux.impl.services.osgi.lifecycle;

import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import org.jflux.api.services.extras.ConfigurationLoader;
import org.jflux.api.services.extras.Constants;
import org.jflux.api.services.extras.ServiceContext;
import org.jflux.api.services.extras.ServiceFactory;
import org.jflux.api.services.extras.VersionProperty;
import org.jflux.api.services.lifecycle.AbstractLifecycleProvider;
import org.jflux.api.services.lifecycle.DependencyDescriptor;
import org.jflux.api.services.lifecycle.utils.DescriptorBuilder;

/**
 *
 * @author Matthew Stevenson <www.robokind.org>
 */
public class ConfiguredServiceLifecycle<T, C, P> extends
        AbstractLifecycleProvider<T, T> {
    private final static Logger theLogger = 
            Logger.getLogger(ConfiguredServiceLifecycle.class.getName());
    
    private final static String theServiceFactory = "serviceFactory";
    private final static String theConfigLoader = "configLoader";
    private final static String theLoaderParam = "loaderParam";
    
    private Class<T> myServiceClass;
    /**
     * Parameter for loading a config from a ConfigurationLoader.
     * If this is set, it will be used in place of a dependency param.  However,
     * having this set and a param dependency will delay loading until the
     * dependency is available even if it is not used.
     */
    private P myParam;
    /**
     * Config for a ServiceFactory.  If this is set, it will be used rather than
     * loading a config from a ConfigurationLoader.  However, having this set 
     * and a ConfigLoader dependency will delay loading until the  dependency is 
     * available even if it is not used.
     */
    private C myConfig;
    
    /**
     * ServiceContext created from loading a service.  If a config is set rather
     * than loaded, this may be null.
     */
    protected ServiceContext<T,C,P> myServiceContext;
        
    /**
     * Creates a lifecycle best fitting the given params.
     * @param params 
     */
    public ConfiguredServiceLifecycle(
            ConfiguredServiceParams<T,C,P> params){
        this(params, null);
    }
    
    /**
     * Creates a lifecycle best fitting the given params.
     * @param params 
     * @param props registration properties
     */
    public ConfiguredServiceLifecycle(
            ConfiguredServiceParams<T,C,P> params, Properties props){
        super(new ArrayList());
        myServiceClass = params.getServiceClass();
        myConfig = params.getConfig();
        myParam = params.getParam();
        getDependencyDescriptors().add(buildFactoryDescriptor(
                myServiceClass, 
                params.getConfigClass(), 
                params.getServiceVersion()));
        if(params.getConfig() == null){
            getDependencyDescriptors().add(buildLoaderDescriptor(
                    params.getConfigClass(), 
                    params.getParamClass(), 
                    params.getConfigFormat()));
        }
        if(params.getParam() == null && myConfig == null){
            getDependencyDescriptors().add(buildParamDescriptor(
                    params.getParamClass(), 
                    params.getConfigFormat(),
                    params.getParamId()));
        }
        if(myRegistrationProperties == null){
            myRegistrationProperties = new Properties();
        }
        myRegistrationProperties.put(
                Constants.SERVICE_VERSION, 
                params.getServiceVersion().toString());
        if(props != null && !props.isEmpty()){
            myRegistrationProperties.putAll(props);
        }
    }
    
    private DependencyDescriptor buildFactoryDescriptor(Class<T> serviceClass, 
            Class<C> configClass, VersionProperty serviceVersion){
        DescriptorBuilder descs = 
                new DescriptorBuilder(theServiceFactory, ServiceFactory.class)
                .with(Constants.SERVICE_CLASS, serviceClass.getName())
                .with(Constants.SERVICE_VERSION, serviceVersion.toString());
        if(configClass != null){
            descs.with(Constants.CONFIG_CLASS, configClass.getName());
        }
        return descs.getDescriptor();
    }
    
    private DependencyDescriptor buildLoaderDescriptor(Class<C> configClass, 
            Class<P> paramClass, VersionProperty configFormat){
        DescriptorBuilder descs = new DescriptorBuilder(
                theConfigLoader, ConfigurationLoader.class)
                .with(Constants.CONFIG_FORMAT_VERSION, configFormat.toString());
        if(configClass != null){
            descs.with(Constants.CONFIG_CLASS, configClass.getName());
        }if(paramClass != null){
            descs.with(Constants.CONFIG_PARAM_CLASS, paramClass.getName());
        }
        return descs.getDescriptor();
    }
    
    private DependencyDescriptor buildParamDescriptor(
            Class<P> paramClass, VersionProperty configFormat, String paramId){
        DescriptorBuilder descs = 
                new DescriptorBuilder(theLoaderParam, paramClass)
                .with(Constants.CONFIG_FORMAT_VERSION, configFormat.toString());
        if(paramId != null){
            descs.with(Constants.CONFIG_PARAM_ID, paramId);
        }
        return descs.getDescriptor();
    }
    
    @Override
    public T getService(){
        return myService;
    }
    
    @Override
    protected T create(Map<String, Object> dependencies) {
        ServiceFactory<T,C> fact = 
                (ServiceFactory<T, C>)dependencies.get(theServiceFactory);
        ServiceContext<T,C,P> serviceContext = new ServiceContext();
        serviceContext.setServiceFactory(fact);
        P param = myParam;
        C config = myConfig;
        if(myConfig == null){
            ConfigurationLoader<C,P> loader = 
                    (ConfigurationLoader<C,P>)dependencies.get(theConfigLoader);
            if(myParam == null){
                param = (P)dependencies.get(theLoaderParam);
            }
            serviceContext.setServiceConfigurationLoader(loader);
            serviceContext.setLoadParameter(param);
            if(!serviceContext.loadConfiguration()){
                theLogger.warning("Failed to load config.");
                return null;
            }
        }else{
            serviceContext.setServiceConfiguration(config);
        }
        if(!serviceContext.buildService()){
            theLogger.warning("Failed to build service.");
            return null;
        }
        myServiceContext = serviceContext;
        return myServiceContext.getService();
    }

    @Override
    public synchronized void stop() {
        cleanupService(myService);
        super.stop();
    }

    @Override
    protected void handleChange(String name, Object dependency, 
            Map<String, Object> availableDependencies) {
        if(myService == null){
            return;
        }
        if(theLoaderParam.equals(name)){
            cleanupService(myService);
            myService = null;
            if(isSatisfied()){
                myService = create(availableDependencies);
            }
            return;
        }
    }
    
    protected void cleanupService(T service){
        
    }

    @Override
    public Class<T> getServiceClass() {
        return myServiceClass;
    }
}
