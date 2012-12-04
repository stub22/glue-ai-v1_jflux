/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jflux.impl.registry.osgi.wrapped;

import java.util.ArrayList;
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
 * Finder implementation for OSGi
 * @author Matthew Stevenson
 */
public class OSGiFinder implements 
        Finder<Descriptor<String,String>, OSGiReference> {
    private final static Logger theLogger = Logger.getLogger(OSGiFinder.class.getName());
    private BundleContext myContext;
    private FinderBase myFinderBase;
    
    OSGiFinder(BundleContext context){
        if(context == null){
            throw new NullPointerException();
        }
        myContext = context;
        myFinderBase = new FinderBase();
    }
    
    @Override
    public Adapter<Descriptor<String, String>, OSGiReference> findSingle() {
        return new Adapter<Descriptor<String, String>, OSGiReference>() {
            @Override
            public OSGiReference adapt(Descriptor<String, String> a) {
                ServiceReference[] refs = myFinderBase.adapt(a);
                if(refs == null){
                    return null;
                }
                return new OSGiReference(refs[0]);
            }
        };
    }

    @Override
    public Adapter<
            Descriptor<String, String>, List<OSGiReference>> findAll() {
        return new Adapter<
                Descriptor<String, String>, List<OSGiReference>>() {
            @Override
            public List<OSGiReference> adapt(Descriptor<String, String> a) {
                ServiceReference[] refs = myFinderBase.adapt(a);
                if(refs == null){
                    return null;
                }
                List<OSGiReference> list = 
                        new ArrayList<OSGiReference>(refs.length);
                for(ServiceReference ref : refs){
                    list.add(new OSGiReference(ref));
                }
                return list;
            }
        };
    }

    @Override
    public Adapter<Descriptor<String, String>, 
            List<OSGiReference>> findCount(final int max) {
        return new Adapter<
                Descriptor<String, String>, List<OSGiReference>>() {
            @Override
            public List<OSGiReference> adapt(Descriptor<String, String> a) {
                ServiceReference[] refs = myFinderBase.adapt(a);
                if(refs == null){
                    return null;
                }
                int c = Math.min(max, refs.length);
                List<OSGiReference> list = 
                        new ArrayList<OSGiReference>(c);
                for(int i = 0; i < c; i++){
                    list.add(new OSGiReference(refs[i]));
                }
                return list;
            }
        };
    }

    /**
     * Returns an Adapter for finding a single reference from a description
     * asynchronously.
     * @return Adapter for finding a single reference from a description
     */
    @Override
    public Adapter<Descriptor<String, String>, 
            PlayableNotifier<OSGiReference>> findSingleAsync() {
        return DefaultFinderProvider.findSingleAsync(this);
    }

    /**
     * Returns an Adapter for finding a continuous reference from a description
     * asynchronously.
     * @return Adapter for finding a continuous reference from a description
     */
    @Override
    public Adapter<Descriptor<String, String>, 
            PlayableNotifier<OSGiReference>> findContinuousAsync() {
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
            PlayableNotifier<OSGiReference>> findContinuousAsync(int max) {
        return DefaultFinderProvider.findContinuousAsync(this, max);
    }

    /**
     * Returns an Adapter for finding all references matching a description
     * asynchronously.
     * @return Adapter for finding all references matching a description.
     */
    @Override
    public Adapter<Descriptor<String, String>, 
            PlayableNotifier<List<OSGiReference>>> findAllAsync() {
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
            PlayableNotifier<List<OSGiReference>>> findCountAsync(int max) {
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
                theLogger.log(Level.SEVERE, 
                        "Invalid LDAP filter: " + filter, ex);
                return null;
            }
        }
    }    
}
