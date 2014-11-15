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
package org.jflux.api.common.rk.services.addon;

import java.io.File;
import org.jflux.impl.services.rk.osgi.SingleServiceListener;
import org.osgi.framework.BundleContext;
import org.jflux.api.common.rk.config.VersionProperty;
import org.jflux.api.common.rk.services.ConfigurationAdapter;
import org.jflux.api.common.rk.services.ConfigurationWriter;
import org.jflux.api.common.rk.services.ServiceConnectionDirectory;
import org.jflux.api.common.rk.services.ServiceContext;
import org.jflux.api.common.rk.services.ServiceUtils;

/**
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public class DefaultAddOnDriver<AddOn, Config> 
        implements ServiceAddOnDriver<AddOn> {
    private BundleContext myContext;
    private VersionProperty myServiceVersion;
    private VersionProperty myConfigFormat;
    private Class<AddOn> myAddOnClass;
    private ConfigurationAdapter<AddOn, Config> myConfigAdapter;
    private SingleServiceListener<
                ConfigurationWriter<Config,File>> myWriterTracker;
    
    public DefaultAddOnDriver(BundleContext context, 
            VersionProperty serviceVersion, 
            VersionProperty configFormat, 
            ConfigurationAdapter<AddOn, Config> configAdapter){
        if(context == null || serviceVersion == null || 
                configFormat == null || configAdapter == null){
            throw new NullPointerException();
        }
        myContext = context;
        myServiceVersion = serviceVersion;
        myConfigFormat = configFormat;
        myConfigAdapter = configAdapter;
        myAddOnClass = myConfigAdapter.getSerivceClass();
        myWriterTracker = ServiceUtils.createWriterServiceListener(context, 
                        myConfigAdapter.getConfigurationClass(), 
                        File.class, configFormat, null);
        myWriterTracker.start();
    }
    
    @Override
    public VersionProperty getServiceVersion() {
        return myServiceVersion;
    }

    @Override
    public VersionProperty getConfigurationFormat() {
        return myConfigFormat;
    }

    @Override
    public Class<AddOn> getServiceClass() {
        return myAddOnClass;
    }

    @Override
    public ServiceAddOn<AddOn> loadAddOn(File file) throws Exception {
        if(file == null){
            throw new NullPointerException();
        }
        ServiceContext<AddOn,?,File> sc = ServiceConnectionDirectory.buildServiceContext(
                myContext, myServiceVersion, myConfigFormat, 
                myAddOnClass, File.class);
        if(sc == null){
            return null;
        }
        sc.setLoadParameter(file);
        if(!sc.buildService()){
            return null;
        }
        AddOn addon = sc.getService();
        if(addon == null){
            return null;
        }
        return new ServiceAddOn.DefaultAddOn<AddOn>(addon, this);        
    }

    @Override
    public boolean writeServiceConfig(AddOn addon, File file) throws Exception {
        Config config = myConfigAdapter.createConfig(addon);
        ConfigurationWriter<Config,File> writer = 
                myWriterTracker.getService();
        if(writer == null){
            return false;
        }
        return writer.writeConfiguration(config, file);
    }
    
    
    
}
