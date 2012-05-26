/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jflux.impl.registry.osgi.util;

import org.jflux.api.registry.opt.Descriptor;

/**
 *
 * @author Matthew Stevenson
 */
public class OSGiRegistryUtil {
    public static String getPropertiesFilter(Descriptor<String,String> desc){
        return DescriptorFilterAdapter.getPropertiesFilter(desc);
    }
    public static String getFullFilter(Descriptor<String,String> desc){
        return DescriptorFilterAdapter.getFullFilter(desc);
    }
}
