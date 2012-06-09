/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jflux.impl.registry.basic.opt;

import java.util.Map;
import java.util.Set;
import org.jflux.api.registry.opt.RegistrationRequest;

/**
 *
 * @author Matthew Stevenson
 */
public class BasicRegistrationRequest<T,K,V> implements 
        RegistrationRequest<T,K,V> {
    private String myName;
    private T myItem;
    private Map<K,V> myProperties;
    private Set<String> myClassNames;
    
    @Override
    public String getName() {
        return myName;
    }

    @Override
    public T getItem() {
        return myItem;
    }

    @Override
    public Map<K, V> getProperties() {
        return myProperties;
    }

    @Override
    public Set<String> getClassNames() {
        return myClassNames;
    }
    
}
