/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jflux.impl.registry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jflux.api.core.Adapter;
import org.jflux.api.core.Listener;
import org.jflux.api.core.Source;
import org.jflux.api.core.chain.ListenerChain;
import org.jflux.api.core.event.BasicMutableHeader;
import org.jflux.api.core.event.Event;
import org.jflux.api.core.event.Header;
import org.jflux.api.core.util.BatchAdapter;
import org.jflux.api.registry.Registry;
import org.jflux.api.registry.opt.Descriptor;
import org.jflux.api.registry.opt.Modification;
import org.jflux.api.registry.opt.RegistrationRequest;
import org.jflux.impl.registry.osgi.util.ServiceEventAdapter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * Registry implementation for OSGi
 * @author Matthew Stevenson
 */
public class OSGiRegistry<Time> implements Registry<
        Descriptor<String,String>, 
        OSGiReference, 
        RegistrationRequest<Time, String, String>,
        OSGiCertificate,
        Modification<String ,String>, 
        Event<Header<OSGiRegistry<Time>, Time>, OSGiReference>> {
    private OSGiDirectRegistry<Time> myDirectRegistry;
    private Source<Time> myTimestampSource;
    private ServiceEventAdapter myEventAdapter;
    private Map<Listener<Event<Header<OSGiRegistry<Time>, Time>, OSGiReference>>, Listener<ServiceEvent>> myListenerMap;
    
    /**
     * Creates a timestamped OSGi registry with a timestamp.
     * @param timestampSource the timestamp
     */
    public OSGiRegistry(BundleContext context, Source<Time> timestampSource){
        if(context == null || timestampSource == null){
            throw new NullPointerException();
        }
        myDirectRegistry = new OSGiDirectRegistry<Time>(context);
        myTimestampSource = timestampSource;
        myEventAdapter = new ServiceEventAdapter(
                new BasicMutableHeader.MutableHeaderSource<OSGiRegistry<Time>, Time>(null, myTimestampSource, "", null));
        myListenerMap = new HashMap<Listener<Event<Header<OSGiRegistry<Time>, Time>, OSGiReference>>, Listener<ServiceEvent>>();
    }
    
    /**
     * Creates a timestamped OSGiRegistry from a direct registry.
     * @param directRegistry the direct registry
     * @param timestampSource the timestamp
     */
    public OSGiRegistry(OSGiDirectRegistry<Time> directRegistry, Source<Time> timestampSource){
        if(directRegistry == null || timestampSource == null){
            throw new NullPointerException();
        }
        myDirectRegistry = directRegistry;
        myTimestampSource = timestampSource;
        myEventAdapter = new ServiceEventAdapter(
                new BasicMutableHeader.MutableHeaderSource<OSGiRegistry<Time>, Time>(null, myTimestampSource, "", null));
        myListenerMap = new HashMap<Listener<Event<Header<OSGiRegistry<Time>, Time>, OSGiReference>>, Listener<ServiceEvent>>();
    }
    
    @Override
    public OSGiReference findSingle(Descriptor<String, String> desc) {
        ServiceReference ref = myDirectRegistry.findSingle(desc);
        return ref == null ? null : new OSGiReference(ref);
    }

    @Override
    public List<OSGiReference> findAll(Descriptor<String, String> desc) {
        return getRefListAdapter().adapt(myDirectRegistry.findAll(desc));
    }

    @Override
    public List<OSGiReference> findCount(
            Descriptor<String, String> desc, int max) {
        return getRefListAdapter().adapt(myDirectRegistry.findCount(desc, max));
    }
    
    @Override
    public <T> T retrieve(final Class<T> clazz, OSGiReference reference) {
        return myDirectRegistry.retrieve(clazz, reference);
    }

    @Override
    public Object retrieve(OSGiReference reference) {
        return myDirectRegistry.retrieve(reference);
    }

    @Override
    public void release(OSGiReference reference) {
        myDirectRegistry.release(reference);
    }

    @Override
    public OSGiCertificate register(RegistrationRequest<Time, String, String> request) {
        ServiceRegistration reg = myDirectRegistry.register(request);
        return reg == null ? null : new OSGiCertificate(reg);
    }

    @Override
    public void unregister(OSGiCertificate cert) {
        myDirectRegistry.unregister(cert);
    }

    @Override
    public void modify(OSGiCertificate cert, Modification<String, String> request) {
        myDirectRegistry.modify(cert, request);
    }

    @Override
    public void addListener(Descriptor<String, String> desc, Listener<Event<Header<OSGiRegistry<Time>, Time>, OSGiReference>> listener) {
        myDirectRegistry.addListener(desc, getWrappedListener(listener));
    }

    @Override
    public void removeListener(Listener<Event<Header<OSGiRegistry<Time>, Time>, OSGiReference>> listener) {
        myDirectRegistry.removeListener(getWrappedListener(listener));
    }
    
    private Listener<ServiceEvent> getWrappedListener(Listener<Event<Header<OSGiRegistry<Time>, Time>, OSGiReference>> listener){
        Listener<ServiceEvent> l = myListenerMap.get(listener);
        if(l == null){
            l = new ListenerChain<ServiceEvent, Event<Header<OSGiRegistry<Time>, Time>, OSGiReference>>(myEventAdapter, listener);
            myListenerMap.put(listener, l);
        }
        return l;
    }
    /**
     * Gets the underlying direct registry
     * @return the direct registry
     */
    public OSGiDirectRegistry getDirectRegistry(){
        return myDirectRegistry;
    }
    
    private static Adapter<List<ServiceReference>, List<OSGiReference>> getRefListAdapter(){
        if(theRefListAdapter == null){
            theRefListAdapter = new BatchAdapter(OSGiReference.getReferenceAdapter());
        }
        return theRefListAdapter;
    }
    private static Adapter<List<ServiceReference>, List<OSGiReference>> theRefListAdapter;
    
}
