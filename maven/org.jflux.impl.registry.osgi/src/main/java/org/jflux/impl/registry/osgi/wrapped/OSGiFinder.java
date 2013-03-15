/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jflux.impl.registry.osgi.wrapped;

import java.util.List;
import java.util.logging.Logger;
import org.jflux.api.core.Adapter;
import org.jflux.api.core.util.BatchAdapter;
import org.jflux.api.registry.Finder;
import org.jflux.api.registry.opt.Descriptor;
import org.jflux.impl.registry.osgi.direct.DirectFinder;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * Finder implementation for OSGi
 * @author Matthew Stevenson
 */
public class OSGiFinder implements 
        Finder<Descriptor<String,String>, OSGiReference> {
    private final static Logger theLogger = Logger.getLogger(OSGiFinder.class.getName());
    private DirectFinder myDirectFinder;
    
    //Needed for async find
//    private Map<Listener,Listener> myListenerMap;
    
    OSGiFinder(BundleContext context){
        if(context == null){
            throw new NullPointerException();
        }
        myDirectFinder = new DirectFinder(context);
        //Needed for async find
//        myListenerMap = new HashMap<Listener, Listener>();
    }
    
    @Override
    public OSGiReference findSingle(Descriptor<String, String> desc) {
        ServiceReference ref = myDirectFinder.findSingle(desc);
        return ref == null ? null : new OSGiReference(ref);
    }

    @Override
    public List<OSGiReference> findAll(Descriptor<String, String> desc) {
        return getRefListAdapter().adapt(myDirectFinder.findAll(desc));
    }

    @Override
    public List<OSGiReference> findCount(
            Descriptor<String, String> desc, int max) {
        return getRefListAdapter().adapt(myDirectFinder.findCount(desc, max));
    }

//    @Override
//    public void findSingleAsync(Descriptor<String, String> desc, Listener<OSGiReference> listener) {
//        myDirectFinder.findSingleAsync(desc, getWrappedListener(listener));
//    }
//
//    @Override
//    public void findAllAsync(Descriptor<String, String> desc, Listener<List<OSGiReference>> listener) {
//        myDirectFinder.findAllAsync(desc, getWrappedListListener(listener));
//    }
//
//    @Override
//    public void findCountAsync(Descriptor<String, String> desc, int max, Listener<List<OSGiReference>> listener) {
//        myDirectFinder.findCountAsync(desc, max, getWrappedListListener(listener));
//    }
//
//    @Override
//    public void removeAsyncListener(Listener listener) {
//        Listener rem = myListenerMap.get(listener);
//        if(rem != null){
//            myDirectFinder.removeAsyncListener(rem);
//        }
//    }
//    
//    private Listener<ServiceReference> getWrappedListener(
//            Listener<OSGiReference> listener){
//        Listener<ServiceReference> l = myListenerMap.get(listener);
//        if(l == null){
//            l = new ListenerChain(OSGiReference.getReferenceAdapter(), listener);
//            myListenerMap.put(listener, l);
//        }
//        return l;
//    }
//    
//    private Listener<List<ServiceReference>> getWrappedListListener(
//            Listener<List<OSGiReference>> listener){
//        Listener<List<ServiceReference>> l = myListenerMap.get(listener);
//        if(l == null){
//            l = new ListenerChain(getRefListAdapter(), listener);
//            myListenerMap.put(listener, l);
//        }
//        return l;
//    }
    
    private static Adapter<List<ServiceReference>, List<OSGiReference>> getRefListAdapter(){
        if(theRefListAdapter == null){
            theRefListAdapter = new BatchAdapter(OSGiReference.getReferenceAdapter());
        }
        return theRefListAdapter;
    }
    private static Adapter<List<ServiceReference>, List<OSGiReference>> theRefListAdapter;
}
