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
package org.jflux.api.services.lifecycle;

/**
 * Describes a service dependency of a ServiceLifecycleProvider.  Used to match
 * find and track the appropriate services needed by a lifecycle provider.
 * 
 * @author Matthew Stevenson <www.robokind.org>
 */
public final class DependencyDescriptor{
    public static enum DependencyType{
        REQUIRED, OPTIONAL
    }
    
    private String myDependencyName;
    private Class myDependencyClass;
    private String myDependencyFilter;
    private DependencyType myType;
    
    /**
     * Create a new required DependencyDescriptor with the given values.
     * @param dependencyName dependency name within a ServiceLifecycleProvider
     * @param clazz Class of the dependency
     * @param filter optional OSGi filter string
     */
    public DependencyDescriptor(String dependencyName, 
            Class clazz, String filter){
        this(dependencyName, clazz, filter, DependencyType.REQUIRED);
    }
    
    /**
     * Create a new DependencyDescriptor with the given values.
     * @param dependencyName dependency name within a ServiceLifecycleProvider
     * @param clazz Class of the dependency
     * @param filter optional OSGi filter string
     * @param type is this dependency required or optional
     */
    public DependencyDescriptor(String dependencyName, 
            Class clazz, String filter, DependencyType type){
        if(dependencyName == null || clazz == null){
            throw new NullPointerException();
        }else if(type == null){
            type = DependencyType.REQUIRED;
        }
        myDependencyName = dependencyName;
        myDependencyClass = clazz;
        myDependencyFilter = filter;
        myType = type;
    }

    /**
     * Returns the dependency name used within a ServiceLifecycleProvider.
     * @return dependency name used within a ServiceLifecycleProvider
     */
    public String getDependencyName(){
        return myDependencyName;
    }
    /**
     * Returns the Class of dependency.
     * @return Class of dependency
     */
    public Class getServiceClass(){
        return myDependencyClass;
    }

    /**
     * Returns an OSGi filter string for the dependency, null if it is
     * not set.
     * @return OSGi filter string for the dependency, null if it is not set
     */
    public String getServiceFilter(){
        return myDependencyFilter;
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
        if ((this.myDependencyFilter == null) ? (other.myDependencyFilter != null) : !this.myDependencyFilter.equals(other.myDependencyFilter)) {
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
        hash = 19 * hash + (this.myDependencyFilter != null ? this.myDependencyFilter.hashCode() : 0);
        hash = 19 * hash + (this.myType != null ? this.myType.hashCode() : 0);
        return hash;
    }
    
    
}
