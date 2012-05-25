/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jflux.impl.registry.osgi;

import org.jflux.api.registry.Registry;
import org.jflux.api.registry.opt.RegistryContext;
import org.osgi.framework.BundleContext;

/**
 *
 * @author Matthew Stevenson
 */
public class OSGiContext<R extends Registry> implements 
        RegistryContext<R, String, String> {
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
    
    final BundleContext getBundleContext(){
        return myContext;
    }
}
