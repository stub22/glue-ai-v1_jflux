/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jflux.impl.registry.osgi.wrapped;

import java.util.logging.Logger;
import org.jflux.api.registry.Accessor;
import org.jflux.api.registry.opt.Modification;
import org.jflux.api.registry.opt.RegistrationRequest;
import org.jflux.impl.registry.osgi.direct.DirectAccessor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * Accessor implementation for OSGi.
 * @author Matthew Stevenson
 */
public class OSGiAccessor<
        R extends RegistrationRequest<?,String,String>,
        M extends Modification<String,String>> 
        implements Accessor<R,OSGiCertificate,M>{
    private final static Logger theLogger = Logger.getLogger(OSGiAccessor.class.getName());
    private DirectAccessor myDirectAccessor;
    
    OSGiAccessor(BundleContext context){
        myDirectAccessor = new DirectAccessor(context);
    }

    @Override
    public OSGiCertificate register(R request) {
        ServiceRegistration reg = myDirectAccessor.register(request);
        return reg == null ? null : new OSGiCertificate(reg);
    }

    @Override
    public void unregister(OSGiCertificate cert) {
        myDirectAccessor.unregister(cert);
    }

    @Override
    public void modify(OSGiCertificate cert, M request) {
        myDirectAccessor.modify(cert, request);
    }
}
