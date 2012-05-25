/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jflux.impl.registry.osgi.wrapped;

import org.jflux.api.core.Listener;
import org.jflux.api.core.Notifier;
import org.jflux.api.core.event.Event;
import org.jflux.api.core.event.Header;
import org.jflux.api.registry.Monitor;
import org.jflux.api.registry.Registry;
import org.jflux.api.registry.opt.Descriptor;
import org.osgi.framework.BundleContext;

/**
 *
 * @author Matthew Stevenson
 */
public class OSGiMonitor<
        R extends Registry<?,?,?,?,OSGiMonitor<R,Time,Evt>>, Time, 
        Evt extends Event<? extends Header<R, Time>, OSGiReference>> 
        implements Monitor<Descriptor<String,String>, Evt> {
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
    public Notifier<Evt> getNotifier(Descriptor<String, String> desc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Listener<Descriptor<String, String>> releaseNotifier() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
