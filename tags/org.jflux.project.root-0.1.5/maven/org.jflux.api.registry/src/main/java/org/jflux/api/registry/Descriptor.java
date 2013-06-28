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
 * A Descriptor describes one or more items in the registry.
 * Descriptors are used to retrieve references and filter reference events.
 * @author Matthew Stevenson <www.jflux.org>
 */
public interface Descriptor {
    /**
     * Gets one of the descriptor's properties by name
     * @param key the property name
     * @return the property value
     */
    public String getProperty(String key);
    /**
     * Gets all of the descriptor's properties
     * @return a set of property names
     */
    public Set<String> getPropertyKeys();
    /**
     * Gets the name of the class associated with the descriptor
     * @return the class name
     */
    public String getClassName();
}
