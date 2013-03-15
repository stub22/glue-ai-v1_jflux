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
package org.jflux.api.registry.opt;

import java.util.Set;

/**
 * A Reference points to an item in a registry and provides registration 
 * details.
 * 
 * @author Matthew Stevenson
 */
public interface Reference<K,V> {
    /**
     * Gets the reference's name
     * @return the reference's name
     */
    public String getName();
    /**
     * Gets a property of the reference by name
     * @param key the property name
     * @return the property value
     */
    public V getProperty(K key);
    /**
     * Gets all properties of the reference
     * @return a set of property names
     */
    public Set<K> getPropertyKeySet();
}
