/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jflux.impl.registry.osgi.wrapped;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.logging.Logger;
import org.jflux.api.core.Adapter;
import org.jflux.api.core.Listener;
import org.jflux.api.core.Notifier;
import org.jflux.api.registry.Accessor;
import org.jflux.api.registry.opt.Modification;
import org.jflux.api.registry.opt.RegistrationRequest;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * Accessor implementation for OSGi.
 * @author Matthew Stevenson
 */
public class OSGiAccessor<
        R extends RegistrationRequest<?,String,String>,
        M extends Modification<? extends OSGiCertificate,String,String>> 
        implements Accessor<R,OSGiCertificate,M>{
    private final static Logger theLogger = Logger.getLogger(OSGiAccessor.class.getName());
    private BundleContext myContext;
    
    OSGiAccessor(BundleContext context){
        if(context == null){
            throw new NullPointerException();
        }
        myContext = context;
    }

    @Override
    public Adapter<R, OSGiCertificate> register() {
        return new Adapter<R, OSGiCertificate>() {

            @Override
            public OSGiCertificate adapt(R a) {
                String[] classNames = a.getClassNames().toArray(new String[0]);
                Dictionary<String,String> props = 
                        new Hashtable<String, String>(a.getProperties());
                Object item = a.getItem();
                ServiceRegistration reg = 
                        myContext.registerService(classNames, item, props);
                return new OSGiCertificate(reg);
            }
        };
    }

    @Override
    public Listener<OSGiCertificate> unregister() {
        return new Listener<OSGiCertificate>() {

            @Override
            public void handleEvent(OSGiCertificate event) {
                event.unregister();
            }
        };
    }

    @Override
    public Adapter<M, OSGiCertificate> modify() {
        return new Adapter<M, OSGiCertificate>() {

            @Override
            public OSGiCertificate adapt(M a) {
                OSGiCertificate cert = a.getCertificate();
                cert.setProperties(new Hashtable(a.getProperties()));
                return cert;
            }
        };
    }

    /**
     * Not supported yet.
     * @return
     */
    @Override
    public Adapter<R, Notifier<OSGiCertificate>> registerAsync() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Not supported yet.
     * @return
     */
    @Override
    public Adapter<M, Notifier<OSGiCertificate>> modifyAsync() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
}
