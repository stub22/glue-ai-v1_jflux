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
import java.util.Arrays;
import java.util.List;
import org.jflux.api.core.Adapter;

/**
 * Adapter to convert an item to a List
 * @author Matthew Stevenson
 * @param <T> input data type
 */
public class ListAdapter<T> implements Adapter<T, List<T>> {
    private int myCapacity;
    
    /**
     * Builds a ListAdapter with capacity 10
     */
    public ListAdapter(){
        myCapacity = 10; //ArrayList default capacity
    }
    
    /**
     * Builds a ListAdapter with specified capacity
     * @param capacity
     */
    public ListAdapter(int capacity){
        if(capacity < 1){
            throw new IllegalArgumentException("Capacity must be greater than zero.");
        }
        myCapacity = capacity;
    }
    
    /**
     * Converts an item to a list
     * @param a input item
     * @return List containing item
     */
    @Override
    public List<T> adapt(T a) {
        List<T> l = new ArrayList<T>(myCapacity);
        l.add(a);
        Arrays.asList(a);
        return l;
    }    
}
