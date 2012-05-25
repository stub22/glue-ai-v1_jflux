/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jflux.impl.registry.osgi.wrapped;

import org.jflux.api.core.event.Event;
import org.jflux.api.core.event.Header;
import org.jflux.api.registry.Registry.RegistryTemplate;
import org.jflux.api.registry.opt.Descriptor;
import org.jflux.api.registry.opt.Modification;
import org.jflux.api.registry.opt.RegistrationRequest;

/**
 *
 * @author Matthew Stevenson
 */
public class OSGiRegistry<Time> implements RegistryTemplate<
        String, String, Time, String, String, 
        OSGiContext<OSGiRegistry<Time>>, 
        Descriptor<String, String>, 
        OSGiReference, 
        RegistrationRequest<?, String, String>, 
        OSGiCertificate, 
        Modification<OSGiCertificate, String, String>, 
        Event<? extends Header<OSGiRegistry<Time>, Time>, OSGiReference>,
        OSGiFinder, 
        OSGiAccessor<
                RegistrationRequest<?, String, String>,
                Modification<OSGiCertificate, String ,String>>, 
        OSGiRetriever<OSGiReference>, 
        OSGiMonitor<
                OSGiRegistry<Time>, Time, Event<
                ? extends Header<OSGiRegistry<Time>, Time>, OSGiReference>>> {

    @Override
    public OSGiFinder getFinder(OSGiContext<OSGiRegistry<Time>> context) {
        return new OSGiFinder(context);
    }

    @Override
    public OSGiMonitor<
            OSGiRegistry<Time>, Time, 
            Event<? extends Header<OSGiRegistry<Time>, Time>, 
            OSGiReference>> getMonitor(
                    OSGiContext<OSGiRegistry<Time>> context) {
        return new OSGiMonitor<
                OSGiRegistry<Time>, Time, 
                Event<? extends Header<OSGiRegistry<Time>, Time>, 
                OSGiReference>>(context);
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
}
