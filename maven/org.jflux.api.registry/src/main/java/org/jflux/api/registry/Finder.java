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
import org.jflux.api.core.Adapter;
import org.jflux.api.core.Notifier;

/**
 * Finds references from a registry.
 * 
 * @param <Desc> Describes the reference to find
 * @param <Ref> Reference type returned
 * 
 * @author Matthew Stevenson
 */
public interface Finder<Desc,Ref> {
    /**
     * Returns an Adapter for finding a single reference from a description.
     * @return Adapter for finding a single reference from a description
     */
    public Adapter<Desc,Ref> findSingle();
    
    /**
     * Returns an Adapter for finding all references matching a description.
     * @return Adapter for finding all references matching a description.
     */
    public Adapter<Desc,List<Ref>> findAll();
    
    /**
     * Returns an Adapter for finding all references matching a description.
     * @return Adapter for finding all references matching a description.
     */
    public Adapter<Desc,List<Ref>> findCount(int max);
    
    public Adapter<Desc,Notifier<Ref>> findSingleAsync();
    
    public Adapter<Desc,Notifier<Ref>> findContinuousAsync();
    
    public Adapter<Desc,Notifier<Ref>> findContinuousAsync(int max);
    
    public Adapter<Desc,Notifier<List<Ref>>> findAllAsync();
    
    public Adapter<Desc,Notifier<List<Ref>>> findCountAsync(int max);
}
