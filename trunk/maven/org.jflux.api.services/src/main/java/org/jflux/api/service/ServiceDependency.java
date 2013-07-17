/*
 * Copyright 2013 The JFlux Project (www.jflux.org).
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
package org.jflux.api.service;

import java.util.Map;

/**
 *
 * @author matt
 */
public class ServiceDependency {    
    private final String myDependencyName;
    private final String myDependencyClassName;
    private final Cardinality myCardinality;
    private final UpdateStrategy myUpdateStrategy;
    private Map<String,String> myDependencyProperties;
    
    /**
     * Create a new required DependencyDescriptor with the given values.
     * @param dependencyName dependency name within a ServiceLifecycleProvider
     * @param className Class name of the dependency
     * @param props Properties of the dependency
     */
    public ServiceDependency(String dependencyName, String className, 
            Cardinality cardinality, UpdateStrategy updateStrategy, 
            Map<String,String> props){
        if(dependencyName == null || className == null 
                || cardinality == null || updateStrategy == null){
            throw new NullPointerException();
        }
        myDependencyName = dependencyName;
        myDependencyClassName = className;
        myCardinality = cardinality;
        myUpdateStrategy = updateStrategy;
        myDependencyProperties = props;
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
    public String getDependencyClassName(){
        return myDependencyClassName;
    }

    public Cardinality getCardinality(){
        return myCardinality;
    }
    
    public UpdateStrategy getUpdateStrategy(){
        return myUpdateStrategy;
    }
    
    public Map<String,String> getProperties() {
        return myDependencyProperties;
    }
    
    public static enum Cardinality {
        OPTIONAL_UNARY(false, false),
        MANDATORY_UNARY(true, false),
        OPTIONAL_MULTIPLE(false, true),
        MANDATORY_MULTIPLE(true, true);
        
        private boolean myRequiredFlag;
        private boolean myMultiplicityFlag;
        
        private Cardinality(boolean required, boolean multiple){
            myRequiredFlag = required;
            myMultiplicityFlag = multiple;
        }
        
        public boolean isRequired(){
            return myRequiredFlag;
        }
        
        public boolean isMultiple(){
            return myMultiplicityFlag;
        }
    }
    
    public static enum UpdateStrategy{
        STATIC, DYNAMIC
    }
}
