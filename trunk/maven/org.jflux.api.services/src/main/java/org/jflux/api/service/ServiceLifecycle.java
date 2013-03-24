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

import java.util.List;
import java.util.Map;

/**
 *
 * @author Matthew Stevenson
 */
public interface ServiceLifecycle<T> {
    public final static int ADDED = 0;
    public final static int REMOVED = 1;
    public final static int CHANGED = 2;
    /**
     * Returns a list describing the service dependencies required by this 
     * lifecycle provider.
     * @return list describing the service dependencies required by this 
     * lifecycle provider
     */
    public List<DependencySpec> getDependencyDescriptors();
    /**
     * 
     * @param dependencyMap
     * @return 
     */
    public T createService(Map<String,Object> dependencyMap);
    /**
     * 
     * @param service
     * @param dependencyName
     * @param dependency
     * @param availableDependencies
     * @return 
     */
    public T handleDependencyChange(T service, int changeType,
            String dependencyName, Object dependency, 
            Map<String,Object> availableDependencies);
    /**
     * 
     * @param service
     * @param availableDependencies 
     */
    public void disposeService(T service, Map<String,Object> availableDependencies);
    
    /**
     * Returns the names of the interfaces this service implements and makes
     * available.
     * @return names of the interfaces this service implements and makes
     * available
     */
    public String[] getServiceClassNames();
}
