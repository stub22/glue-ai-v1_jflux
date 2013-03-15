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
package org.jflux.api.registry.util;

import java.util.List;
import org.jflux.api.core.Adapter;
import org.jflux.api.core.Listener;
import org.jflux.api.core.playable.PlayableNotifier;
import org.jflux.api.data.concurrent.AsyncAdapter;
import org.jflux.api.registry.Registry;

/**
 *
 * @author matt
 */


public class FinderUtils {
    /**
     * Returns an Adapter for finding a single reference.
     * @param registry the Registry
     * @return Adapter for finding a single reference
     */
    public static <D,R> R findSingle(Registry<D,R,?,?,?,?> registry, D desc) {
        final List<R> find = registry.findAll(desc);
        if(find == null || find.isEmpty()){
            return null;
        }
        return find.get(0);
    }

    /**
     * Returns an Adapter for finding all references.
     * @param registry the Registry
     * @param max the maximum number of parameters
     * @return Adapter for finding all references.
     */
    public static <D,R> List<R> findCount(
            Registry<D,R,?,?,?,?> registry, D desc, final int max) {
        final List<R> find = registry.findAll(desc);
        if(find == null || find.size() <= max){
            return find;
        }
        return find.subList(0, max);
    }

    /**
     * Returns an Adapter for finding a single reference asynchronously.
     * @param registry the Registry
     * @return Adapter for finding a single reference
     */
    public static <D,R> PlayableNotifier<R> findSingleAsync(
            final Registry<D,R,?,?,?,?> registry, D desc, Listener<R> listener){
        if(registry == null){
            return null;
        }
        PlayableNotifier<R> pn = 
                new AsyncAdapter<D,R>(new Adapter<D, R>() {
                    @Override public R adapt(D a) {
                        return findSingle(registry, a);
                    }}).adapt(desc);
        pn.addListener(listener);
        pn.start();
        return pn;
    }

    /**
     * Returns an Adapter for finding all references asynchronously.
     * @param registry the Registry
     * @return Adapter for finding all references
     */
    public static <D,R> PlayableNotifier<List<R>> findAllAsync(
            final Registry<D,R,?,?,?,?> registry, D desc, Listener<List<R>> listener){
        if(registry == null){
            return null;
        }
        PlayableNotifier<List<R>> pn = new AsyncAdapter<D,List<R>>(
                new Adapter<D, List<R>>() {
                    @Override public List<R> adapt(D a) {
                        return registry.findAll(a);
                    }}).adapt(desc);
        pn.addListener(listener);
        pn.start();
        return pn;
    }

    /**
     * Returns an Adapter for finding the number of references
     * asynchronously.
     * @param registry the Registry
     * @param max the maximum number of parameters
     * @return Adapter for finding the number of references
     */
    public static <D,R> PlayableNotifier<List<R>> findCountAsync(
            final Registry<D,R,?,?,?,?> registry, D desc, final int max, 
            Listener<List<R>> listener){
        if(registry == null){
            return null;
        }
        PlayableNotifier<List<R>> pn = new AsyncAdapter<D,List<R>>(
                new Adapter<D, List<R>>() {
                    @Override public List<R> adapt(D a) {
                        return findCount(registry, a, max);
                    }}).adapt(desc);
        pn.addListener(listener);
        pn.start();
        return pn;
    }
}
