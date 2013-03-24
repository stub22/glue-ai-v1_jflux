/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jflux.impl.registry.osgi.util;

import org.jflux.api.registry.Descriptor;

/**
 *
 * @author Matthew Stevenson
 */
public class OSGiRegistryUtil {
    /**
     * Creates an OSGi filter string from a descriptor's properties.
     * @param desc the descriptor
     * @return an OSGi filter string
     */
    public static String getPropertiesFilter(Descriptor desc){
        return DescriptorFilterAdapter.getPropertiesFilter(desc);
    }
    /**
     * Converts a descriptor into an OSGi filter string.
     * @param desc the descriptor
     * @return an OSGi filter string
     */
    public static String getFullFilter(Descriptor desc){
        return DescriptorFilterAdapter.getFullFilter(desc);
    }
}
