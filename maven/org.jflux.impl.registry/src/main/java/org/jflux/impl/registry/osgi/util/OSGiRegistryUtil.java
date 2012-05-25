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
    public static String getFilter(Descriptor<String,String> desc){
        return DescriptorFilterAdapter.getFilter(desc);
    }
}
