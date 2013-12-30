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
 *
 * @author Matthew Stevenson
 */
public class SourceChain<A,B> implements Source<B> {
    protected Source<A> myInnerSource;
    protected Adapter<A,B> myAdapter;
        
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

    @Override
    public B getValue() {
        A a = myInnerSource.getValue();
        return myAdapter.adapt(a);
    }
    
    public static <T> SourceChainBuilder<T,T> builder(Source<T> source){
        return new SourceChainBuilder<T,T>().setSource(source);
    }

    public static <T> SourceChainBuilder<T,T> builder(){
        return new SourceChainBuilder<T,T>();
    }
    
    public static class SourceChainBuilder<X,Y> {
        private Source<X> mySource;
        private AdapterChainBuilder<X,Y> myAdapters;
        
        public SourceChainBuilder(){
            myAdapters = new AdapterChainBuilder<X, Y>();
        }
        
        public SourceChainBuilder<X,Y> setSource(Source<X> source){
            mySource = source;
            return this;
        }

        public <T> SourceChainBuilder<X,T> attach(Adapter<Y,T> adapter){
            myAdapters.attach(adapter);
            return (SourceChainBuilder<X,T>)this;
        }

        public <T> SourceChain<T,Y> done(){
            return new SourceChain<T,Y>(mySource, myAdapters.done());
        }
    }
}
