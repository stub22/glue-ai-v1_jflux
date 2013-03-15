/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jflux.impl.registry.osgi.wrapped;

import java.util.HashMap;
import java.util.Map;
import org.jflux.api.core.Adapter;
import org.jflux.api.core.Listener;
import org.jflux.api.core.Source;
import org.jflux.api.core.chain.ListenerChain;
import org.jflux.api.core.event.BasicEvent;
import org.jflux.api.core.event.BasicMutableHeader.MutableHeaderSource;
import org.jflux.api.core.event.Event;
import org.jflux.api.core.event.Header;
import org.jflux.api.core.event.MutableHeader;
import org.jflux.api.registry.Monitor;
import org.jflux.api.registry.Registry;
import org.jflux.api.registry.opt.Descriptor;
import org.jflux.impl.registry.osgi.direct.DirectMonitor;
import org.osgi.framework.ServiceEvent;

/**
 * Monitor implementation for OSGi
 * @author Matthew Stevenson
 */
public class OSGiMonitor<R extends Registry<?,?,?,?,OSGiMonitor<R,Time>>, Time> 
        implements Monitor<Descriptor<String,String>, 
                            Event<Header<R, Time>, OSGiReference>> {
    private DirectMonitor<Time> myDirectMonitor;
    private ServiceEventAdapter myEventAdapter;
    private Map<Listener<Event<Header<R, Time>, OSGiReference>>, Listener<ServiceEvent>> myListenerMap;
    
    OSGiMonitor(DirectMonitor<Time> directMonitor, Source<Time> timestampSource){
        if(directMonitor == null){
            throw new NullPointerException();
        }
        myDirectMonitor = directMonitor;
        myEventAdapter = new ServiceEventAdapter(
                new MutableHeaderSource<R, Time>(null, timestampSource, "", null));
        myListenerMap = new HashMap<Listener<Event<Header<R, Time>, OSGiReference>>, Listener<ServiceEvent>>();
    }
    
    OSGiMonitor(OSGiContext context, Source<Time> timestampSource){
       this(new DirectMonitor(context), timestampSource);
    }

    @Override
    public void addListener(Descriptor<String, String> desc, Listener<Event<Header<R, Time>, OSGiReference>> listener) {
        myDirectMonitor.addListener(desc, getWrappedListener(listener));
    }

    @Override
    public void removeListener(Descriptor<String, String> desc, Listener<Event<Header<R, Time>, OSGiReference>> listener) {
        myDirectMonitor.removeListener(desc, getWrappedListener(listener));
    }

    @Override
    public void removeListener(Listener<Event<Header<R, Time>, OSGiReference>> listener) {
        myDirectMonitor.removeListener(getWrappedListener(listener));
    }
    
    private Listener<ServiceEvent> getWrappedListener(Listener<Event<Header<R, Time>, OSGiReference>> listener){
        Listener<ServiceEvent> l = myListenerMap.get(listener);
        if(l == null){
            l = new ListenerChain<ServiceEvent, Event<Header<R, Time>, OSGiReference>>(myEventAdapter, listener);
            myListenerMap.put(listener, l);
        }
        return l;
    }
    
    /**
     * Registered state
     */
    public final static String REGISTERED = "registered";
    /**
     * Modified state
     */
    public final static String MODIFIED = "modified";
    /**
     * Modified (end match) state
     */
    public final static String MODIFIED_ENDMATCH = "modified_endmatch";
    /**
     * Unregistering state
     */
    public final static String UNREGISTERING = "unregistering";
    
    /**
     * Wraps an OSGi ServiceEvent into a JFlux Event
     */
    public class ServiceEventAdapter implements 
            Adapter<ServiceEvent, Event<Header<R, Time>, OSGiReference>> {
        private Source<MutableHeader<R, Time>> myHeaderSource;
        
        /**
         * Creates a ServiceEventAdapter from a header source
         * @param headerSource the header source
         */
        public ServiceEventAdapter(Source<MutableHeader<R, Time>> headerSource){
            if(headerSource == null){
                throw new NullPointerException();
            }
            myHeaderSource = headerSource;
        }
        
        /**
         * Converts an OSGi ServiceEvent into a JFlux Event
         * @param a the ServiceEvent
         * @return the Event
         */
        @Override
        public Event<Header<R, Time>, OSGiReference> adapt(ServiceEvent a) {
            MutableHeader<R, Time> h = myHeaderSource.getValue();
            h.setEventType(getTypeString(a.getType()));
            OSGiReference r = new OSGiReference(a.getServiceReference());
            return new BasicEvent<Header<R, Time>, OSGiReference>(h, r);
        }
        
        private String getTypeString(int type){
            switch(type){
                case ServiceEvent.REGISTERED : return REGISTERED;
                case ServiceEvent.MODIFIED : return MODIFIED;
                case ServiceEvent.MODIFIED_ENDMATCH : return MODIFIED_ENDMATCH;
                case ServiceEvent.UNREGISTERING : return UNREGISTERING;
                default : throw new IllegalArgumentException("Invalid Service Event Type.");
            }
        }
    }
}
