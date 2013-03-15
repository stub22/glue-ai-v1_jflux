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
    
    public DirectRetriever(BundleContext context){
        if(context == null){
            throw new NullPointerException();
        }
        myContext = context;
    }
    
    @Override
    public <T> T retrieve(Class<T> clazz, ServiceReference reference) {
        Object obj = myContext.getService(reference);
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
    public Object retrieve(ServiceReference reference) {
        return myContext.getService(reference);
    }

    @Override
    public void release(ServiceReference reference) {
        myContext.ungetService(reference);
    }
}