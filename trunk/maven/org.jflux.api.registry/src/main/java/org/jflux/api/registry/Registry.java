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

import org.jflux.api.core.event.Event;
import org.jflux.api.core.event.Header;
import org.jflux.api.registry.opt.Certificate;
import org.jflux.api.registry.opt.Descriptor;
import org.jflux.api.registry.opt.Modification;
import org.jflux.api.registry.opt.Reference;
import org.jflux.api.registry.opt.RegistrationRequest;
import org.jflux.api.registry.opt.RegistryContext;

/**
 * Provides access to registry components based on the given context.
 * 
 * @param <Context> Provides registry permissions
 * @param <Desc> Describes registry items
 * @param <RegReq> Requests an item be added to the registry
 * @param <ModReq> Requests a registration be modified
 * @param <RefEvt> Reference Event type used by the registry
 * @param <Ref> Reference to an item
 * @param <Cert> Certificate providing permissions for modifying or removing a 
 * registration
 * 
 * @author Matthew Stevenson
 */
public interface Registry<Context,Desc,RegReq,ModReq,Cert,Ref,RefEvt> {
    /**
     * Returns a registry Finder matching the context permissions.
     * @param context
     * @return Finder matching the context permissions
     */
    public Finder<Desc,Ref> getFinder(Context context);
    /**
     * Returns a registry Monitor matching the context permissions.
     * @param context
     * @return
     */
    public Monitor<Desc,RefEvt> getMonitor(Context context);
    /**
     * Returns a registry Accessor matching the context permissions.
     * @param context
     * @return
     */
    public Accessor<RegReq,Cert,ModReq> getAccessor(Context context);
    /**
     * Returns a typed registry Retriever matching the context permissions.
     * @param <T> Item type retrieved
     * @param context
     * @param clazz item class
     * @return typed registry Retriever matching the context permissions
     */
    public <T> Retriever<Ref,T> getRetriever(Context context, Class<T> clazz);
    /**
     * Returns an untyped registry Retriever matching the context permissions.
     * @param context
     * @return untyped registry Retriever matching the context permissions
     */
    public Retriever<Ref,Object> getRetriever(Context context);
    
    /**
     * Registry using the message interfaces from org.jflux.api.regstry.opt.
     * @param <Time> Timestamp type
     * @param <CxtK> Registry context property key
     * @param <CxtV> Registry context property value
     * @param <K> property key
     * @param <V> property value
     */
    public static interface BasicRegistry<CxtK,CxtV,Time,K,V> extends Registry<
            RegistryContext<BasicRegistry<CxtK,CxtV,Time,K,V>,CxtK,CxtV>,
            Descriptor<K,V>,
            RegistrationRequest<?,K,V>,
            Modification<Certificate<Reference<K,V>>,K,V>,
            Certificate<Reference<K,V>>,
            Reference<K,V>,
            Event<Header<BasicRegistry<CxtK,CxtV,Time,K,V>,Time>,Reference<K,V>>>{
    }
}
