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

import java.util.ArrayList;
import java.util.List;
import org.jflux.api.services.DependencyDescriptor;
import org.jflux.api.services.DependencyDescriptor.DependencyType;

/**
 * Convenience class for building a List of DependencyDescriptors.
 * Methods for adding descriptors return the DescriptorListBuilder to allow for
 * method chaining.
 * e.x.:
 *      List&gt;DependencyDescriptor&lt; descs = 
 *          new DescriptorListBuilder().add(...).addId(...).getDescriptors();
 * @author Matthew Stevenson <www.robokind.org>
 */
public class DescriptorListBuilder{
    private List<DependencyDescriptor> myDependencies;
    
    /**
     * Creates an empty DescriptorListBuilder.
     */
    public DescriptorListBuilder(){}
    
    /**
     * Creates a new DescriptorListBuilder with the given descriptors.
     * @param descriptors initial DependencyDescriptor list
     */
    public DescriptorListBuilder(List<DependencyDescriptor> descriptors){
        if(myDependencies == null){
            myDependencies = descriptors;
        }else if(descriptors != null && !descriptors.isEmpty()){
            myDependencies.addAll(descriptors);
        }
    }
    
    /**
     * Adds a new DependencyDescriptor with the given name and class
     * @param name dependency name used within the ManagedService
     * @param clazz dependency class
     * @return context for describing the dependency further or adding other
     * dependencies
     */
    public DescriptorBuilder dependency(String name, Class clazz){
        if(myDependencies == null){
            myDependencies = new ArrayList<DependencyDescriptor>();
        }
        return new DescriptorBuilder(
                this, name, clazz, DependencyType.REQUIRED);
    }
    /**
     * Returns the list of DependencyDescriptors added to this list builder.
     * @return list of DependencyDescriptors added to this list builder
     */
    public List<DependencyDescriptor> getDescriptors(){
        if(myDependencies == null){
            myDependencies = new ArrayList<DependencyDescriptor>();
        }
        return myDependencies;
    }
}
