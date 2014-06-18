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

package org.jflux.impl.services.rk.osgi;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.jflux.impl.services.rk.lifecycle.ManagedService;
import org.jflux.impl.services.rk.lifecycle.utils.SimpleLifecycle;
import org.jflux.impl.services.rk.osgi.lifecycle.OSGiComponent;

/**
 * Utility methods for working with OSGi.
 * @author Matthew Stevenson <www.robokind.org>
 */
public class OSGiUtils {
    private final static Logger theLogger = Logger.getLogger(OSGiUtils.class.getName());
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
        return String.format("(&%s)", filter);
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
    
    public static String createFilter(String key, String val){
        if(key == null || val == null){
            return null;
        }
        return String.format("(%s=%s)",key,val);
    }
    
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
     * Checks the OSGi Service Registry for a Service with the given class and
     * matching the given filter. Returns true if a matching ServiceReference is
     * found, return null if there is an InvalidSyntaxException.
     * @param context BundleContext to use
     * @param clazz Class name to match
     * @param filter OSGi Service filter String
     * @return true if a matching ServiceReference is found, returns null if 
     * there is an InvalidSyntaxException
     */
    public static Boolean serviceExists(
            BundleContext context, String clazz, String filter){
        try{
            ServiceReference[] refs = 
                    context.getAllServiceReferences(clazz, filter); 
            boolean exists = (refs != null);
            return exists;
        }catch(InvalidSyntaxException ex){
            theLogger.log(Level.WARNING, 
                    "Invalid OSGi filter syntax String: " + filter + ".", ex);
            return null;
        }
    }
    
    public static Boolean serviceExists(
            BundleContext context, String clazz, Properties props){
        String filter = createServiceFilter(props);
        return serviceExists(context, clazz, filter);
    }
    
    public static Boolean serviceExists(BundleContext context, String clazz, 
            String idPropertyName, String idString, Properties props){
        if(props == null){
            props = new Properties();
        }
        props.put(idPropertyName, idString);
        return serviceExists(context, clazz, props);
    }
    
    /**
     * Checks the OSGi Service Registry for a Service with the given class and
     * matching the given filter. Returns true if a matching ServiceReference is
     * found, return null if there is an InvalidSyntaxException.
     * @param context BundleContext to use
     * @param clazz Class to match
     * @param filter OSGi Service filter String
     * @return true if a matching ServiceReference is found, returns null if 
     * there is an InvalidSyntaxException
     */
    public static Boolean serviceExists(
            BundleContext context, Class clazz, String filter){
        return serviceExists(context, clazz.getName(), filter);
    }
    
    /**
     * Checks the OSGi Service Registry for an existing Service matching the
     * given filter String and one or more of the given Class names.
     * @param context BundleContext to use
     * @param classes one or more of the class names must be matched
     * @param filter OSGi Service filter String to match
     * @return true if a ServiceReference is found matching one or more of the
     * given classes and the filter String, return null if there is an 
     * InvalidSyntaxException
     */
    public static Boolean serviceExists(
            BundleContext context, String[] classes, String filter) {
        for(String c : classes){
            Boolean b = serviceExists(context, c, filter);
            if(b == null){//InvalidSyntaxException
                return null;
            }else if(!b){
                continue;
            }else if(b){
                theLogger.log(Level.INFO, 
                        "Found existing Service: {0}, matching {1}.", 
                        new Object[]{c, filter});
                return true;
            }
        }theLogger.log(Level.INFO, 
                        "No existing Service matching {0} with class {1}.", 
                        new Object[]{filter, Arrays.toString(classes)});
        return false;
    }
    
    public static <T> T getService(
            Class<T> clazz, BundleContext context, ServiceReference ref){
        if(context == null || ref == null){
            theLogger.warning("Cannot get service with null "
                    + "BundleContext or ServiceReference");
            return null;
        }
        Object obj = null;
        try{
            obj = context.getService(ref);
        }catch(Exception ex){
            theLogger.log(Level.FINEST, "Unable to get service.", ex);
        }
        if(obj == null){
            theLogger.log(Level.FINEST, 
                    "Service for reference ({0}) has a null Service.", 
                    getInformationString(ref));
            context.ungetService(ref);
            return null;
        }else if(!clazz.isAssignableFrom(obj.getClass())){   
            theLogger.log(Level.WARNING, 
                    "Service for reference ({0}) is not assignable to {1}.", 
                    new Object[]{getInformationString(ref),clazz.getName()});
            context.ungetService(ref);
            return null;
        }
        try{
            return (T)obj;
        }catch(Throwable t){
            theLogger.log(Level.WARNING, 
                    "There was an error casting the Service", t);
            context.ungetService(ref);
            return null;
        }
    }
    
    public static String getInformationString(ServiceReference ref){
        return "{ServiceReference: [" + 
                formatPropertiesString(ref) + ", " +
                formatUsingBundlesString(ref) + ", " +
                "{toString: " + ref.toString() + "}]}";
    }
    
    public static String formatPropertiesString(ServiceReference ref){
        String props = "";
        for(String s : ref.getPropertyKeys()){
            String prop = "{" + s + ": " + ref.getProperty(s).toString() + "}";
            props = props.isEmpty() ? prop : (props + ", " + prop);
        }
        return "{properties: " + props + "}";
    }
    
    public static String formatUsingBundlesString(ServiceReference ref){
        String bundles = "";
        try{
            for(Bundle b : ref.getUsingBundles()){
                String name = b.getSymbolicName();
                bundles = bundles.isEmpty() ? name : (bundles + ", " + name);
            }
        }catch(Exception ex){}
        return "{bundles: [" + bundles + "]}";
    }
    
    public static <T> SingleServiceListener<T> createIdServiceListener(
            Class<T> clazz, BundleContext context, String idPropertyName, 
            String idString, String filter){
        if(context == null || idPropertyName == null || idString == null){
            throw new NullPointerException();
        }
        filter = createIdFilter(idPropertyName, idString, filter);
        return new SingleServiceListener<T>(clazz, context, filter);
    }
    
    public static ServiceRegistration registerService(
            BundleContext context, String className, String idPropertyName, 
            String idString, Object service, Properties serviceProperties){
        if(context == null || className == null || 
                idPropertyName == null || idString == null || service == null){
            throw new NullPointerException();
        }
        Dictionary<String, Object> propTable = new Hashtable<String, Object>();
        if(serviceProperties != null){
            for(Object prop: serviceProperties.keySet()) {
                propTable.put(prop.toString(), serviceProperties.get(prop));
            }
        }
        propTable.put(idPropertyName, idString);
        ServiceRegistration reg = context.registerService(
                className, service, propTable);
        if(reg == null){
            theLogger.log(Level.WARNING, 
                    "Unknown error registering Service with Id: {0}={1}", 
                    new Object[]{idPropertyName,idString});
        }else{
            theLogger.log(Level.INFO, 
                    "Successfully registered Service with Id: {0}={1}", 
                    new Object[]{idPropertyName,idString});
        }
        return reg;
    }
    
    public static ManagedService startComponent(
            BundleContext context, String className, String idPropertyName, 
            String idString, Object service, Properties serviceProperties){
        return startComponent(context, new String[]{className}, 
                idPropertyName, idString, service, serviceProperties);      
    }
    public static ManagedService startComponent(
            BundleContext context, String[] classNames, String idPropertyName, 
            String idString, Object service, Properties serviceProperties){
        if(idPropertyName != null){
            if(serviceProperties == null){
                serviceProperties = new Properties();
            }
            serviceProperties.put(idPropertyName, idString);
        }
        return startComponent(context, classNames, service, serviceProperties);      
    }
    public static ManagedService startComponent(BundleContext context, 
            String[] classNames, Object service, Properties serviceProperties){
        if(context == null || service == null){
            throw new NullPointerException();
        }
        SimpleLifecycle lifecycle = 
                new SimpleLifecycle(service, classNames, null);
        OSGiComponent comp = 
                new OSGiComponent(context, lifecycle, serviceProperties);
        comp.start();
        return comp;        
    }
    
    /**
     * Registers the given service only if a service does not exist with the 
     * given id.  Returns the ServiceRegistration, or null if a service exists.
     * @param context
     * @param className
     * @param idPropertyName
     * @param idString
     * @param service
     * @param serviceProperties
     * @return the ServiceRegistration, or null if a service exists
     */
    public static ServiceRegistration registerUniqueService(
            BundleContext context, String className, String idPropertyName, 
            String idString, Object service, Properties serviceProperties){
        if(context == null || service == null){
            throw new NullPointerException();
        }
        if(serviceExists(context, className, 
                idPropertyName, idString, null)){
            theLogger.log(Level.WARNING, 
                    "Service already exists with Id: {0}={1}", 
                    new Object[]{idPropertyName,idString});
            return null;
        }
        return registerService(context, className, 
                idPropertyName, idString, service, serviceProperties);
    }
    
    public static ManagedService startUniqueComponent(
            BundleContext context, String className, String idPropertyName, 
            String idString, Object service, Properties serviceProperties){
        return startUniqueComponent(context, new String[]{className}, 
                idPropertyName, idString, service, serviceProperties);      
    }
    public static ManagedService startUniqueComponent(
            BundleContext context, String[] classNames, String idPropertyName, 
            String idString, Object service, Properties serviceProperties){
        Properties uniqueProps = new Properties();
        uniqueProps.put(idPropertyName, idString);
        return startUniqueComponent(
                context, classNames, service, uniqueProps, serviceProperties);      
    }
    public static ManagedService startUniqueComponent(BundleContext context, 
            String[] classNames, Object service, Properties uniqueProps, 
            Properties serviceProperties){
        if(context == null || service == null){
            throw new NullPointerException();
        }
        SimpleLifecycle lifecycle = 
                new SimpleLifecycle(service, classNames, null);
        OSGiComponent comp = new OSGiComponent(
                context, lifecycle, serviceProperties, uniqueProps, true);
        comp.start();
        return comp;        
    }
    
    public static void addServiceListener(
            BundleContext context, ServiceListener listener, 
            Class clazz, String serviceFilter) throws InvalidSyntaxException{
        String listenerFilter = "(" + Constants.OBJECTCLASS + "=" + 
                clazz.getName() + ")";
        boolean empty = (serviceFilter == null || serviceFilter.isEmpty());
        listenerFilter = empty ? listenerFilter : "(&" + listenerFilter + 
                serviceFilter + ")";
        theLogger.log(Level.INFO, 
                "Adding ServiceListener for: {0}", listenerFilter);
        context.addServiceListener(listener, listenerFilter);
    }
}
