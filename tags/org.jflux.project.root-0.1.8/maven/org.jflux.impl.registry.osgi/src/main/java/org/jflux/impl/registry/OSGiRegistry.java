/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jflux.impl.registry;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jflux.api.core.Adapter;
import org.jflux.api.core.Listener;
import org.jflux.api.core.util.BatchAdapter;
import org.jflux.api.registry.Certificate;
import org.jflux.api.registry.Descriptor;
import org.jflux.api.registry.Modification;
import org.jflux.api.registry.Reference;
import org.jflux.api.registry.RegistrationRequest;
import org.jflux.api.registry.Registry;
import org.jflux.api.registry.RegistryEvent;
import org.jflux.api.registry.basic.BasicRegistryEvent;
import org.jflux.api.registry.util.FinderUtils;
import org.jflux.impl.registry.osgi.util.OSGiRegistryUtil;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * Registry implementation for OSGi
 * @author Matthew Stevenson
 */
public class OSGiRegistry implements Registry {
    private final static Logger theLogger = Logger.getLogger(OSGiRegistry.class.getName());
    private BundleContext myContext;
    private Map<Listener<RegistryEvent>, ServiceListener> myListenerMap;
    
    /**
     * Creates a timestamped OSGi registry with a timestamp.
     * @param timestampSource the timestamp
     */
    public OSGiRegistry(BundleContext context){
        if(context == null){
            throw new NullPointerException();
        }
        myContext = context;
        myListenerMap = new HashMap<Listener<RegistryEvent>, ServiceListener>();
    }
    
    @Override
    public OSGiReference findSingle(Descriptor desc) {
        ServiceReference[] refs = find(desc);
        if(refs == null){
            return null;
        }
        return new OSGiReference(refs[0]);
    }

    @Override
    public List<Reference> findAll(Descriptor desc) {
        ServiceReference[] refs = find(desc);
        if(refs == null){
            return null;
        }
        return getRefListAdapter().adapt(Arrays.asList(refs));
    }

    @Override
    public List<Reference> findCount(
            Descriptor desc, int max) {
        return FinderUtils.findCount(this, desc, max);
    }
    
    private ServiceReference[] find(Descriptor a) {
        String className = a.getClassName();
        String filter = OSGiRegistryUtil.getPropertiesFilter(a);
        try{
            return myContext.getServiceReferences(className, filter);
        }catch(InvalidSyntaxException ex){
            theLogger.log(Level.SEVERE, "Invalid LDAP filter: " + filter, ex);
            return null;
        }
    }
    
    @Override
    public <T> T retrieve(final Class<T> clazz, Reference reference) {
        if(!(reference instanceof OSGiReference)){
            return null;
        }
        Object obj = myContext.getService((OSGiReference)reference);
        if(obj == null){
            return null;
        }
        try{
            return (T)obj;
        }catch(ClassCastException ex){
            theLogger.log(Level.SEVERE, 
                    "Unable to cast item to specified type.  "
                    + "Expected: " + clazz.getName() + ",  "
                    + "Found: " + obj.getClass().getName(), ex);
            return null;
        }
    }

    @Override
    public Object retrieve(Reference reference) {
        if(!(reference instanceof OSGiReference)){
            return null;
        }
        ServiceReference ref = ((OSGiReference)reference).getReference();
        return myContext.getService(ref);
    }

    @Override
    public void release(Reference reference) {
        if(!(reference instanceof OSGiReference)){
            return;
        }
        myContext.ungetService(((OSGiReference)reference).getReference());
    }

    @Override
    public OSGiCertificate register(RegistrationRequest request) {
        String[] classNames = request.getClassNames();
        Dictionary props = null;
        if(request.getProperties() != null){
            props = new Hashtable(request.getProperties());
        }
        Object item = request.getItem();
        ServiceRegistration reg =  myContext.registerService(classNames, item, props);
        return reg == null ? null : new OSGiCertificate(reg);
    }

    @Override
    public void unregister(Certificate cert) {
        if(!(cert instanceof OSGiCertificate)){
            return;
        }
        ((OSGiCertificate)cert).unregister();
    }

    @Override
    public void modify(Certificate cert, Modification request) {
        if(!(cert instanceof OSGiCertificate)){
            return;
        }
        Dictionary props = null;
        if(request.getProperties() != null){
            props = new Hashtable<String,String>(request.getProperties());
        }
        ((OSGiCertificate)cert).setProperties(props);
    }

    @Override
    public void addListener(Descriptor desc, Listener<RegistryEvent> listener) {
        String filter = OSGiRegistryUtil.getFullFilter(desc);
        ServiceListener sl = getWrappedListener(listener);
        try{
            myContext.addServiceListener(sl, filter);
        }catch(InvalidSyntaxException ex){
            theLogger.log(Level.SEVERE, 
                    "Invalid LDAP filter: " + filter, ex);
        }
    }

    @Override
    public void removeListener(Listener<RegistryEvent> listener) {
        ServiceListener sl = getWrappedListener(listener);
        if(sl != null){
            myContext.removeServiceListener(sl);
        }
    }
    
    private ServiceListener getWrappedListener(final Listener<RegistryEvent> listener){
        ServiceListener l = myListenerMap.get(listener);
        if(l == null){
            l = new ServiceListener() {
                @Override public void serviceChanged(ServiceEvent se) {
                    if(se == null || se.getServiceReference() == null){
                        return;
                    }
                    RegistryEvent e = new BasicRegistryEvent(
                            se.getType(), new OSGiReference(se.getServiceReference()));
                    listener.handleEvent(e);
                }};
            myListenerMap.put(listener, l);
        }
        return l;
    }
    
    private static Adapter<List<ServiceReference>, List<Reference>> getRefListAdapter(){
        if(theRefListAdapter == null){
            theRefListAdapter = new BatchAdapter(OSGiReference.getReferenceAdapter());
        }
        return theRefListAdapter;
    }
    private static Adapter<List<ServiceReference>, List<Reference>> theRefListAdapter;
    
}
