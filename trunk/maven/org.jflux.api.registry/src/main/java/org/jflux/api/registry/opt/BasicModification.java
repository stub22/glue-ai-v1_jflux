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
public class BasicModification<Cert, K, V> implements 
        Modification<Cert, K, V> {
    private Cert myCertificate;
    private Map<K,V> myProperties;
    
    /**
     * Gets the Certificate associated with the modification
     * @return the Certificate
     */
    @Override
    public Cert getCertificate() {
        return myCertificate;
    }

    /**
     * Gets the properties to be modified
     * @return the properties
     */
    @Override
    public Map<K, V> getProperties() {
        return myProperties;
    }
    
}
