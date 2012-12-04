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

import org.jflux.api.registry.Registry;

/**
 * Provides user information and access rights for working with a Registry.
 * 
 * @author Matthew Stevenson
 */
public interface RegistryContext<R extends Registry,K,V> {
    /**
     * Get the context's registry.
     * @return the registry
     */
    public R getRegistry();
    /**
     * Get a property of the context by name.
     * @param key the property name
     * @return the property value
     */
    public V getProperty(K key);
}
