/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jflux.impl.registry.osgi.wrapped;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jflux.api.core.Adapter;
import org.jflux.api.core.Notifier;
import org.jflux.api.registry.Retriever;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * Retriever implementation for OSGi.
 * @author Matthew Stevenson
 */
public class OSGiRetriever<Ref extends OSGiReference> implements 
        Retriever<Ref> {
    private final static Logger theLogger = Logger.getLogger(OSGiRetriever.class.getName());
    BundleContext myContext;
    
    OSGiRetriever(BundleContext context){
        if(context == null){
            throw new NullPointerException();
        }
        myContext = context;
    }
    
    @Override
    public <T> T retrieve(final Class<T> clazz, Ref reference) {
        ServiceReference ref = reference.getReference();
        Object obj = myContext.getService(ref);
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
    public Object retrieve(Ref reference) {
        ServiceReference ref = reference.getReference();
        return myContext.getService(ref);
    }

    @Override
    public void release(Ref reference) {
        ServiceReference ref = reference.getReference();
        myContext.ungetService(ref);
    }

    /**
     * Not supported yet.
     * @param clazz
     * @return
     */
    @Override
    public <T> Adapter<Ref, Notifier<T>> retrieveAsync(Class<T> clazz) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Not supported yet.
     * @return
     */
    @Override
    public Adapter<Ref, Notifier<?>> retrieveAsync() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
