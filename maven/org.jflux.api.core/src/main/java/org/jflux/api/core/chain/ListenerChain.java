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
 * A Listener filtered through an Adapter.
 * @param <A> input type
 * @param <B> adaptation type
 * @author Matthew Stevenson
 */
public class ListenerChain<A,B> implements Listener<A> {
    /**
     * The internal adapter.
     */
    protected Adapter<A,B> myAdapter;
    /**
     * The final Listener.
     */
    protected Listener<B> myInnerListener;
    
    /**
     * Builds a ListenerChain from an Adapter and a Listener.
     * @param adapter the adapter
     * @param listener the final listener
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
     * Gets the final listener.
     * @return the final listener
     */
    public Listener<B> getInnerListener(){
        return myInnerListener;
    }
    
    /**
     * Gets the internal adapter.
     * @return the internal adapter
     */
    public Adapter<A,B> getInnerAdapter(){
        return myAdapter;
    }

    /**
     * Processes input through the internal adapter and feeds it to the final listener.
     * @param input the input
     */
    @Override
    public void handleEvent(A input) {
        B b = myAdapter.adapt(input);
        myInnerListener.handleEvent(b);
    }

    /**
     * Starts a ListenerChainBuilder with an Adapter.
     * @param <T> adapter input type
     * @param <S> adapter output type
     * @param adapter starting adapter
     * @return new ListenerChainBuilder
     */
    public static <T,S> ListenerChainBuilder<T,S> builder(Adapter<T,S> adapter){
        return new ListenerChainBuilder<T,T>().attach(adapter);
    }

    /**
     * Starts a ListenerChainBuilder
     * @param <T> data type
     * @return empty ListenerChainBuilder
     */
    public static <T> ListenerChainBuilder<T,T> builder(){
        return new ListenerChainBuilder<T,T>();
    }
    
    /**
     * Class to build a ListenerChain from components.
     * @param <X> input type
     * @param <Y> adaptation type
     */
    public static class ListenerChainBuilder<X,Y> {
        private AdapterChainBuilder<X,Y> myAdapters;
        
        /**
         * Create an empty ListenerChainBuilder.
         */
        public ListenerChainBuilder(){
            myAdapters = new AdapterChainBuilder<X, Y>();
        }

        /**
         * Add an Adapter to the internal AdapterChain.
         * @param <T> adapter output type
         * @param adapter new adapter
         * @return the ListenerChainBuilder itself
         */
        public <T> ListenerChainBuilder<X,T> attach(Adapter<Y,T> adapter){
            myAdapters.attach(adapter);
            return (ListenerChainBuilder<X,T>)this;
        }

        /**
         * Constructs the final ListenerChain
         * @param listener the final listener
         * @return the final ListenerChain
         */
        public ListenerChain<X,Y> done(Listener<Y> listener){
            return new ListenerChain<X,Y>(myAdapters.done(), listener);
        }
    }
}
