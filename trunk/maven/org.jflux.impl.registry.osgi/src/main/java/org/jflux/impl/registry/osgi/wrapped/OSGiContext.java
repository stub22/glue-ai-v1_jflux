/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jflux.impl.registry.osgi.wrapped;

import java.io.File;
import java.io.InputStream;
import java.util.Dictionary;
import org.jflux.api.core.Adapter;
import org.jflux.api.core.util.DefaultTimestampSource;
import org.jflux.api.registry.Registry;
import org.jflux.api.registry.opt.RegistryContext;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * RegistryContext implementation for OSGi.
 * @author Matthew Stevenson
 */
public class OSGiContext<R extends Registry<? extends OSGiContext<R>,?,?,?,?>> 
        implements RegistryContext<R, String, String>, BundleContext {
    private R myRegistry;
    private BundleContext myContext;

    OSGiContext(R registry, BundleContext context) {
        if(registry == null || context == null){
            throw new NullPointerException();
        }
        myRegistry = registry;
        myContext = context;
    }

    /**
     * Gets the context's registry.
     * @return the registry
     */
    @Override
    public R getRegistry() {
        return myRegistry;
    }

    /**
     * Gets a property of the context by name
     * @param key the property's name
     * @return the property's value
     */
    @Override
    public String getProperty(String key) {
        return myContext.getProperty(key);
    }

    /**
     * Gets the context's bundle
     * @return the bundle
     */
    @Override
    public Bundle getBundle() {
        return myContext.getBundle();
    }

    /**
     * Installs a bundle to the context.
     * @param location the bundle's location
     * @param input an input stream
     * @return the installed bundle
     * @throws BundleException
     */
    @Override
    public Bundle installBundle(String location, InputStream input) throws BundleException {
        return myContext.installBundle(location, input);
    }

    /**
     * Installs a bundle to the context.
     * @param location the bundle's location
     * @return the installed bundle
     * @throws BundleException
     */
    @Override
    public Bundle installBundle(String location) throws BundleException {
        return myContext.installBundle(location);
    }

    /**
     * Gets the context's bundle by ID.
     * @param id the ID
     * @return the bundle
     */
    @Override
    public Bundle getBundle(long id) {
        return myContext.getBundle(id);
    }

    /**
     * Gets all the context's bundles.
     * @return a list of bundles
     */
    @Override
    public Bundle[] getBundles() {
        return myContext.getBundles();
    }

    /**
     * Adds a service listener to the context.
     * @param listener the service listener
     * @param filter an OSGi filter string to listen for
     * @throws InvalidSyntaxException
     */
    @Override
    public void addServiceListener(ServiceListener listener, String filter) throws InvalidSyntaxException {
        myContext.addServiceListener(listener, filter);
    }

    /**
     * Adds a service listener to the context.
     * @param listener the service listener
     */
    @Override
    public void addServiceListener(ServiceListener listener) {
        myContext.addServiceListener(listener);
    }

    /**
     * Removes a service listener from the context
     * @param listener the service listener
     */
    @Override
    public void removeServiceListener(ServiceListener listener) {
        myContext.removeServiceListener(listener);
    }

    /**
     * Adds a bundle listener to the context
     * @param listener the bundle listener
     */
    @Override
    public void addBundleListener(BundleListener listener) {
        myContext.addBundleListener(listener);
    }

    /**
     * Removes a bundle listener from the context
     * @param listener the bundle listener
     */
    @Override
    public void removeBundleListener(BundleListener listener) {
        myContext.removeBundleListener(listener);
    }

    /**
     * Adds a framework listener to the context
     * @param listener the framework listener
     */
    @Override
    public void addFrameworkListener(FrameworkListener listener) {
        myContext.addFrameworkListener(listener);
    }

    /**
     * Removes a framework listener from the context
     * @param listener the framework listener
     */
    @Override
    public void removeFrameworkListener(FrameworkListener listener) {
        myContext.removeFrameworkListener(listener);
    }

    /**
     * Registers a service to the context.
     * @param clazzes the classes to register the service under
     * @param service the service to register
     * @param properties the service's properties
     * @return the service certificate
     */
    @Override
    public ServiceRegistration registerService(String[] clazzes, Object service, Dictionary properties) {
        return myContext.registerService(clazzes, service, properties);
    }

    /**
     * Registers a service to the context.
     * @param clazz the class to register the service under
     * @param service the service to register
     * @param properties the service's properties
     * @return the service certificate
     */
    @Override
    public ServiceRegistration registerService(String clazz, Object service, Dictionary properties) {
        return myContext.registerService(clazz, service, properties);
    }

    /**
     * Gets references to the context's services.
     * @param clazz the class to get references to
     * @param filter the filter string to search by
     * @return a list of references
     * @throws InvalidSyntaxException
     */
    @Override
    public ServiceReference[] getServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
        return myContext.getServiceReferences(clazz, filter);
    }

    /**
     * Gets all references to the context's services.
     * @param clazz the class to get references to
     * @param filter the filter string to search by
     * @return a list of references
     * @throws InvalidSyntaxException
     */
    @Override
    public ServiceReference[] getAllServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
        return myContext.getAllServiceReferences(clazz, filter);
    }

    /**
     * Gets a reference to one of the context's services.
     * @param clazz the class to get a reference to
     * @return a reference
     * @return
     */
    @Override
    public ServiceReference getServiceReference(String clazz) {
        return myContext.getServiceReference(clazz);
    }

    /**
     * Retrieves a service from its reference.
     * @param reference the reference
     * @return the service
     */
    @Override
    public Object getService(ServiceReference reference) {
        return myContext.getService(reference);
    }

    /**
     * Un-retrieves a service from its reference.
     * @param reference the reference
     * @return the service
     */
    @Override
    public boolean ungetService(ServiceReference reference) {
        return myContext.ungetService(reference);
    }

    /**
     * Get a data file by name
     * @param filename the file's name
     * @return the data file
     */
    @Override
    public File getDataFile(String filename) {
        return myContext.getDataFile(filename);
    }

    /**
     * Creates an OSGi service filter from a filter string
     * @param filter the filter string
     * @return the filter
     * @throws InvalidSyntaxException
     */
    @Override
    public Filter createFilter(String filter) throws InvalidSyntaxException {
        return myContext.createFilter(filter);
    }
    
    /**
     * Wraps an OSGi BundleContext into a JFlux RegistryContext
     */
    public static class BundleContextWrapper implements 
            Adapter<BundleContext,OSGiContext> {
        /**
         * Converts an OSGi BundleContext into a JFlux RegistryContext
         * @param a the BundleContext
         * @return
         */
        @Override
        public OSGiContext adapt(BundleContext a) {
            return new OSGiContext(new OSGiRegistry(new DefaultTimestampSource()), a);
        }        
    }
}
