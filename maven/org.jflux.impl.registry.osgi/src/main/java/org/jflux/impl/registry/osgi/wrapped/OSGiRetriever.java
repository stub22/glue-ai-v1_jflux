/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jflux.impl.registry.osgi.wrapped;

import java.util.logging.Logger;
import org.jflux.api.registry.Retriever;
import org.jflux.impl.registry.osgi.direct.DirectRetriever;
import org.osgi.framework.BundleContext;

/**
 * Retriever implementation for OSGi.
 * @author Matthew Stevenson
 */
public class OSGiRetriever<Ref extends OSGiReference> implements 
        Retriever<Ref> {
    private final static Logger theLogger = Logger.getLogger(OSGiRetriever.class.getName());
    private BundleContext myContext;
    private DirectRetriever myDirectRetreiver;
    
    OSGiRetriever(BundleContext context){
        myDirectRetreiver = new DirectRetriever(context);
    }
    
    @Override
    public <T> T retrieve(final Class<T> clazz, Ref reference) {
        return myDirectRetreiver.retrieve(clazz, reference);
    }

    @Override
    public Object retrieve(Ref reference) {
        return myDirectRetreiver.retrieve(reference);
    }

    @Override
    public void release(Ref reference) {
        myDirectRetreiver.release(reference);
    }
}
