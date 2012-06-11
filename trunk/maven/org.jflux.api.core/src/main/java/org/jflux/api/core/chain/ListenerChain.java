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
 *
 * @author Matthew Stevenson
 */
public class ListenerChain<A,B> implements Listener<A> {
    protected Adapter<A,B> myAdapter;
    protected Listener<B> myInnerListener;
    
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

    @Override
    public void handleEvent(A input) {
        B b = myAdapter.adapt(input);
        myInnerListener.handleEvent(b);
    }

    public static <T,S> ListenerChainBuilder<T,S> builder(Adapter<T,S> adapter){
        return new ListenerChainBuilder<T,T>().attach(adapter);
    }

    public static <T> ListenerChainBuilder<T,T> builder(){
        return new ListenerChainBuilder<T,T>();
    }
    
    public static class ListenerChainBuilder<X,Y> {
        private AdapterChainBuilder<X,Y> myAdapters;
        
        public ListenerChainBuilder(){
            myAdapters = new AdapterChainBuilder<X, Y>();
        }

        public <T> ListenerChainBuilder<X,T> attach(Adapter<Y,T> adapter){
            myAdapters.attach(adapter);
            return (ListenerChainBuilder<X,T>)this;
        }

        public ListenerChain<X,Y> done(Listener<Y> listener){
            return new ListenerChain<X,Y>(myAdapters.done(), listener);
        }
    }
}
