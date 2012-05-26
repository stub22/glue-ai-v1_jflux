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
import org.jflux.api.core.Notifier;
import org.jflux.api.core.util.DefaultNotifier;
import org.jflux.api.registry.Finder;
import org.jflux.api.registry.opt.Descriptor;
import org.jflux.impl.registry.osgi.util.OSGiRegistryUtil;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 *
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
    public Adapter<Descriptor<String, String>, List<ServiceReference>> 
            findCount(final int max) {
        
        final Adapter<Descriptor<String, String>, 
                List<ServiceReference>> find = findAll();
        
        return new Adapter<
                Descriptor<String, String>, List<ServiceReference>>() {
            @Override
            public List<ServiceReference> adapt(Descriptor<String, String> a) {
                List<ServiceReference> list = find.adapt(a);
                if(list == null || list.size() <= max){
                    return list;
                }
                return list.subList(0, max);
            }
        };
    }

    @Override
    public Adapter<Descriptor<String, String>, Notifier<ServiceReference>> findSingleAsync() {
        return new Adapter<Descriptor<String, String>, Notifier<ServiceReference>>() {

            @Override
            public Notifier<ServiceReference> adapt(final Descriptor<String, String> a) {
                final Adapter<Descriptor<String, String>, 
                        ServiceReference> find = findSingle();
                if(find == null){
                    return null;
                }
                final Notifier<ServiceReference> notifier = 
                        new DefaultNotifier<ServiceReference>();
                
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ServiceReference ref = find.adapt(a);
                        notifier.notifyListeners(ref);
                    }
                }).start();
                return notifier;
            }
        };
    }

    @Override
    public Adapter<Descriptor<String, String>, Notifier<ServiceReference>> findContinuousAsync() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Adapter<Descriptor<String, String>, Notifier<ServiceReference>> findContinuousAsync(int max) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Adapter<Descriptor<String, String>, Notifier<List<ServiceReference>>> findAllAsync() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Adapter<Descriptor<String, String>, Notifier<List<ServiceReference>>> findCountAsync(int max) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private class FinderBase implements 
            Adapter<Descriptor<String, String>, ServiceReference[]> {
        
        @Override
        public ServiceReference[] adapt(Descriptor<String, String> a) {
            String className = a.getClassName();
            String filter = OSGiRegistryUtil.getFilter(a);
            try{
                return myContext.getServiceReferences(className, filter);
            }catch(InvalidSyntaxException ex){
                theLogger.log(Level.SEVERE, "Invalid LDAP filter: " + filter, ex);
                return null;
            }
        }
    } 
}
