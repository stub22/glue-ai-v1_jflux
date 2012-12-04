/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jflux.impl.registry.osgi.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.jflux.api.core.Adapter;
import org.jflux.api.registry.opt.Descriptor;
import org.osgi.framework.Constants;

/**
 *
 * @author Matthew Stevenson
 */
public class DescriptorFilterAdapter implements 
        Adapter<Descriptor<String,String>, String> {

    /**
     * Creates an OSGi filter string from a descriptor's properties.
     * @param a the descriptor
     * @return an OSGi filter string
     */
    public static String getPropertiesFilter(Descriptor<String, String> a) {
        Set<String> keys = a.getPropertyKeys();
        List<String> conditions = new ArrayList<String>(keys.size());
        for(String key : a.getPropertyKeys()){
            String val = a.getProperty(key);
            String condition = format(key, val);
            conditions.add(condition);
        }
        String filter;
        if(conditions.size() > 1) {
            filter = combine(conditions);
        } else if(conditions.size() == 1) {
            filter = conditions.get(0);
        } else {
            filter = null;
        }
        return filter;
    }
    /**
     * Converts a descriptor into an OSGi filter string.
     * @param a the descriptor
     * @return an OSGi filter string
     */
    public static String getFullFilter(Descriptor<String, String> a) {
        Set<String> keys = a.getPropertyKeys();
        List<String> conditions = new ArrayList<String>(keys.size());
        for(String key : a.getPropertyKeys()){
            String val = a.getProperty(key);
            String condition = format(key, val);
            conditions.add(condition);
        }
        conditions.add(format(Constants.OBJECTCLASS, a.getClassName()));
        String filter;
        if(conditions.size() > 1) {
            filter = combine(conditions);
        } else if(conditions.size() == 1) {
            filter = conditions.get(0);
        } else {
            filter = null;
        }
        return filter;
    }

    private static String format(String key, String val){
        return "(" + key + "=" + val + ")";
    }

    private static String combine(List<String>conditions){
        StringBuilder sb = new StringBuilder("(&");
        for(String str : conditions){
            sb.append(str);
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * Creates an OSGi filter string from a descriptor's properties.
     * @param a the descriptor
     * @return an OSGi filter string
     */
    @Override
    public String adapt(Descriptor<String, String> a) {
        return getPropertiesFilter(a);
    }
}
