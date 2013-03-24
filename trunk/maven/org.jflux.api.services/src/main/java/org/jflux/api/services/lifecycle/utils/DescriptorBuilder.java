/*
 * Copyright 2012 Hanson Robokind LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jflux.api.services.lifecycle.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import org.jflux.api.services.dep.DependencyDescriptor;
import org.jflux.api.services.dep.DependencyDescriptor.DependencyType;

/**
 * Builds a DependencyDescriptor.
 * 
 * @author Matthew Stevenson <www.robokind.org>
 */
public class DescriptorBuilder extends DescriptorListBuilder{
    private DescriptorListBuilder myListBuilder;
    private String myDependencyName;
    private Class myDependencyClass;
    private Map<String,String> myFilterProps;
    private DependencyType myType;
    /**
     * Begins building a DependencyDescriptor with the given name and class.
     * By default the DependencyType is REQUIRED.  Call optional()
     * @param name the name of the dependency.
     * @param clazz the class associated with the dependency.
     */
    public DescriptorBuilder(String name, Class clazz){
        this(new DescriptorListBuilder(), name, clazz, DependencyType.REQUIRED);
    }

    /**
     * Begins building a DependencyDescriptor with the given name and class,
     * descriptor list builder, and dependency type.
     * @param listBuilder a descriptor list builder
     * @param name the name of the dependency.
     * @param clazz the class associated with the dependency.
     * @param type the dependency type (REQUIRED or OPTIONAL)
     */
    public DescriptorBuilder(DescriptorListBuilder listBuilder, 
            String name, Class clazz, DependencyType type){
        if(listBuilder == null || name == null || clazz == null){
            throw new NullPointerException();
        }
        if(type == null){
            type = DependencyType.REQUIRED;
        }
        myListBuilder = listBuilder;
        myDependencyName = name;
        myDependencyClass = clazz;
        myType = type;
        myFilterProps = new HashMap<String, String>();
    }
    
    /**
     * Gives the dependency descriptor.
     * @return the dependency descriptor
     */
    public DependencyDescriptor getDescriptor(){
        return new DependencyDescriptor(
                myDependencyName, myDependencyClass, myFilterProps, myType);
    }
    
    /**
     * Add a property to the dependency descriptor.
     * @param key the property's name
     * @param value the property's value
     * @return this
     */
    public DescriptorBuilder with(String key, String value){
        if(key == null || value == null){
            throw new NullPointerException();
        }
        myFilterProps.put(key, value);
        return this;
    }
    
    /**
     * Add a set of properties to the dependency descriptor.
     * @param props the properties
     * @return this
     */
    public DescriptorBuilder with(Properties props){
        if(props == null){
            throw new NullPointerException();
        }
        for(Entry e : props.entrySet()){
            myFilterProps.put(e.getKey().toString(), e.getValue().toString());
        }
        return this;
    }
    /**
     * Sets the dependency type to OPTIONAL.
     * @return this
     */
    public DescriptorBuilder optional(){
        myType = DependencyType.OPTIONAL;
        return this;
    }
    /**
     * Sets the dependency type to REQUIRED.
     * @return this
     */
    public DescriptorBuilder required(){
        myType = DependencyType.REQUIRED;
        return this;
    }
    
    @Override
    public DescriptorBuilder dependency(String name, Class clazz){
        return finish().dependency(name, clazz);
    }
    
    @Override
    public List<DependencyDescriptor> getDescriptors(){
        return finish().getDescriptors();
    }
    
    private DescriptorListBuilder finish(){
        myListBuilder.getDescriptors().add(getDescriptor());
        return myListBuilder;
    }
}
