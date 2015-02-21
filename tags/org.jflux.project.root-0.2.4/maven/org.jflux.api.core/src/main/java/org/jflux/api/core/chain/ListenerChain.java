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

import org.jflux.api.core.Adapter;
import org.jflux.api.core.Listener;
import org.jflux.api.core.chain.AdapterChain.AdapterChainBuilder;

/**
 * Chain connecting a Listener up to (an) Adapter(s)
 * @author Matthew Stevenson
 * @param <A> input data type
 * @param <B> final data type
 */
public class ListenerChain<A,B> implements Listener<A> {

    /**
     * The intermediate Adapter
     */
    protected Adapter<A,B> myAdapter;

    /**
     * The real Listener
     */
    protected Listener<B> myInnerListener;
    
    /**
     * Builds a new ListenerChain from an Adapter and a Listener
     * @param adapter the Adapter
     * @param listener the Listener
     */
    public ListenerChain(Adapter<A,B> adapter, Listener<B> listener) {
        if(adapter == null || listener == null){
            throw new NullPointerException();
        }
        if(listener instanceof ListenerChain){
            ListenerChain lc = (ListenerChain)listener;
            myInnerListener = lc.myInnerListener;
            myAdapter = new AdapterChain(adapter, lc.myAdapter);            
        }else{
            myInnerListener = listener;
            myAdapter = adapter;
        }
    }
    
    /**
     * Get the internal Listener
     * @return the internal Listener
     */
    public Listener<B> getInnerListener(){
        return myInnerListener;
    }
    
    /**
     * Get the intermediate Adapter
     * @return the intermediate Adapter
     */
    public Adapter<A,B> getInnerAdapter(){
        return myAdapter;
    }

    /**
     * Runs data through the ListenerChain
     * @param input the data
     */
    @Override
    public void handleEvent(A input) {
        B b = myAdapter.adapt(input);
        myInnerListener.handleEvent(b);
    }

    /**
     * Create a ListenerChainBuilder from an Adapter
     * @param <T> input data type
     * @param <S> final data type
     * @param adapter the Adapter
     * @return new ListenerChainBuilder
     */
    public static <T,S> ListenerChainBuilder<T,S> builder(Adapter<T,S> adapter){
        return new ListenerChainBuilder<T,T>().attach(adapter);
    }

    /**
     * Create an empty ListenerChainBuilder
     * @param <T> input data type
     * @return empty ListenerChainBuilder
     */
    public static <T> ListenerChainBuilder<T,T> builder(){
        return new ListenerChainBuilder<T,T>();
    }
    
    /**
     * Utility class to generate a ListenerChain from Adapters
     * @param <X> input data type
     * @param <Y> final data type
     */
    public static class ListenerChainBuilder<X,Y> {
        private AdapterChainBuilder<X,Y> myAdapters;
        
        /**
         * Create an empty ListenerChainBuilder
         */
        public ListenerChainBuilder(){
            myAdapters = new AdapterChainBuilder<X, Y>();
        }

        /**
         * Add an Adapter to the ListenerChainBuilder
         * @param <T> intermediate data type
         * @param adapter Adapter to add
         * @return the ListenerChainBuilder itself
         */
        public <T> ListenerChainBuilder<X,T> attach(Adapter<Y,T> adapter){
            myAdapters.attach(adapter);
            return (ListenerChainBuilder<X,T>)this;
        }

        /**
         * Cap off the ListenerChain with a Listener
         * @param listener Listener to add
         * @return new ListenerChain
         */
        public ListenerChain<X,Y> done(Listener<Y> listener){
            return new ListenerChain<X,Y>(myAdapters.done(), listener);
        }
    }
}
