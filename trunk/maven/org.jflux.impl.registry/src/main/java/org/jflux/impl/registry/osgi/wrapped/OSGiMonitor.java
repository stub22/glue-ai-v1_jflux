/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jflux.impl.registry.osgi.wrapped;

import org.jflux.api.core.Listener;
import org.jflux.api.core.event.Event;
import org.jflux.api.core.event.Header;
import org.jflux.api.core.playable.PlayableNotifier;
import org.jflux.api.registry.Monitor;
import org.jflux.api.registry.Registry;
import org.jflux.api.registry.opt.Descriptor;
import org.jflux.impl.registry.osgi.direct.DirectMonitor;

/**
 *
 * @author Matthew Stevenson
 */
public class OSGiMonitor<
        R extends Registry<?,?,?,?,OSGiMonitor<R,Time>>, Time> 
        implements Monitor<
                Descriptor<String,String>, 
                Event<Header<R, Time>, OSGiReference>, 
                PlayableNotifier<Event<Header<R, Time>, OSGiReference>>> {
    private DirectMonitor<Time> myDirectMonitor;
    
    OSGiMonitor(DirectMonitor<Time> directMonitor){
        if(directMonitor == null){
            throw new NullPointerException();
        }
        myDirectMonitor = directMonitor;
    }
    
    OSGiMonitor(OSGiContext context){
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public PlayableNotifier<Event<Header<R, Time>, OSGiReference>> 
            getNotifier(Descriptor<String, String> desc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Listener<PlayableNotifier<Event<Header<R, Time>, OSGiReference>>> 
            releaseNotifier() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
