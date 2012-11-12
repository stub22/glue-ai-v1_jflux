/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jflux.impl.registry.osgi.wrapped;

import org.jflux.api.core.Adapter;
import org.jflux.api.core.Listener;
import org.jflux.api.core.Notifier;
import org.jflux.api.core.Source;
import org.jflux.api.core.chain.NotifierChain;
import org.jflux.api.core.event.BasicEvent;
import org.jflux.api.core.event.BasicMutableHeader.MutableHeaderSource;
import org.jflux.api.core.event.Event;
import org.jflux.api.core.event.Header;
import org.jflux.api.core.event.MutableHeader;
import org.jflux.api.core.playable.PlayableNotifier;
import org.jflux.api.core.playable.PlayableNotifier.DefaultPlayableNotifier;
import org.jflux.api.registry.Monitor;
import org.jflux.api.registry.Registry;
import org.jflux.api.registry.opt.Descriptor;
import org.jflux.impl.registry.osgi.direct.DirectMonitor;
import org.jflux.impl.registry.osgi.direct.OSGiDirectRegistry;
import org.osgi.framework.ServiceEvent;

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
    private ServiceEventAdapter myEventAdapter;
    
    OSGiMonitor(DirectMonitor<Time> directMonitor, Source<Time> timestampSource){
        if(directMonitor == null){
            throw new NullPointerException();
        }
        myDirectMonitor = directMonitor;
        myEventAdapter = new ServiceEventAdapter(
                new MutableHeaderSource<R, Time>(null, timestampSource, "", null));
    }
    
    OSGiMonitor(OSGiContext context, Source<Time> timestampSource){
       this((DirectMonitor)new OSGiDirectRegistry().getMonitor(context), timestampSource);
    }
    
    @Override
    public PlayableNotifier<Event<Header<R, Time>, OSGiReference>> 
            getNotifier(Descriptor<String, String> desc) {
        DefaultPlayableNotifier<ServiceEvent> dpn = myDirectMonitor.getNotifier(desc);
        Notifier<ServiceEvent> n = dpn.getNotifier();
        Notifier<Event<Header<R, Time>, OSGiReference>> nc = new NotifierChain(n, myEventAdapter);
        return new DefaultPlayableNotifier<Event<Header<R, Time>, OSGiReference>>(nc);
    }

    @Override
    public Listener<PlayableNotifier<Event<Header<R, Time>, OSGiReference>>> 
            releaseNotifier() {
        return new ServiceNotifierStopper();
    }
    
    public final static String REGISTERED = "registered";
    public final static String MODIFIED = "modified";
    public final static String MODIFIED_ENDMATCH = "modified_endmatch";
    public final static String UNREGISTERING = "unregistering";
    
    public class ServiceEventAdapter implements 
            Adapter<ServiceEvent, Event<Header<R, Time>, OSGiReference>> {
        private Source<MutableHeader<R, Time>> myHeaderSource;
        
        public ServiceEventAdapter(Source<MutableHeader<R, Time>> headerSource){
            if(headerSource == null){
                throw new NullPointerException();
            }
            myHeaderSource = headerSource;
        }
        
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
    
    private class ServiceNotifierStopper implements 
            Listener<PlayableNotifier<Event<Header<R, Time>, OSGiReference>>>  {

        @Override
        public void handleEvent(PlayableNotifier<Event<Header<R, Time>, OSGiReference>> input) {
            input.stop();
        }
    }
}
