/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jflux.api.registry.basic;

import java.util.Collections;
import org.jflux.api.registry.Descriptor;
import java.util.Map;
import java.util.Set;

/**
 * A basic implementation of Descriptor
 * @author Matthew Stevenson
 */
public class BasicDescriptor implements Descriptor {
//    private String myName;
    private Map<String,String> myProperties;
    private String myClassName;
    
    public BasicDescriptor(
//            String name, 
            String className, Map<String,String> properties){
        if(className == null){
            throw new NullPointerException();
        }
//        myName = name;
        myClassName = className;
        myProperties = properties;
    }
//    
//    /**
//     * Gets the descriptor's name.
//     * @return the descriptor's name
//     */
//    @Override
//    public String getDescriptorName() {
//        return myName;
//    }

    /**
     * Gets a descriptor's property by name
     * @param key the property name
     * @return the property value
     */
    @Override
    public String getProperty(String key) {
        if(myProperties == null){
            return null;
        }
        return myProperties.get(key);
    }

    /**
     * Gets a list of all the descriptor's properties
     * @return a set of property names
     */
    @Override
    public Set<String> getPropertyKeys() {
        if(myProperties == null){
            return Collections.EMPTY_SET;
        }
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
