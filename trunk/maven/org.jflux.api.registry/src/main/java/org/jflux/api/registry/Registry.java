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
import org.jflux.api.core.playable.PlayableNotifier;
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
 * @author Matthew Stevenson
 */
public interface Registry<
        Context,
        F extends Finder, 
        A extends Accessor, 
        R extends Retriever,
        M extends Monitor> {
    /**
     * Returns a registry Finder matching the context permissions.
     * @param context
     * @return Finder matching the context permissions
     */
    public F getFinder(Context context);
    /**
     * Returns a registry Monitor matching the context permissions.
     * @param context
     * @return Monitor matching the context permissions
     */
    public M getMonitor(Context context);
    /**
     * Returns a registry Accessor matching the context permissions.
     * @param context
     * @return
     */
    public A getAccessor(Context context);
    /**
     * Returns a registry Retriever matching the context permissions.
     * @param context
     * @return registry Retriever matching the context permissions
     */
    public R getRetriever(Context context);
    
    /**
     * Registry using the message interfaces from org.jflux.api.regstry.opt.
     * @param <Time> Timestamp type
     * @param <CxtK> Registry context property key
     * @param <CxtV> Registry context property value
     * @param <K> property key
     * @param <V> property value
     */
    public static interface RegistryTemplate<CxtK, CxtV, Time, K, V,
            Cxt extends RegistryContext<
                    ? extends Registry<Cxt,F,A,R,M>,CxtK,CxtV>,
            Desc extends Descriptor<K,V>,
            Ref extends Reference<K,V>,
            Req extends RegistrationRequest<?,K,V>,
            Cert extends Certificate<Ref>,
            ModReq extends Modification<Cert,K,V>,
            RefEvt extends Event<
                    ? extends Header<? extends Registry,Time>,Ref>,
            N extends PlayableNotifier<RefEvt>,
            F extends Finder<Desc,Ref>,
            A extends Accessor<Req,Cert,ModReq>, 
            R extends Retriever<Ref>,
            M extends Monitor<Desc,RefEvt,N>> extends Registry<Cxt,F,A,R,M>{
    }
    
    /**
     * A basic kind of Registry
     */
    public static interface BasicRegistry<CxtK,CxtV,Time,K,V> extends Registry<
            RegistryContext<BasicRegistry<CxtK,CxtV,Time,K,V>,CxtK,CxtV>,
            Finder<Descriptor<K,V>,Reference<K,V>>,
            Accessor<
                    RegistrationRequest<?,K,V>,
                    Certificate<Reference<K,V>>,
                    Modification<Certificate<Reference<K,V>>,K,V>>,
            Retriever<Reference<K,V>>,
            Monitor<Descriptor<K,V>,
                    Event<
                        Header<? extends BasicRegistry<CxtK,CxtV,Time,K,V>,Time>, 
                        Reference<K,V>>,
                    PlayableNotifier<Event<
                        Header<? extends BasicRegistry<CxtK,CxtV,Time,K,V>,Time>, 
                        Reference<K,V>>>>> {
    }
}
