/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jflux.impl.registry.basic.opt;

import java.util.Map;
import org.jflux.api.registry.opt.Modification;

/**
 *
 * @author Matthew Stevenson
 */
public class BasicModification<Cert, K, V> implements 
        Modification<Cert, K, V> {
    private Cert myCertificate;
    private Map<K,V> myProperties;
    
    @Override
    public Cert getCertificate() {
        return myCertificate;
    }

    @Override
    public Map<K, V> getProperties() {
        return myProperties;
    }
    
}
