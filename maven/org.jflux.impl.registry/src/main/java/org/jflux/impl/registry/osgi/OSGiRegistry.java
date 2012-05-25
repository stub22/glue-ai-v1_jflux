/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jflux.impl.registry.osgi;

import org.jflux.api.registry.Registry;

/**
 *
 * @author Matthew Stevenson
 */
public class OSGiRegistry implements Registry<
        OSGiContext, OSGiFinder, OSGiAccessor, OSGiRetriever, OSGiMonitor> {

    @Override
    public OSGiFinder getFinder(OSGiContext context) {
        return new OSGiFinder(context);
    }

    @Override
    public OSGiMonitor getMonitor(OSGiContext context) {
        return new OSGiMonitor(context);
    }

    @Override
    public OSGiAccessor getAccessor(OSGiContext context) {
        return new OSGiAccessor(context);
    }

    @Override
    public OSGiRetriever getRetriever(OSGiContext context) {
        return new OSGiRetriever(context);
    }
    
}
