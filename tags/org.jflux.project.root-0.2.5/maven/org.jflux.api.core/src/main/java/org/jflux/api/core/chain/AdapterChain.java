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
package org.jflux.api.core.chain;

import java.util.ArrayList;
import java.util.List;
import org.jflux.api.core.Adapter;

/**
 * Chains multiple Adapters together
 * @author Matthew Stevenson
 * @param <A> input data type
 * @param <B> output data type
 */
public class AdapterChain<A,B> implements Adapter<A,B> {
    private List<Adapter> myAdapters;
    
    /**
     * Builds an AdapterChain from a pair of Adapters
     * @param <T> intermediate data type
     * @param a starting Adapter
     * @param b ending Adapter
     */
    public <T> AdapterChain(Adapter<A,T> a, Adapter<T,B> b){
        myAdapters = new ArrayList<Adapter>();
        if(a instanceof AdapterChain){
            myAdapters.addAll(((AdapterChain)a).myAdapters);
        }else{
            myAdapters.add(a);
        }
        if(b instanceof AdapterChain){
            myAdapters.addAll(((AdapterChain)b).myAdapters);
        }else{
            myAdapters.add(b);
        }
    }
    
    private AdapterChain(List<Adapter> adapters){
        myAdapters = adapters;
    }
    
    /**
     * Get the internal Adapters
     * @return List of Adapters
     */
    public List<Adapter> getAdapters(){
        return myAdapters;
    }
    
    /**
     * Adapts data throughout the chain
     * @param a input data
     * @return output data
     */
    @Override
    public B adapt(A a) {
        Object o = a;
        for(Adapter adapter : myAdapters){
            o = adapter.adapt(o);
        }
        return (B)o;
    }

    /**
     * Created an AdapterChainBuilder from a single Adapter
     * @param <T> input data type
     * @param <S> output data type
     * @param adapter initial Adapter
     * @return new AdapterChainBuilder
     */
    public static <T,S> AdapterChainBuilder<T,S> builder(Adapter<T,S> adapter){
        return new AdapterChainBuilder<T,S>(adapter);
    }
    
    /**
     * Utility class to build an AdapterChain from any number of Adapters
     * @param <X> input data type
     * @param <Y> output data type
     */
    public static class AdapterChainBuilder<X,Y> {
        private List<Adapter> myAdapterList;

        /**
         * Generate a new AdapterChainBuilder from a single Adapter
         * @param adapter
         */
        public AdapterChainBuilder(Adapter<X,Y> adapter){
            if(adapter == null){
                throw new NullPointerException();
            }
            myAdapterList = new ArrayList<Adapter>();
            myAdapterList.add(adapter);
        }

        /**
         * Generate a new AdapterChainBuilder with no Adapters
         */
        public AdapterChainBuilder(){
            myAdapterList = new ArrayList<Adapter>();
        }

        /**
         * Add an Adapter to the AdapterChainBuilder
         * @param <T> intermediate data type
         * @param adapter Adapter to add
         * @return the AdapterChainBuilder itself
         */
        public <T> AdapterChainBuilder<X,T> attach(Adapter<Y,T> adapter){
            if(adapter == null){
                throw new NullPointerException();
            }
            myAdapterList.add(adapter);
            return (AdapterChainBuilder<X,T>)this;
        }

        /**
         * Generate an AdapterChain
         * @return new AdapterChain
         */
        public Adapter<X,Y> done(){
            return new AdapterChain<X, Y>(myAdapterList);
        }
    }
}
