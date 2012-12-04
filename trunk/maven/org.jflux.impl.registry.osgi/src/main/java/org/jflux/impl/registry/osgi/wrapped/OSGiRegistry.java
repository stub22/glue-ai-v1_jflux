/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jflux.impl.registry.osgi.wrapped;

import org.jflux.api.core.Source;
import org.jflux.api.registry.Registry;
import org.jflux.api.registry.opt.Modification;
import org.jflux.api.registry.opt.RegistrationRequest;
import org.jflux.impl.registry.osgi.direct.OSGiDirectRegistry;

/**
 * Registry implementation for OSGi
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
    private Source<Time> myTimestampSource;
    
    /**
     * Creates a timestamped OSGi registry with a timestamp.
     * @param timestampSource the timestamp
     */
    public OSGiRegistry(Source<Time> timestampSource){
        if(timestampSource == null){
            throw new NullPointerException();
        }
        myTimestampSource = timestampSource;
    }
    
    /**
     * Creates a timestamped OSGiRegistry from a direct registry.
     * @param directRegistry the direct registry
     * @param timestampSource the timestamp
     */
    public OSGiRegistry(OSGiDirectRegistry<Time> directRegistry, Source<Time> timestampSource){
        if(directRegistry == null){
            throw new NullPointerException();
        }
        myDirectRegistry = directRegistry;
        myTimestampSource = timestampSource;
    }

    @Override
    public OSGiFinder getFinder(OSGiContext<OSGiRegistry<Time>> context) {
        return new OSGiFinder(context);
    }

    @Override
    public OSGiMonitor<OSGiRegistry<Time>, Time> getMonitor(
                    OSGiContext<OSGiRegistry<Time>> context) {
        return new OSGiMonitor<OSGiRegistry<Time>, Time>(context, myTimestampSource);
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
    
    /**
     * Gets the underlying direct registry
     * @return the direct registry
     */
    public OSGiDirectRegistry getDirectRegistry(){
        return myDirectRegistry;
    }
}
