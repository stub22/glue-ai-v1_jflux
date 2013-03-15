/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jflux.api.registry.opt;

import java.util.Map;

/**
 * Basic implementation of Modification
 * @author Matthew Stevenson
 */
public class BasicModification<K, V> implements 
        Modification<K, V> {
    private Map<K,V> myProperties;
    
    /**
     * Gets the properties to be modified
     * @return the properties
     */
    @Override
    public Map<K, V> getProperties() {
        return myProperties;
    }
    
}
