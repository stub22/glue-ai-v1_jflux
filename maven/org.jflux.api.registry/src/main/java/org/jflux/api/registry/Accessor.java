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

import org.jflux.api.core.Adapter;
import org.jflux.api.core.Listener;

/**
 * Provides registration access for a Registry.
 * 
 * @param <Req> Requests an item be registered
 * @param <ModReq> Requests a registration be modified
 * @param <Cert> Provides permissions for accessing a registration
 * @author Matthew Stevenson
 */
public interface Accessor<Req,Cert,ModReq> {
    /**
     * Returns an Adapter for registering items.
     * @return Adapter for registering items
     */
    public Adapter<Req,Cert> register();
    /**
     * Returns a Listener for unregistering items.
     * @return Listener for unregistering an items
     */
    public Listener<Cert> unregister();
    /**
     * Returns an Adapter for modifying a registration.
     * @return Adapter for modifying a registration
     */
    public Adapter<ModReq,Cert> modify();
}
