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

import java.util.List;
import org.jflux.api.core.Listener;

/**
 * Provides access to registry components based on the given context.
 * 
 * @param <Context> Provides registry permissions
 * @author Matthew Stevenson
 */
public interface Registry {
    /**
     * Returns the first reference matching the given description.  
     * Returns null if no matching reference is found.
     * @param desc
     * @return first reference matching the given description.
     */
    public Reference findSingle(Descriptor desc);
    
    /**
     * Returns all references matching the given description.  
     * Returns null if no matching reference is found.
     * @param desc
     * @return all references matching the given description.
     */
    public List<Reference> findAll(Descriptor desc);
    
    /**
     * Returns a list of references matching the given description whose size 
     * is less than or equal to max.  
     * Returns null if no matching reference is found.
     * @param desc
     * @param max
     * @return list of references matching the given description.
     */
    public List<Reference> findCount(Descriptor desc, int max);
    
    
    /**
     * Returns an Adapter for retrieving an item.
     * @param clazz the item's class
     * @return Adapter for retrieving an item
     */
    public <T> T retrieve(Class<T> clazz, Reference reference);
    /**
     * Returns an Adapter for retrieving an untyped item.
     * @return Adapter for retrieving an untyped item
     */
    public Object retrieve(Reference reference);
    /**
     * Returns a Listener for releasing an item which was retrieved.
     * @return Listener for releasing an item which was retrieved
     */
    public void release(Reference reference);
    
    
    /**
     * Returns an Adapter for registering items.
     * @return Adapter for registering items
     */
    public Certificate register(RegistrationRequest<?> request);
    /**
     * Returns a Listener for unregistering items.
     * @return Listener for unregistering an items
     */
    public void unregister(Certificate cert);
    /**
     * Returns an Adapter for modifying a registration.
     * @return Adapter for modifying a registration
     */
    public void modify(Certificate cert, Modification request);
    
    
    /**
     * Adds a Listener to be notified of events matching the description
     * @param desc event filter
     * @param listener the listener to be notified
     */
    public void addListener(Descriptor desc, Listener<RegistryEvent> listener);
    /**
     * Removes a Listener from being notified of any events
     * @param listener the listener to be removed
     */
    public void removeListener(Listener<RegistryEvent> listener);
}
