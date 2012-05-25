/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jflux.impl.registry.osgi;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jflux.api.core.Adapter;
import org.jflux.api.core.Listener;
import org.jflux.api.registry.Retriever;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 *
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
    
    OSGiRetriever(OSGiContext context){
        this(context.getBundleContext());
    }
    
    @Override
    public <T> Adapter<Ref, T> retrieve(final Class<T> clazz) {
        return new Adapter<Ref, T>() {
            @Override
            public T adapt(Ref a) {
                ServiceReference ref = a.getReference();
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
        };
    }

    @Override
    public Adapter<Ref, ?> retrieve() {
        return new Adapter<Ref, Object>() {
            @Override
            public Object adapt(Ref a) {
                ServiceReference ref = a.getReference();
                return myContext.getService(ref);
            }
        };
    }

    @Override
    public Listener<Ref> release() {
        return new Listener<Ref>() {

            @Override
            public void handleEvent(Ref event) {
                ServiceReference ref = event.getReference();
                myContext.ungetService(ref);
            }
        };
    }
    
}
