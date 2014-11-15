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
package org.jflux.impl.services.rk.lifecycle.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import org.jflux.impl.services.rk.lifecycle.DependencyDescriptor;
import org.jflux.impl.services.rk.lifecycle.DependencyDescriptor.DependencyType;
import org.jflux.impl.services.rk.osgi.OSGiUtils;

/**
 * Builds a DependencyDescriptor.
 * 
 * @author Matthew Stevenson <www.robokind.org>
 */
public class DescriptorBuilder extends DescriptorListBuilder{
    private DescriptorListBuilder myListBuilder;
    private String myDependencyName;
    private Class myDependencyClass;
    private List<String> myFilters;
    private DependencyType myType;
    /**
     * Begins building a DependencyDescriptor with the give name and class.
     * By default the DependencyType is REQUIRED.  Call optional()
     * @param name
     * @param clazz 
     */
    public DescriptorBuilder(String name, Class clazz){
        this(new DescriptorListBuilder(), name, clazz, DependencyType.REQUIRED);
    }
    
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
        myFilters = new ArrayList<String>();
    }
    
    public DependencyDescriptor getDescriptor(){
        String filter = buildFilter();
        return new DependencyDescriptor(
                myDependencyName, myDependencyClass, filter, myType);
    }
    
    public DescriptorBuilder with(String key, String value){
        if(key == null || value == null){
            throw new NullPointerException();
        }
        myFilters.add(OSGiUtils.createFilter(key, value));
        return this;
    }
    
    public DescriptorBuilder with(String filter){
        if(filter == null){
            throw new NullPointerException();
        }
        myFilters.add(filter);
        return this;
    }
    
    public DescriptorBuilder with(Properties props){
        if(props == null){
            throw new NullPointerException();
        }
        for(Entry e : props.entrySet()){
            myFilters.add(OSGiUtils.createFilter(
                    e.getKey().toString(), e.getValue().toString()));
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
    
    private String buildFilter(){
        if(myFilters.isEmpty()){
            return null;
        }
        StringBuilder filterBuilder = new StringBuilder();
        if(myFilters.size() > 1){
            filterBuilder.append("(&");
        }
        for(String f : myFilters){
            f = f.trim();
            if(!f.startsWith("(")){
                filterBuilder.append("(");
            }
            filterBuilder.append(f);
            if(f.charAt(f.length()-1) != ')'){
                filterBuilder.append(")");
            }
        }
        if(myFilters.size() > 1){
            filterBuilder.append(")");
        }
        return filterBuilder.toString();
    }
    
    private DescriptorListBuilder finish(){
        myListBuilder.getDescriptors().add(getDescriptor());
        return myListBuilder;
    }
}
