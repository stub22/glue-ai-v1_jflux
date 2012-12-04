/*
 * Copyright 2011 Hanson Robokind LLC.
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
package org.jflux.impl.services.osgi;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jflux.api.registry.Accessor;
import org.jflux.api.registry.Finder;
import org.jflux.api.registry.opt.BasicRegistrationRequest;
import org.jflux.api.registry.opt.Certificate;
import org.jflux.api.registry.opt.Descriptor;
import org.jflux.api.registry.opt.Reference;
import org.jflux.api.registry.opt.RegistrationRequest;
import org.jflux.api.registry.opt.RegistryContext;
import org.jflux.api.services.DependencyDescriptor;
import org.jflux.impl.registry.osgi.wrapped.OSGiContext.BundleContextWrapper;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * Utility methods for working with OSGi.
 *
 * @author Matthew Stevenson <www.robokind.org>
 */
public class OSGiUtils {
    private final static Logger theLogger = Logger.getLogger(OSGiUtils.class.getName());

    /**
     * Create an OSGi filter string for the given property.
     * @param key The property's name.
     * @param val The property's value.
     * @return an OSGi filter string
     */
    public static String createFilter(String key, String val) {
        if(key == null || val == null) {
            return null;
        }
        return String.format("(%s=%s)", key, val);
    }
    
    /**
     * Returns the BundleContext associated with the given Class.
     * @param clazz Class associated with a Bundle
     * @return the BundleContext associated with the given Class, if no Bundle
     * is found for the Class null is returned
     */
    public static BundleContext getBundleContext(Class clazz){
        Bundle bundle = FrameworkUtil.getBundle(clazz);
        if(bundle == null){
            theLogger.log(Level.WARNING, 
                    "Unable to find Bundle for class {0}", 
                    clazz.getName());
            return null;
        }
        try{
            BundleContext context = bundle.getBundleContext();
            if(context == null){
                theLogger.log(Level.WARNING, 
                        "Found Bundle for class {0}, "
                        + "but no valid BundleContext.", clazz.getName());
            }
            return context;
        }catch(SecurityException ex){
            //Not actually catching, ensuring the SecurityException is logged.
            theLogger.log(Level.SEVERE, 
                    "Permission Denied to BundleContext for class " + 
                    clazz.getName() + 
                    ".  Caller lacks appropriate AdminPermissions.", ex);
            throw ex;
        }
    }
    
    /**
     * Get a class's registry context.
     * @param clazz
     * @return a registry context
     */
    public static RegistryContext getRegistryContext(Class clazz){
        BundleContext context = getBundleContext(clazz);
        if(context == null){
            return null;
        }
        RegistryContext rc = new BundleContextWrapper().adapt(context);
        return rc;
    }
    
    /**
     * Convert an OSGi bundle context into a non-OSGi-specific generic registry
     * context.
     * @param context
     * @return the input in RegistryContext form
     */
    public static RegistryContext getRegistryContext(BundleContext context){
        if(context == null){
            return null;
        }
        RegistryContext rc = new BundleContextWrapper().adapt(context);
        return rc;
    }
    
    /**
     * Creates a filter string which matches all of the properties given.
     * @param props Properties to match
     * @return OSGi filter string, null if props is null or empty
     */
    public static String createServiceFilter(Properties props){
        if(props == null || props.isEmpty()){
            return null;
        }
        String filter = "";
        for(Entry e : props.entrySet()){
            String key = (String)e.getKey();
            String val = (String)e.getValue();
            filter = String.format("%s(%s=%s)", filter,key,val);
        }
        if(props.size() > 1) {
            return String.format("(&%s)", filter);
        } else {
            return filter;
        }
    }
    
    /**
     * Creates a filter string which matches the key and value given
     * @param key key
     * @param val value
     * @return OSGi filter string, null if props is null or empty
     */
    public static String createServiceFilter(String key, String val){
        if(key == null || val == null){
            return null;
        }
        return String.format("(%s=%s)",key,val);
    }
    
    /**
     * Creates an OSGi ID filter string from a property and an OSGi filter
     * string.
     * @param idPropertyName the name of the property
     * @param idString the value of the property
     * @param filter the base filter string
     * @return the ID filter string
     */
    public static String createIdFilter(
            String idPropertyName, String idString, String filter){
        String idFilter =
                OSGiUtils.createServiceFilter(idPropertyName, idString);
        if(filter == null || filter.isEmpty()){
            filter = idFilter;
        }else{
            filter = "(&" + idFilter + filter + ")";
        }
        return filter;
    }
    
    /**
     * Determines if a set of unique properties is already in use.
     * @param context the registry context to check for properties
     * @param uniqueProperties the unique properties
     * @param registrationClassNames the classes associated with the properties
     * @return true if the unique properties are not in use
     */
    public static boolean uniquePropertiesAvailable(
            RegistryContext context, Properties uniqueProperties,
            String[] registrationClassNames) {
        if(uniqueProperties == null) {
            return true;
        }
        for(Entry e : uniqueProperties.entrySet()) {
            String key = e.getKey().toString();
            String val = e.getValue().toString();
            // iterate through myRegistrationClassNames
            // grab the class for each class name
            // create a DependencyDescriptor
            // check that key and that value, create a new map
            // do the finder for that, see if it's null
            // if not null, return false
            for(String clsName : registrationClassNames) {
                if(scanClassForProps(context, clsName, key, val)) {
                    theLogger.log(Level.SEVERE,
                            "Unique service property already in use: ({}={}).",
                            new Object[]{key, val});
                    return false;
                }
            }
        }
        return true;
    }
    
    private static boolean scanClassForProps(
            RegistryContext context, String clsName, String key, String val) {
        try {
            Class cls = Class.forName(clsName);
            Map<String, String> props = new HashMap();
            Finder f = context.getRegistry().getFinder(context);
            props.put(key, val);
            Descriptor d = new DependencyDescriptor("", cls, props);
            Object result = f.findSingle().adapt(d);
            if(result != null) {
                return true;
            }
        } catch(Exception ex) {
            theLogger.log(Level.SEVERE, ex.getMessage());
        }
        return false;
    }
    
    /**
     * Registers the given service only if a service does not exist with the 
     * given id.
     * @param context the registry context for the service
     * @param cls the service class name
     * @param uniqueProperties the service's unique properties
     * @param service the service to register
     * @param props the service's non-unique properties
     * @return the Certificate, or null if a service exists
     */
    public static Certificate registerUniqueService(
            RegistryContext context, String cls,
            Properties uniqueProperties, Object service,
            Map<String, String> props) {
        String[] classNames = new String[1];
        classNames[0] = cls;
        
        if(!uniquePropertiesAvailable(context, uniqueProperties, classNames)) {
            return null;
        }
        
        RegistrationRequest<Object, String, String> rr =
                    new BasicRegistrationRequest<Object, String, String>(
                    "", service, props, cls);
        Accessor acc = context.getRegistry().getAccessor(context);
        
        return (Certificate)acc.register().adapt(rr);
    }
    
    /**
     * Registers the given service only if a service does not exist with the 
     * given id.
     * @param context the registry context for the service
     * @param cls the service class name
     * @param uniqueProperties the service's unique properties
     * @param service the service to register
     * @param props the service's non-unique properties
     * @return the Certificate, or null if a service exists
     */
    public static Certificate registerUniqueService(
            RegistryContext context, String cls,
            Properties uniqueProperties, Object service,
            Properties props) {
        String[] classNames = new String[1];
        Map<String, String> propMap = new HashMap<String, String>();
        classNames[0] = cls;
        
        if(props != null) {
            for(Entry e: props.entrySet()) {
                propMap.put(e.getKey().toString(), e.getValue().toString());
            }
        }
        
        return registerUniqueService(
                context, cls, uniqueProperties, service, propMap);
    }
    
    /**
     * Checks the OSGi Service Registry for a Service with the given class and
     * matching the given properties.
     * @param context RegistryContext to use
     * @param clazz Class name to match
     * @param idPropertyName name of an additional property to use
     * @param idString value of an additional property to use
     * @param props the properties to use
     * @return true if a matching ServiceReference is found, returns null if 
     * there is an InvalidSyntaxException
     */

    public static boolean serviceExists(
            RegistryContext context, String clazz, String idPropertyName,
            String idString, Properties props) {
        Finder fin = context.getRegistry().getFinder(context);
        
        if(props == null) {
            props = new Properties();
        }
        
        props.put(idPropertyName, idString);
        
        return serviceExists(context, clazz, props);
    }
    
    /**
     * Checks the OSGi Service Registry for a Service with the given class and
     * matching the given filter.
     * @param context RegistryContext to use
     * @param clazz Class name to match
     * @param props the properties to use
     * @return true if a matching ServiceReference is found, returns null if 
     * there is an InvalidSyntaxException
     */

    public static boolean serviceExists(
            RegistryContext context, String clazz, Properties props) {
        Finder fin = context.getRegistry().getFinder(context);
        Map<String, String> propMap = new HashMap<String, String>();
        
        if(props != null) {
            for(Entry e: props.entrySet()) {
                propMap.put(e.getKey().toString(), e.getValue().toString());
            }
        }
        
        try {
            Descriptor d = new DependencyDescriptor(
                    "", Class.forName(clazz), propMap);
            Reference r = (Reference)fin.findSingle().adapt(d);
            
            return r != null;
        } catch(Exception e) {
            theLogger.log(Level.SEVERE, e.getMessage());
            return false;
        }
    }
}
