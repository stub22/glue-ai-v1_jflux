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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jflux.api.core.Listener;
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
    private Map<Listener,List<PlayableNotifier>> myAsyncMap;
    
    public DirectFinder(BundleContext context){
        if(context == null){
            throw new NullPointerException();
        }
        myContext = context;
    }
    
    @Override
    public ServiceReference findSingle(Descriptor<String, String> desc) {
        ServiceReference[] refs = find(desc);
        if(refs == null){
            return null;
        }
        return refs[0];
    }

    @Override
    public List<ServiceReference> findAll(Descriptor<String, String> desc) {
        ServiceReference[] refs = find(desc);
        if(refs == null){
            return null;
        }
        return Arrays.asList(refs);
    }

    @Override
    public List<ServiceReference> findCount(
            Descriptor<String, String> desc, int max) {
        return DefaultFinderProvider.findCount(this, desc, max);
    }
//
//    /**
//     * Returns an Adapter for finding a single reference from a description
//     * asynchronously.
//     * @return Adapter for finding a single reference from a description
//     */
//    @Override
//    public void findSingleAsync(
//            Descriptor<String, String> desc, 
//            Listener<ServiceReference> listener) {
//        PlayableNotifier<ServiceReference> n = 
//                DefaultFinderProvider.findSingleAsync(this, desc, listener);
//        addAsyncListener(listener, n);
//    }
//
//    /**
//     * Returns an Adapter for finding all references matching a description
//     * asynchronously.
//     * @return Adapter for finding all references matching a description.
//     */
//    @Override
//    public void findAllAsync(
//            Descriptor<String, String> desc, 
//            Listener<List<ServiceReference>> listener) {
//        PlayableNotifier<List<ServiceReference>> n = 
//                DefaultFinderProvider.findAllAsync(this, desc, listener);
//        addAsyncListener(listener, n);
//    }
//
//    /**
//     * Returns an Adapter for finding the number of references matching a
//     * description asynchronously.
//     * @param max the maximum number of parameters
//     * @return Adapter for finding the number of references matching a
//     * description.
//     */
//    @Override
//    public void findCountAsync(
//            Descriptor<String, String> desc, int max,
//            Listener<List<ServiceReference>> listener) {
//        PlayableNotifier<List<ServiceReference>> n = 
//                DefaultFinderProvider.findCountAsync(this, desc, max, listener);
//        addAsyncListener(listener, n);
//    }
//    
//    private synchronized void addAsyncListener(Listener l, PlayableNotifier n){
//        List<PlayableNotifier> ns = myAsyncMap.get(l);
//        if(ns == null){
//            ns = new ArrayList<PlayableNotifier>();
//            myAsyncMap.put(l, ns);
//        }
//        ns.add(n);
//    }
//
//    @Override
//    public synchronized void removeAsyncListener(Listener listener) {
//        List<PlayableNotifier> ns = myAsyncMap.remove(listener);
//        if(ns == null){
//            return;
//        }
//        for(PlayableNotifier n : ns){
//            if(n == null){
//                continue;
//            }
//            n.stop();
//            n.removeListener(listener);
//        }
//    }
//    
    public ServiceReference[] find(Descriptor<String, String> a) {
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
