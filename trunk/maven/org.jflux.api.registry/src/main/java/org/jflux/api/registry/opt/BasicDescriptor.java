/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jflux.api.registry.opt;

import java.util.Map;
import java.util.Set;

/**
 * A basic implementation of Descriptor
 * @author Matthew Stevenson
 */
public class BasicDescriptor<K,V> implements Descriptor<K, V> {
    private String myName;
    private Map<K,V> myProperties;
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
    public V getProperty(K key) {
        return myProperties.get(key);
    }

    /**
     * Gets a list of all the descriptor's properties
     * @return a set of property names
     */
    @Override
    public Set<K> getPropertyKeys() {
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
