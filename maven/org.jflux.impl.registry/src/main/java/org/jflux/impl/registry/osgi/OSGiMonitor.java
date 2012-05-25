/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jflux.impl.registry.osgi;

import org.jflux.api.core.Listener;
import org.jflux.api.core.Notifier;
import org.jflux.api.core.event.Event;
import org.jflux.api.registry.Monitor;
import org.jflux.impl.registry.basic.opt.BasicDescriptor;
import org.osgi.framework.BundleContext;

/**
 *
 * @author Matthew Stevenson
 */
public class OSGiMonitor implements 
        Monitor<BasicDescriptor<String,String>, Event> {
    private BundleContext myContext;
    
    OSGiMonitor(BundleContext context){
        if(context == null){
            throw new NullPointerException();
        }
        myContext = context;
    }
    
    OSGiMonitor(OSGiContext context){
        this(context.getBundleContext());
    }
    
    @Override
    public Notifier<Event> getNotifier(BasicDescriptor<String, String> desc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Listener<BasicDescriptor<String, String>> releaseNotifier() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
