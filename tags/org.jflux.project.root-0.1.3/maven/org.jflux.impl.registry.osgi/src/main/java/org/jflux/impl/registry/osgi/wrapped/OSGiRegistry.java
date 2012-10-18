/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jflux.impl.registry.osgi.wrapped;

import org.jflux.api.registry.Registry;
import org.jflux.api.registry.opt.Modification;
import org.jflux.api.registry.opt.RegistrationRequest;
import org.jflux.impl.registry.osgi.direct.OSGiDirectRegistry;

/**
 *
 * @author Matthew Stevenson
 */
public class OSGiRegistry<Time> implements Registry<
        OSGiContext<OSGiRegistry<Time>>, 
        OSGiFinder, 
        OSGiAccessor<RegistrationRequest<?, String, String>,
                Modification<OSGiCertificate, String ,String>>, 
        OSGiRetriever<OSGiReference>, 
        OSGiMonitor<OSGiRegistry<Time>, Time>> {
    private OSGiDirectRegistry<Time> myDirectRegistry;
    
    public OSGiRegistry(){
        
    }
    
    public OSGiRegistry(OSGiDirectRegistry<Time> directRegistry){
        if(directRegistry == null){
            throw new NullPointerException();
        }
        myDirectRegistry = directRegistry;
    }

    @Override
    public OSGiFinder getFinder(OSGiContext<OSGiRegistry<Time>> context) {
        return new OSGiFinder(context);
    }

    @Override
    public OSGiMonitor<OSGiRegistry<Time>, Time> getMonitor(
                    OSGiContext<OSGiRegistry<Time>> context) {
        return new OSGiMonitor<OSGiRegistry<Time>, Time>(context);
    }

    @Override
    public OSGiAccessor<
            RegistrationRequest<?, String, String>, 
            Modification<OSGiCertificate, String, String>> getAccessor(
                    OSGiContext<OSGiRegistry<Time>> context) {
        return new OSGiAccessor<
                RegistrationRequest<?, String, String>, 
                Modification<OSGiCertificate, String, String>>(context);
    }

    @Override
    public OSGiRetriever<OSGiReference> getRetriever(
            OSGiContext<OSGiRegistry<Time>> context) {
        return new OSGiRetriever<OSGiReference>(context);
    }
    
    public OSGiDirectRegistry getDirectRegistry(){
        return myDirectRegistry;
    }
}
