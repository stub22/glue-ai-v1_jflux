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
package org.jflux.api.service.binding;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.jflux.api.service.ServiceDependency;

/**
 * The DependencyValidator is used validate a dependencyId and dependency against a
 * list of Descriptors.
 * 
 * @author matt
 */
public class DependencyValidator {
    private final static Logger theLogger = 
            Logger.getLogger(DependencyValidator.class.getName());
    /**
     * Validates a map of dependency ids and services to validate against a 
     * list of Descriptors.  A map is valid if it has a dependency 
     * matching each descriptor in the list.  A descriptor is matched if
     * there is an entry where the key and the dependency's class matches
     * the descriptor's fields.
     * @param descriptors list of Descriptors needed to be matched
     * @param depdendencies map of ids and dependencies
     * @return true if there is a dependency in the map for each descriptor,
     * false if one or more descriptors is not filled
     */
    public static boolean validateServices(
            List<ServiceDependency> descriptors, Map<String,Object> depdendencies){
        if(descriptors == null || descriptors.isEmpty()){
            return true;
        }else if(depdendencies == null || depdendencies.isEmpty()){
//            for(Descriptor desc : descriptors){
//                if(Descriptor.DependencyType.REQUIRED == desc.getDependencyType()){
//                    return false;
//                }
//            }
//            return true;
            return false;
        }
        for(ServiceDependency descriptor : descriptors){
            if(!checkDescriptor(descriptor, depdendencies)){
                return false;
            }
        }
        return true;
    }
    /**
     * Validates a Descriptor against a map of dependencies.  A 
     * descriptor is valid if there is an entry where the key and the 
     * dependency's class matches the descriptor's fields.  If there is no
     * dependency for the name, the descriptor is only valid if it is
     * optional.
     * If the dependecy's class does not match the descriptor, it is 
     * invalid.
     * @param descriptor descriptor to check for
     * @param dependencies map of ids and dependencies to check against
     * @return true is a map entry matches the descriptor, false otherwise
     */
    private static boolean checkDescriptor(
            ServiceDependency descriptor, 
            Map<String,Object> dependencies){
        String id = descriptor.getDependencyName();
        Object req = dependencies.get(id);
        if(req == null){
//            return Descriptor.DependencyType.OPTIONAL == 
//                    descriptor.getDependencyType();
            return false;
        }
        return checkClass(descriptor.getDependencyClassName(), req);
    }

    /**
     * Validates an id and dependency against a list of 
     * Descriptors.  The id and dependency are valid if there is a
     * descriptor with a matching dependency id and class.
     * @param descriptors list of Descriptors to match against
     * @param dependencyId id for the dependency
     * @param dependency dependency to validate
     * @return true if a matching descriptor is found
     */
    public static boolean validateService(
            List<ServiceDependency> descriptors,
            String dependencyId, Object dependency){
        if(dependencyId == null || dependency == null){
            theLogger.warning("Found null argument.  Returning false.");
            return false;
        }
        for(ServiceDependency descriptor : descriptors){
            if(dependencyId.equals(descriptor.getDependencyName()) 
                    && checkClass(descriptor.getDependencyClassName(), dependency)){
                return true;
            }
        }
        return false;
    }
    
    private static boolean checkClass(String className, Object obj){
        if(className == null || obj == null){
            return false;
        }
        Class c = getClassFromName(className);
        return c == null ? false : obj.getClass().isAssignableFrom(c);
    }
    
    private static Class getClassFromName(String className){
        try{
            return ClassLoader.getSystemClassLoader().loadClass(className);
        }catch(ClassNotFoundException ex){
            return null;
        }
    }

    /**
     * Returns true if there is a Descriptor with the given
     * dependency id.
     * @param descriptors list of Descriptors to search
     * @param dependencyId dependency id to search for
     * @return true if there is a Descriptor with the given
     * dependency id
     */
    public static boolean validateServiceId(
            List<ServiceDependency> descriptors, String dependencyId){
        if(dependencyId == null){
            theLogger.warning("Found null dependencyId.  Returning false.");
            return false;
        }
        for(ServiceDependency descriptor : descriptors){
            String id = descriptor.getDependencyName();
            if(dependencyId.equals(id)){
                return true;
            }
        }
        return false;
    }
}