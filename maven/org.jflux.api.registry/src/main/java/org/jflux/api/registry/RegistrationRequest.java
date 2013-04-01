/*
 * Copyright 2012 The JFlux Project (www.jflux.org).
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
package org.jflux.api.registry;

import java.util.Map;
import java.util.Set;

/**
 * Provides details for adding an item to a Registry.
 * 
 * @author Matthew Stevenson
 */
public interface RegistrationRequest<T> {
//    /**
//     * Gets the service's name.
//     * @return the service's name
//     */
//    public String getName();
    /**
     * Gets the service.
     * @return the service
     */
    public T getItem();
    /**
     * Gets the service's properties
     * @return the service's properties
     */
    public Map<String,String> getProperties();
    /**
     * Gets the class names the service will be registered under
     * @return a set of class names
     */
    public String[] getClassNames();
}
