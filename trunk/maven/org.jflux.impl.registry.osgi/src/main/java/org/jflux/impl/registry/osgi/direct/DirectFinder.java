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

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jflux.api.core.Adapter;
import org.jflux.api.core.playable.PlayableNotifier;
import org.jflux.api.registry.Finder;
import org.jflux.api.registry.opt.Descriptor;
import org.jflux.impl.registry.osgi.util.OSGiRegistryUtil;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * A bare OSGi finder with no JFlux wrappers.
 * @author Matthew Stevenson
 */
public class DirectFinder implements 
        Finder<Descriptor<String,String>, ServiceReference> {
    private final static Logger theLogger = Logger.getLogger(DirectFinder.class.getName());
    private BundleContext myContext;
    private FinderBase myFinderBase;
    
    DirectFinder(BundleContext context){
        if(context == null){
            throw new NullPointerException();
        }
        myContext = context;
        myFinderBase = new FinderBase();
    }
    
    @Override
    public Adapter<Descriptor<String, String>, ServiceReference> findSingle() {
        return new Adapter<Descriptor<String, String>, ServiceReference>() {
            @Override
            public ServiceReference adapt(Descriptor<String, String> a) {
                ServiceReference[] refs = myFinderBase.adapt(a);
                if(refs == null){
                    return null;
                }
                return refs[0];
            }
        };
    }

    @Override
    public Adapter<
            Descriptor<String, String>, List<ServiceReference>> findAll() {
        return new Adapter<
                Descriptor<String, String>, List<ServiceReference>>() {
            @Override
            public List<ServiceReference> adapt(Descriptor<String, String> a) {
                ServiceReference[] refs = myFinderBase.adapt(a);
                if(refs == null){
                    return null;
                }
                return Arrays.asList(refs);
            }
        };
    }

    @Override
    public Adapter<Descriptor<String, String>, 
            List<ServiceReference>> findCount(final int max) {
        return DefaultFinderProvider.findCount(this, max);
    }

    /**
     * Returns an Adapter for finding a single reference from a description
     * asynchronously.
     * @return Adapter for finding a single reference from a description
     */
    @Override
    public Adapter<Descriptor<String, String>, 
            PlayableNotifier<ServiceReference>> findSingleAsync() {
        return DefaultFinderProvider.findSingleAsync(this);
    }

    /**
        * Returns an Adapter for finding a continuous reference from a description
        * asynchronously.
        * @return Adapter for finding a continuous reference from a description
     */
    @Override
    public Adapter<Descriptor<String, String>, 
            PlayableNotifier<ServiceReference>> findContinuousAsync() {
        return DefaultFinderProvider.findContinuousAsync(this);
    }

    /**
     * Returns an Adapter for finding a continuous reference from a description
     * asynchronously.
     * @param max the maximum number of parameters
     * @return Adapter for finding a continuous reference from a description
     */
    @Override
    public Adapter<Descriptor<String, String>, 
            PlayableNotifier<ServiceReference>> findContinuousAsync(int max) {
        return DefaultFinderProvider.findContinuousAsync(this, max);
    }

    /**
     * Returns an Adapter for finding all references matching a description
     * asynchronously.
     * @return Adapter for finding all references matching a description.
     */
    @Override
    public Adapter<Descriptor<String, String>, 
            PlayableNotifier<List<ServiceReference>>> findAllAsync() {
        return DefaultFinderProvider.findAllAsync(this);
    }

    /**
     * Returns an Adapter for finding the number of references matching a
     * description asynchronously.
     * @param max the maximum number of parameters
     * @return Adapter for finding the number of references matching a
     * description.
     */
    @Override
    public Adapter<Descriptor<String, String>, 
            PlayableNotifier<List<ServiceReference>>> findCountAsync(int max) {
        return DefaultFinderProvider.findCountAsync(this, max);
    }
    
    private class FinderBase implements 
            Adapter<Descriptor<String, String>, ServiceReference[]> {
        
        @Override
        public ServiceReference[] adapt(Descriptor<String, String> a) {
            String className = a.getClassName();
            String filter = OSGiRegistryUtil.getPropertiesFilter(a);
            try{
                return myContext.getServiceReferences(className, filter);
            }catch(InvalidSyntaxException ex){
                theLogger.log(Level.SEVERE, "Invalid LDAP filter: " + filter, ex);
                return null;
            }
        }
    } 
}
