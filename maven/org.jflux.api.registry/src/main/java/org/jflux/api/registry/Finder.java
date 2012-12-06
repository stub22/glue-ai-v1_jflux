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
    public Ref findSingle(Desc desc);
    
    /**
     * Returns an Adapter for finding all references matching a description.
     * @return Adapter for finding all references matching a description.
     */
    public List<Ref> findAll(Desc desc);
    
    /**
     * Returns an Adapter for finding all references matching a description.
     * @param max the maximum number of parameters
     * @return Adapter for finding all references matching a description.
     */
    public List<Ref> findCount(int max, Desc desc);
    
    /**
     * Returns an Adapter for finding a single reference from a description
     * asynchronously.
     * @return Adapter for finding a single reference from a description
     */
    public Adapter<Desc,PlayableNotifier<Ref>> findSingleAsync();
    
    /**
     * Returns an Adapter for finding a continuous reference from a description
     * asynchronously.
     * @return Adapter for finding a continuous reference from a description
     */
    public Adapter<Desc,PlayableNotifier<Ref>> findContinuousAsync();
    
    /**
     * Returns an Adapter for finding a continuous reference from a description
     * asynchronously.
     * @param max the maximum number of parameters
     * @return Adapter for finding a continuous reference from a description
     */
    public Adapter<Desc,PlayableNotifier<Ref>> findContinuousAsync(int max);
    
    /**
     * Returns an Adapter for finding all references matching a description
     * asynchronously.
     * @return Adapter for finding all references matching a description.
     */
    public Adapter<Desc,PlayableNotifier<List<Ref>>> findAllAsync();
    
    /**
     * Returns an Adapter for finding the number of references matching a
     * description asynchronously.
     * @param max the maximum number of parameters
     * @return Adapter for finding the number of references matching a
     * description.
     */
    public Adapter<Desc,PlayableNotifier<List<Ref>>> findCountAsync(int max);
    
    /**
     * A basic Finder backend.
     */
    public static class DefaultFinderProvider {
        /**
         * Returns an Adapter for finding a single reference.
         * @param finder the Finder frontend
         * @return Adapter for finding a single reference
         */
        public static <D,R> R findSingle(Finder<D,R> finder, D desc) {
            final List<R> find = finder.findAll(desc);
            if(find == null || find.isEmpty()){
                return null;
            }
            return find.get(0);
        }
        
        /**
         * Returns an Adapter for finding all references.
         * @param finder the Finder frontend
         * @param max the maximum number of parameters
         * @return Adapter for finding all references.
         */
        public static <D,R> List<R> findAll(
                Finder<D,R> finder, D desc) {
            final List<R> find = finder.findAll(desc);
            return find;
        }
        
        /**
         * Returns an Adapter for finding all references.
         * @param finder the Finder frontend
         * @param max the maximum number of parameters
         * @return Adapter for finding all references.
         */
        public static <D,R> List<R> findCount(
                Finder<D,R> finder, final int max, D desc) {
            final List<R> find = finder.findAll(desc);
            if(find == null || find.size() <= max){
                return find;
            }
            return find.subList(0, max);
        }

        /**
         * Returns an Adapter for finding a single reference asynchronously.
         * @param finder the Finder frontend
         * @return Adapter for finding a single reference
         */
        public static <D,R> Adapter<D,PlayableNotifier<R>> findSingleAsync(
                final Finder<D,R> finder){
            if(finder == null){
                return null;
            }
            return new AsyncAdapter<D,R>(new Adapter<D, R>() {

                        @Override
                        public R adapt(D a) {
                            return findSingle(finder, a);
                        }
                    });
        }

        /**
         * Returns an Adapter for finding a continuous reference asynchronously.
         * @param finder the Finder frontend
         * @return Adapter for finding a continuous reference
         */
        public static <D,R> Adapter<D,PlayableNotifier<R>> findContinuousAsync(
                Finder<D,R> finder){
            throw new UnsupportedOperationException("Not supported yet.");
        }

        /**
         * Returns an Adapter for finding a continuous reference asynchronously.
         * @param finder the Finder frontend
         * @param max the maximum number of parameters
         * @return Adapter for finding a continuous reference
         */
        public static <D,R> Adapter<D,PlayableNotifier<R>> findContinuousAsync(
                Finder<D,R> finder, int max){
            throw new UnsupportedOperationException("Not supported yet.");
        }

        /**
         * Returns an Adapter for finding all references asynchronously.
         * @param finder the Finder frontend
         * @return Adapter for finding all references
         */
        public static <D,R> Adapter<D,PlayableNotifier<List<R>>> findAllAsync(
                final Finder<D,R> finder){
            if(finder == null){
                return null;
            }
            return new AsyncAdapter<D,List<R>>(new Adapter<D, List<R>>() {

                        @Override
                        public List<R> adapt(D a) {
                            return findAll(finder, a);
                        }
                    });
        }

        /**
         * Returns an Adapter for finding the number of references
         * asynchronously.
         * @param finder the Finder frontend
         * @param max the maximum number of parameters
         * @return Adapter for finding the number of references
         */
        public static <D,R> Adapter<D,PlayableNotifier<List<R>>> findCountAsync(
                final Finder<D,R> finder, final int max){
            if(finder == null){
                return null;
            }
            return new AsyncAdapter<D,List<R>>(new Adapter<D, List<R>>() {

                        @Override
                        public List<R> adapt(D a) {
                            return findCount(finder, max, a);
                        }
                    });
        }
        
        
    }
}
