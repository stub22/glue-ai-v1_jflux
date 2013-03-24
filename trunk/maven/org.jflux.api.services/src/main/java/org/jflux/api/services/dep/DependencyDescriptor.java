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
package org.jflux.api.services.dep;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.jflux.api.registry.Descriptor;

/**
 * Describes a service dependency of a ServiceLifecycleProvider.  Used to match
 * find and track the appropriate services needed by a lifecycle provider.
 * 
 * @author Matthew Stevenson <www.robokind.org>
 */
public final class DependencyDescriptor implements Descriptor {
    public static enum DependencyType{
        REQUIRED, OPTIONAL
    }
    
    private String myDependencyName;
    private Class myDependencyClass;
    private Map<String,String> myDependencyProperties;
    private DependencyType myType;
    
    /**
     * Create a new required DependencyDescriptor with the given values.
     * @param dependencyName dependency name within a ServiceLifecycleProvider
     * @param clazz Class of the dependency
     * @param props Properties of the dependency
     */
    public DependencyDescriptor(String dependencyName, 
            Class clazz, Map<String,String> props){
        this(dependencyName, clazz, props, DependencyType.REQUIRED);
    }
    
    /**
     * Create a new DependencyDescriptor with the given values.
     * @param dependencyName dependency name within a ServiceLifecycleProvider
     * @param clazz Class of the dependency
     * @param props Properties of the dependency
     * @param type is this dependency required or optional
     */
    public DependencyDescriptor(String dependencyName, 
            Class clazz, Map<String,String> props, DependencyType type){
        if(dependencyName == null || clazz == null){
            throw new NullPointerException();
        }else if(type == null){
            type = DependencyType.REQUIRED;
        }
        myDependencyName = dependencyName;
        myDependencyClass = clazz;
        myDependencyProperties = props;
        myType = type;
    }

    /**
     * Returns the dependency name used within a ServiceLifecycleProvider.
     * @return dependency name used within a ServiceLifecycleProvider
     */
    @Override
    public String getDescriptorName(){
        return myDependencyName;
    }
    /**
     * Returns the Class of dependency.
     * @return Class of dependency
     */
    public Class getServiceClass(){
        return myDependencyClass;
    }

    @Override
    public String getProperty(String key) {
        if(myDependencyProperties == null){
            return null;
        }
        return myDependencyProperties.get(key);
    }

    @Override
    public Set<String> getPropertyKeys() {
        if(myDependencyProperties == null){
            return Collections.EMPTY_SET;
        }
        return myDependencyProperties.keySet();
    }

    @Override
    public String getClassName() {
        return myDependencyClass.getName();
    }
    
    /**
     * Returns the dependency type, Required or Optional
     * @return dependency type, Required or Optional
     */
    public DependencyType getDependencyType(){
        return myType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DependencyDescriptor other = (DependencyDescriptor) obj;
        if ((this.myDependencyName == null) ? (other.myDependencyName != null) : !this.myDependencyName.equals(other.myDependencyName)) {
            return false;
        }
        if (this.myDependencyClass != other.myDependencyClass && (this.myDependencyClass == null || !this.myDependencyClass.equals(other.myDependencyClass))) {
            return false;
        }
        if (this.myType != other.myType) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + (this.myDependencyName != null ? this.myDependencyName.hashCode() : 0);
        hash = 19 * hash + (this.myDependencyClass != null ? this.myDependencyClass.hashCode() : 0);
        hash = 19 * hash + (this.myType != null ? this.myType.hashCode() : 0);
        return hash;
    }
}
