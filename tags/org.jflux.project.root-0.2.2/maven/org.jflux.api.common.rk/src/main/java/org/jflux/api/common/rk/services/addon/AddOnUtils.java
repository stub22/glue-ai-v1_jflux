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
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jflux.impl.services.rk.osgi.OSGiUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.jflux.api.common.rk.config.VersionProperty;
import org.jflux.api.common.rk.services.Constants;

/**
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public class AddOnUtils {
    private final static Logger theLogger = 
            Logger.getLogger(AddOnUtils.class.getName());
    
    public static ServiceRegistration registerAddOnDriver(
            BundleContext context, ServiceAddOnDriver driver, 
            Properties props){
        Dictionary<String, Object> propTable = new Hashtable<String, Object>();
        if(context == null || driver == null){
            throw new NullPointerException();
        }
        
        if(props != null){
            for(Object prop: props.keySet()) {
                propTable.put(prop.toString(), props.get(prop));
            }
        }
        propTable.put(Constants.SERVICE_VERSION, 
                driver.getServiceVersion().toString());
        propTable.put(Constants.CONFIG_FORMAT_VERSION, 
                driver.getConfigurationFormat().toString());
        ServiceRegistration reg = context.registerService(
                ServiceAddOnDriver.class.getName(), driver, propTable);
        if(reg == null){
            theLogger.log(Level.WARNING, 
                    "Unknown error occured while registering "
                    + "ServiceConfigurationWriter.");
        }else{
            theLogger.log(Level.INFO, 
                    "ServiceAddOnDriver registered successfully.");
        }
        return reg;
    }
    
    public static ServiceReference[] getAddOnDriverReferences(
            BundleContext context,
            VersionProperty serviceVersion, 
            VersionProperty configFormat){
        if(context == null || serviceVersion == null || configFormat == null){
            throw new NullPointerException();
        }
        Properties props = new Properties();
        props.put(Constants.SERVICE_VERSION, 
                serviceVersion.toString());
        props.put(Constants.CONFIG_FORMAT_VERSION, 
                configFormat.toString());
        String filter = OSGiUtils.createServiceFilter(props);
        ServiceReference[] refs;
        try{
            refs = context.getServiceReferences(
                ServiceAddOnDriver.class.getName(), filter);
        }catch(InvalidSyntaxException ex){
            theLogger.log(Level.SEVERE, 
                    "There was an error fetching service references.", ex);
            return null;
        }
        if(refs == null || refs.length == 0){
            theLogger.log(Level.WARNING, 
                    "Unable to find AddOnDriver with "
                    + "serviceVersion={0} and configFormat={1}", 
                    new Object[]{
                        serviceVersion.display(), configFormat.display()});
            return null;
        }
        return refs;
    }
    
    public static ServiceReference[] getAddOnDriverReferences(
            BundleContext context){
        if(context == null){
            throw new NullPointerException();
        }
        ServiceReference[] refs;
        try{
            refs = context.getServiceReferences(
                ServiceAddOnDriver.class.getName(), null);
        }catch(InvalidSyntaxException ex){
            theLogger.log(Level.SEVERE, 
                    "There was an error fetching service references.", ex);
            return null;
        }
        if(refs == null || refs.length == 0){
            theLogger.log(Level.WARNING, "Unable to find AddOnDrivers");
            return null;
        }
        return refs;
    }
    
    public static List<ServiceAddOnDriver> getAddOnDriverList(
            BundleContext context, ServiceReference[] refs){
        if(context == null || refs == null){
            throw new NullPointerException();
        }
        List<ServiceAddOnDriver> drivers = new ArrayList<ServiceAddOnDriver>();
        for(ServiceReference ref : refs){
            ServiceAddOnDriver driver = OSGiUtils.getService(
                    ServiceAddOnDriver.class, context, ref);
            if(driver == null){
                continue;
            }
            drivers.add(driver);
        }
        return drivers;
    }
    
    public static void releaseAddOnDrivers(
            BundleContext context, ServiceReference[] refs){
        if(context == null || refs == null){
            throw new NullPointerException();
        }
        for(ServiceReference ref : refs){
            context.ungetService(ref);
        }
    }
    
    public static boolean saveAddOnConfig(
            ServiceAddOn addon, String addonPath) throws Exception{
        File addonFile = new File(addonPath);
        ServiceAddOnDriver driver = addon.getAddOnDriver();
        return driver.writeServiceConfig(addon.getAddOn(), addonFile);
    }
}
