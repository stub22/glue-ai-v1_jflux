/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jflux.api.registry.basic;

import org.jflux.api.registry.Modification;
import java.util.Map;

/**
 * Basic implementation of Modification
 * @author Matthew Stevenson
 */
public class BasicModification implements Modification {
    private Map<String,String> myProperties;
    
    /**
     * Gets the properties to be modified
     * @return the properties
     */
    @Override
    public Map<String, String> getProperties() {
        return myProperties;
    }
    
}
