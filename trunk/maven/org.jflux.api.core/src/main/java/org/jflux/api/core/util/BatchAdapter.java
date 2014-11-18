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
package org.jflux.api.core.util;

import java.util.ArrayList;
import java.util.List;
import org.jflux.api.core.Adapter;

/**
 * Converts a collection of items per an internal Adapter
 * @author Matthew Stevenson
 * @param <A> input data type
 * @param <B> output data type
 */
public class BatchAdapter<A,B> implements Adapter<Iterable<A>,List<B>> {
    private Adapter<A,B> myAdapter;
    
    /**
     * Creates a new BatchAdapter with an internal Adapter
     * @param adapter
     */
    public BatchAdapter(Adapter<A,B> adapter){
        if(adapter == null){
            throw new NullPointerException();
        }
        myAdapter = adapter;
    }
    
    /**
     * Processes a collection of items
     * @param a collection of items
     * @return List of processed items
     */
    @Override
    public List<B> adapt(Iterable<A> a) {
        if(a == null){
            throw new NullPointerException();
        }
        List<B> list = new ArrayList<B>();
        for(A item : a){
            B b = myAdapter.adapt(item);
            list.add(b);
        }
        return list;
    }    
}
