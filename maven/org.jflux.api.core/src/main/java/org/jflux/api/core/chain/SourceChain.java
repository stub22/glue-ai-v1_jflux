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
import org.jflux.api.core.Source;
import org.jflux.api.core.chain.AdapterChain.AdapterChainBuilder;

/**
 * Source that runs through one or more Adapters
 * @author Matthew Stevenson
 * @param <A> initial data type
 * @param <B> output data type
 */
public class SourceChain<A,B> implements Source<B> {

    /**
     * origin Source
     */
    protected Source<A> myInnerSource;

    /**
     * internal Adapter
     */
    protected Adapter<A,B> myAdapter;
        
    /**
     * Builds a SourceChain from an origin Source and a single Adapter
     * @param <T> origin data type
     * @param source origin Source
     * @param adapter intermediate Adapter
     */
    public <T> SourceChain(Source<? extends T> source, Adapter<T,B> adapter) {
        if(source == null || adapter == null){
            throw new NullPointerException();
        }
        if(source instanceof SourceChain){
            SourceChain<A,T> sc = (SourceChain)source;
            myInnerSource = sc.myInnerSource;
            myAdapter = new AdapterChain<A, B>(sc.myAdapter, adapter);
        }else{
            myInnerSource = (Source<A>)source;
            myAdapter = (Adapter<A,B>)adapter;
        }
    }

    /**
     * Get the Adapter-processed output of the origin Source
     * @return
     */
    @Override
    public B getValue() {
        A a = myInnerSource.getValue();
        return myAdapter.adapt(a);
    }
    
    /**
     * Creates a new SourceChainBuilder from an origin Source
     * @param <T> origin data type
     * @param source the origin Source
     * @return new SourceChainBuilder
     */
    public static <T> SourceChainBuilder<T,T> builder(Source<T> source){
        return new SourceChainBuilder<T,T>().setSource(source);
    }

    /**
     * Creates an empty SourceChainBuilder
     * @param <T> data type
     * @return empty SourceChainBuilder
     */
    public static <T> SourceChainBuilder<T,T> builder(){
        return new SourceChainBuilder<T,T>();
    }
    
    /**
     * Utility class to build a SourceChain from components
     * @param <X> origin data type
     * @param <Y> output data type
     */
    public static class SourceChainBuilder<X,Y> {
        private Source<X> mySource;
        private AdapterChainBuilder<X,Y> myAdapters;
        
        /**
         * Create an empty SourceChainBuilder
         */
        public SourceChainBuilder(){
            myAdapters = new AdapterChainBuilder<X, Y>();
        }
        
        /**
         * Set the origin Source
         * @param source origin Source
         * @return the SourceChainBuilder itself
         */
        public SourceChainBuilder<X,Y> setSource(Source<X> source){
            mySource = source;
            return this;
        }

        /**
         * Add an Adapter
         * @param <T> intermediate data type
         * @param adapter Adapter to add
         * @return the SourceChainBuilder itself
         */
        public <T> SourceChainBuilder<X,T> attach(Adapter<Y,T> adapter){
            myAdapters.attach(adapter);
            return (SourceChainBuilder<X,T>)this;
        }

        /**
         * Generate a SourceChain
         * @param <T> intermediate data type
         * @return new SourceChain
         */
        public <T> SourceChain<T,Y> done(){
            return new SourceChain<T,Y>(mySource, myAdapters.done());
        }
    }
}
