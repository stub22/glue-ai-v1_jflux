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
import org.jflux.api.core.event.Event;
import org.jflux.api.core.event.Header;
import org.jflux.api.registry.opt.Certificate;
import org.jflux.api.registry.opt.Descriptor;
import org.jflux.api.registry.opt.Modification;
import org.jflux.api.registry.opt.Reference;
import org.jflux.api.registry.opt.RegistrationRequest;

/**
 * Provides access to registry components based on the given context.
 * 
 * @param <Context> Provides registry permissions
 * @author Matthew Stevenson
 */
public interface Registry<Desc,Ref,Req,Cert,ModReq, Evt>{
    /**
     * Returns the first reference matching the given description.  
     * Returns null if no matching reference is found.
     * @param desc
     * @return first reference matching the given description.
     */
    public Ref findSingle(Desc desc);
    
    /**
     * Returns all references matching the given description.  
     * Returns null if no matching reference is found.
     * @param desc
     * @return all references matching the given description.
     */
    public List<Ref> findAll(Desc desc);
    
    /**
     * Returns a list of references matching the given description whose size 
     * is less than or equal to max.  
     * Returns null if no matching reference is found.
     * @param desc
     * @param max
     * @return list of references matching the given description.
     */
    public List<Ref> findCount( Desc desc, int max);
    
    
    
    /**
     * Returns an Adapter for retrieving an item.
     * @param clazz the item's class
     * @return Adapter for retrieving an item
     */
    public <T> T retrieve(Class<T> clazz, Ref reference);
    /**
     * Returns an Adapter for retrieving an untyped item.
     * @return Adapter for retrieving an untyped item
     */
    public Object retrieve(Ref reference);
    /**
     * Returns a Listener for releasing an item which was retrieved.
     * @return Listener for releasing an item which was retrieved
     */
    public void release(Ref reference);
    
    
    
    /**
     * Returns an Adapter for registering items.
     * @return Adapter for registering items
     */
    public Cert register(Req request);
    /**
     * Returns a Listener for unregistering items.
     * @return Listener for unregistering an items
     */
    public void unregister(Cert cert);
    /**
     * Returns an Adapter for modifying a registration.
     * @return Adapter for modifying a registration
     */
    public void modify(Cert cert, ModReq request);
    
    
    
    /**
     * Adds a Listener to be notified of events matching the description
     * @param desc event filter
     * @param listener the listener to be notified
     */
    public void addListener(Desc desc, Listener<Evt> listener);
    /**
     * Removes a Listener from being notified of any events
     * @param listener the listener to be removed
     */
    public void removeListener(Listener<Evt> listener);
    
    /**
     * A basic kind of Registry
     */
    public static interface BasicRegistry<Time,K,V> extends Registry<
            Descriptor<K,V>,
            Reference<K,V>,
            RegistrationRequest<?,K,V>,
            Certificate<Reference<K,V>>,
            Modification<K,V>,
            Event<Header<? extends BasicRegistry<Time,K,V>,Time>,Reference<K,V>>> {
    }
}
