/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jflux.api.registry.basic;

import org.jflux.api.registry.Descriptor;
import java.util.Map;
import java.util.Set;

/**
 * A basic implementation of Descriptor
 * @author Matthew Stevenson
 */
public class BasicDescriptor implements Descriptor {
    private String myName;
    private Map<String,String> myProperties;
    private String myClassName;
    
    /**
     * Gets the descriptor's name.
     * @return the descriptor's name
     */
    @Override
    public String getDescriptorName() {
        return myName;
    }

    /**
     * Gets a descriptor's property by name
     * @param key the property name
     * @return the property value
     */
    @Override
    public String getProperty(String key) {
        return myProperties.get(key);
    }

    /**
     * Gets a list of all the descriptor's properties
     * @return a set of property names
     */
    @Override
    public Set<String> getPropertyKeys() {
        return myProperties.keySet();
    }

    /**
     * Gets the name of the class associated with the descriptor
     * @return the class name
     */
    @Override
    public String getClassName() {
        return myClassName;
    }
    
}
