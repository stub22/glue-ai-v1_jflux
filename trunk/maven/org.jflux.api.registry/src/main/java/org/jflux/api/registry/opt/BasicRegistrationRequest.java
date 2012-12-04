/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jflux.api.registry.opt;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Basic implementation of RegistrationRequest
 * @author Matthew Stevenson
 */
public class BasicRegistrationRequest<T,K,V> implements 
        RegistrationRequest<T,K,V> {
    private String myName;
    private T myItem;
    private Map<K,V> myProperties;
    private Set<String> myClassNames;
    
    /**
     * Creates a new registration request for multiple class names
     * @param name the name of the service
     * @param item the service
     * @param properties the service's properties
     * @param classNames the service's class names
     */
    public BasicRegistrationRequest(
            String name, T item, Map<K,V> properties, Set<String> classNames) {
        if(item == null || classNames == null || classNames.isEmpty()) {
            throw new NullPointerException();
        }
        myName = name;
        myItem = item;
        myProperties = properties;
        myClassNames = classNames;
    }
    
    /**
     * Creates a new registration request for a single class name
     * @param name the name of the service
     * @param item the service
     * @param properties the service's properties
     * @param className the service's class name
     */
    public BasicRegistrationRequest(
            String name, T item, Map<K,V> properties, String className) {
        if(item == null || className == null || className.isEmpty()) {
            throw new NullPointerException();
        }
        myName = name;
        myItem = item;
        myProperties = properties;
        myClassNames = new HashSet<String>();
        myClassNames.add(className);
    }
    
    /**
     * Gets the service's name.
     * @return the service's name
     */
    @Override
    public String getName() {
        return myName;
    }

    /**
     * Gets the service
     * @return the service
     */
    @Override
    public T getItem() {
        return myItem;
    }

    /**
     * Gets the service's properties
     * @return the service's properties
     */
    @Override
    public Map<K, V> getProperties() {
        return myProperties;
    }

    /**
     * Gets the service's class names
     * @return the service's class names
     */
    @Override
    public Set<String> getClassNames() {
        return myClassNames;
    }
    
}
