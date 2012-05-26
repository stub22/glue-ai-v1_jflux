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
import org.jflux.api.core.playable.PlayableNotifier;
import org.jflux.api.data.concurrent.AsyncAdapter;

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
    
    public Adapter<Desc,PlayableNotifier<Ref>> findSingleAsync();
    
    public Adapter<Desc,PlayableNotifier<Ref>> findContinuousAsync();
    
    public Adapter<Desc,PlayableNotifier<Ref>> findContinuousAsync(int max);
    
    public Adapter<Desc,PlayableNotifier<List<Ref>>> findAllAsync();
    
    public Adapter<Desc,PlayableNotifier<List<Ref>>> findCountAsync(int max);
    
    public static class DefaultFinderProvider {
        public static <D,R> Adapter<D,R> findSingle(Finder<D,R> finder) {
            final Adapter<D, List<R>> find = finder.findAll();

            return new Adapter<D,R>() {
                @Override
                public R adapt(D a) {
                    List<R> list = find.adapt(a);
                    if(list == null || list.isEmpty()){
                        return null;
                    }
                    return list.get(0);
                }
            };
        }
        
        public static <D,R> Adapter<D,List<R>> findCount(
                Finder<D,R> finder, final int max) {
            
            final Adapter<D, List<R>> find = finder.findAll();

            return new Adapter<D, List<R>>() {
                @Override
                public List<R> adapt(D a) {
                    List<R> list = find.adapt(a);
                    if(list == null || list.size() <= max){
                        return list;
                    }
                    return list.subList(0, max);
                }
            };
        }

        public static <D,R> Adapter<D,PlayableNotifier<R>> findSingleAsync(
                Finder<D,R> finder){
            Adapter<D,R> f = finder.findSingle();
            if(f == null){
                return null;
            }
            return new AsyncAdapter<D,R>(f);
        }

        public static <D,R> Adapter<D,PlayableNotifier<R>> findContinuousAsync(
                Finder<D,R> finder){
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public static <D,R> Adapter<D,PlayableNotifier<R>> findContinuousAsync(
                Finder<D,R> finder, int max){
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public static <D,R> Adapter<D,PlayableNotifier<List<R>>> findAllAsync(
                Finder<D,R> finder){
            Adapter<D,List<R>> f = finder.findAll();
            if(f == null){
                return null;
            }
            return new AsyncAdapter<D,List<R>>(f);
        }

        public static <D,R> Adapter<D,PlayableNotifier<List<R>>> findCountAsync(
                Finder<D,R> finder, int max){
            Adapter<D,List<R>> f = finder.findCount(max);
            if(f == null){
                return null;
            }
            return new AsyncAdapter<D,List<R>>(f);
        }
        
        
    }
}
