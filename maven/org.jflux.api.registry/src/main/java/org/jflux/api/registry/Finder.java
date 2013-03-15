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
import org.jflux.api.core.Listener;
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
//    
//    /**
//     * Asynchronously finds the first reference matching the description and 
//     * notifies the listener.  If no reference is found, null is passed to the 
//     * listener
//     * @param desc 
//     * @param listener 
//     */
//    public void findSingleAsync(Desc desc, Listener<Ref> listener);
//    
//    /**
//     * Asynchronously finds all references matching the description and 
//     * notifies the listener.  If no reference is found, null is passed to the 
//     * listener
//     * @param desc 
//     * @param listener 
//     */
//    public void findAllAsync(Desc desc, Listener<List<Ref>> listener);
//    
//    /**
//     * Asynchronously finds all references matching the description up to max 
//     * and notifies the listener.  If no reference is found, null is passed to 
//     * the listener
//     * @param desc 
//     * @param listener 
//     */
//    public void findCountAsync(Desc desc, int max, Listener<List<Ref>> listener);
//    
//    /**
//     * All asynchronous actions associated with the given listener are stopped.
//     * @param listener 
//     */
//    public void removeAsyncListener(Listener listener);
//    
    /**
     * A basic Finder backend requiring a finder with only findAll.
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
        public static <D,R> List<R> findCount(
                Finder<D,R> finder, D desc, final int max) {
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
        public static <D,R> PlayableNotifier<R> findSingleAsync(
                final Finder<D,R> finder, D desc, Listener<R> listener){
            if(finder == null){
                return null;
            }
            PlayableNotifier<R> pn = 
                    new AsyncAdapter<D,R>(new Adapter<D, R>() {
                        @Override public R adapt(D a) {
                            return findSingle(finder, a);
                        }}).adapt(desc);
            pn.addListener(listener);
            pn.start();
            return pn;
        }
        
        /**
         * Returns an Adapter for finding all references asynchronously.
         * @param finder the Finder frontend
         * @return Adapter for finding all references
         */
        public static <D,R> PlayableNotifier<List<R>> findAllAsync(
                final Finder<D,R> finder, D desc, Listener<List<R>> listener){
            if(finder == null){
                return null;
            }
            PlayableNotifier<List<R>> pn = new AsyncAdapter<D,List<R>>(
                    new Adapter<D, List<R>>() {
                        @Override public List<R> adapt(D a) {
                            return finder.findAll(a);
                        }}).adapt(desc);
            pn.addListener(listener);
            pn.start();
            return pn;
        }

        /**
         * Returns an Adapter for finding the number of references
         * asynchronously.
         * @param finder the Finder frontend
         * @param max the maximum number of parameters
         * @return Adapter for finding the number of references
         */
        public static <D,R> PlayableNotifier<List<R>> findCountAsync(
                final Finder<D,R> finder, D desc, final int max, 
                Listener<List<R>> listener){
            if(finder == null){
                return null;
            }
            PlayableNotifier<List<R>> pn = new AsyncAdapter<D,List<R>>(
                    new Adapter<D, List<R>>() {
                        @Override public List<R> adapt(D a) {
                            return findCount(finder, a, max);
                        }}).adapt(desc);
            pn.addListener(listener);
            pn.start();
            return pn;
        }
    }
}
