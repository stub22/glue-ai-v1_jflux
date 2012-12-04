/*
 * Copyright 2012 The JFlux Project (www.jflux.org).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jflux.impl.registry.osgi.direct;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jflux.api.core.Adapter;
import org.jflux.api.core.Listener;
import org.jflux.api.core.Notifier;
import org.jflux.api.registry.Retriever;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * A bare OSGi retriever with no JFlux wrappers.
 * @author Matthew Stevenson
 */
public class DirectRetriever implements 
        Retriever<ServiceReference> {
    private final static Logger theLogger = Logger.getLogger(DirectRetriever.class.getName());
    BundleContext myContext;
    
    DirectRetriever(BundleContext context){
        if(context == null){
            throw new NullPointerException();
        }
        myContext = context;
    }
    
    @Override
    public <T> Adapter<ServiceReference, T> retrieve(final Class<T> clazz) {
        return new Adapter<ServiceReference, T>() {
            @Override
            public T adapt(ServiceReference a) {
                Object obj = myContext.getService(a);
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
    public Adapter<ServiceReference, ?> retrieve() {
        return new Adapter<ServiceReference, Object>() {
            @Override
            public Object adapt(ServiceReference a) {
                return myContext.getService(a);
            }
        };
    }

    @Override
    public Listener<ServiceReference> release() {
        return new Listener<ServiceReference>() {

            @Override
            public void handleEvent(ServiceReference event) {
                myContext.ungetService(event);
            }
        };
    }

    /**
     * Not supported yet.
     * @param clazz
     * @return
     */
    @Override
    public <T> Adapter<ServiceReference, Notifier<T>> retrieveAsync(Class<T> clazz) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Not supported yet.
     * @return
     */
    @Override
    public Adapter<ServiceReference, Notifier<?>> retrieveAsync() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}