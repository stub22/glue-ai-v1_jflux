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
 *
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

    @Override
    public R getRegistry() {
        return myRegistry;
    }

    @Override
    public String getProperty(String key) {
        return myContext.getProperty(key);
    }

    @Override
    public Bundle getBundle() {
        return myContext.getBundle();
    }

    @Override
    public Bundle installBundle(String location, InputStream input) throws BundleException {
        return myContext.installBundle(location, input);
    }

    @Override
    public Bundle installBundle(String location) throws BundleException {
        return myContext.installBundle(location);
    }

    @Override
    public Bundle getBundle(long id) {
        return myContext.getBundle(id);
    }

    @Override
    public Bundle[] getBundles() {
        return myContext.getBundles();
    }

    @Override
    public void addServiceListener(ServiceListener listener, String filter) throws InvalidSyntaxException {
        myContext.addServiceListener(listener, filter);
    }

    @Override
    public void addServiceListener(ServiceListener listener) {
        myContext.addServiceListener(listener);
    }

    @Override
    public void removeServiceListener(ServiceListener listener) {
        myContext.removeServiceListener(listener);
    }

    @Override
    public void addBundleListener(BundleListener listener) {
        myContext.addBundleListener(listener);
    }

    @Override
    public void removeBundleListener(BundleListener listener) {
        myContext.removeBundleListener(listener);
    }

    @Override
    public void addFrameworkListener(FrameworkListener listener) {
        myContext.addFrameworkListener(listener);
    }

    @Override
    public void removeFrameworkListener(FrameworkListener listener) {
        myContext.removeFrameworkListener(listener);
    }

    @Override
    public ServiceRegistration registerService(String[] clazzes, Object service, Dictionary properties) {
        return myContext.registerService(clazzes, service, properties);
    }

    @Override
    public ServiceRegistration registerService(String clazz, Object service, Dictionary properties) {
        return myContext.registerService(clazz, service, properties);
    }

    @Override
    public ServiceReference[] getServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
        return myContext.getServiceReferences(clazz, filter);
    }

    @Override
    public ServiceReference[] getAllServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
        return myContext.getAllServiceReferences(clazz, filter);
    }

    @Override
    public ServiceReference getServiceReference(String clazz) {
        return myContext.getServiceReference(clazz);
    }

    @Override
    public Object getService(ServiceReference reference) {
        return myContext.getService(reference);
    }

    @Override
    public boolean ungetService(ServiceReference reference) {
        return myContext.ungetService(reference);
    }

    @Override
    public File getDataFile(String filename) {
        return myContext.getDataFile(filename);
    }

    @Override
    public Filter createFilter(String filter) throws InvalidSyntaxException {
        return myContext.createFilter(filter);
    }
    
    public static class BundleContextWrapper implements 
            Adapter<BundleContext,OSGiContext> {
        @Override
        public OSGiContext adapt(BundleContext a) {
            return new OSGiContext(new OSGiRegistry(new DefaultTimestampSource()), a);
        }        
    }
}
