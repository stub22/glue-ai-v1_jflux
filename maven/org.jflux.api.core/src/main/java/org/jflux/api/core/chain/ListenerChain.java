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
    protected Listener<B> myOutputListener;
    
    public ListenerChain(Adapter<A,B> adapter, Listener<B> listener) {
        if(adapter == null || listener == null){
            throw new NullPointerException();
        }
        if(listener instanceof ListenerChain){
            ListenerChain lc = (ListenerChain)listener;
            myOutputListener = lc.myOutputListener;
            myAdapter = new AdapterChain(adapter, lc.myAdapter);            
        }else{
            myOutputListener = listener;
            myAdapter = adapter;
        }
    }

    @Override
    public void handleEvent(A input) {
        B b = myAdapter.adapt(input);
        myOutputListener.handleEvent(b);
    }

    public static <A,B> ListenerChainBuilder<A,B> builder(Adapter<A,B> adapter){
        return new ListenerChainBuilder<A,A>().attach(adapter);
    }

    public static <A,B> ListenerChainBuilder<A,B> builder(){
        return new ListenerChainBuilder<A,B>();
    }
    
    public static class ListenerChainBuilder<A,B> {
        private AdapterChainBuilder<A,B> myAdapters;
        
        public ListenerChainBuilder(){
            myAdapters = new AdapterChainBuilder<A, B>();
        }

        public <N> ListenerChainBuilder<A,N> attach(Adapter<B,N> adapter){
            myAdapters.attach(adapter);
            return (ListenerChainBuilder<A,N>)this;
        }

        public ListenerChain<A,B> done(Listener<B> listener){
            return new ListenerChain<A,B>(myAdapters.done(), listener);
        }
    }
}
