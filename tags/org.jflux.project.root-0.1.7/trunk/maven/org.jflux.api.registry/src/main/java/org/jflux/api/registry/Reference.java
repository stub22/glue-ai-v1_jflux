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

import java.util.Set;

/**
 * A Reference points to an item in a registry and provides registration 
 * details.
 * 
 * @author Matthew Stevenson
 */
public interface Reference {
    /**
     * Gets a property of the reference by name
     * @param key the property name
     * @return the property value
     */
    public String getProperty(String key);
    /**
     * Gets all properties of the reference
     * @return a set of property names
     */
    public Set<String> getPropertyKeySet();
}
