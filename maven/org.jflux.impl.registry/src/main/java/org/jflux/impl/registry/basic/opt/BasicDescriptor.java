/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jflux.impl.registry.basic.opt;

import java.util.Map;
import java.util.Set;
import org.jflux.api.registry.opt.Descriptor;

/**
 *
 * @author Matthew Stevenson
 */
public class BasicDescriptor<K,V> implements Descriptor<K, V> {
    private String myName;
    private Map<K,V> myProperties;
    private String myClassName;
    
    @Override
    public String getName() {
        return myName;
    }

    @Override
    public V getProperty(K key) {
        return myProperties.get(key);
    }

    @Override
    public Set<K> getPropertyKeys() {
        return myProperties.keySet();
    }

    @Override
    public String getClassName() {
        return myClassName;
    }
    
}
