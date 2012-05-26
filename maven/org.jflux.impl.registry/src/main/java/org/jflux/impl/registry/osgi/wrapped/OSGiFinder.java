/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jflux.impl.registry.osgi.wrapped;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jflux.api.core.Adapter;
import org.jflux.api.core.Notifier;
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
    
    OSGiFinder(OSGiContext context){
        this(context.getBundleContext());
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
    public Adapter<Descriptor<String, String>, List<OSGiReference>> findCount(int max) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Adapter<Descriptor<String, String>, Notifier<OSGiReference>> findSingleAsync() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Adapter<Descriptor<String, String>, Notifier<OSGiReference>> findContinuousAsync() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Adapter<Descriptor<String, String>, Notifier<OSGiReference>> findContinuousAsync(int max) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Adapter<Descriptor<String, String>, Notifier<List<OSGiReference>>> findAllAsync() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Adapter<Descriptor<String, String>, Notifier<List<OSGiReference>>> findCountAsync(int max) {
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
