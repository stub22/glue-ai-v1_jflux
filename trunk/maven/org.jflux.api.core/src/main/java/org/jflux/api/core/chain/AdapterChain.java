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
 *
 * @author Matthew Stevenson
 */
public class AdapterChain<A,B> implements Adapter<A,B> {
    protected List<Adapter> myAdapters;
    
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
    
    @Override
    public B adapt(A a) {
        Object o = a;
        for(Adapter adapter : myAdapters){
            o = adapter.adapt(o);
        }
        return (B)o;
    }

    public static <T,S> AdapterChainBuilder<T,S> builder(Adapter<T,S> adapter){
        return new AdapterChainBuilder<T,S>(adapter);
    }
    
    public static class AdapterChainBuilder<X,Y> {
        private List<Adapter> myAdapterList;

        public AdapterChainBuilder(Adapter<X,Y> adapter){
            if(adapter == null){
                throw new NullPointerException();
            }
            myAdapterList = new ArrayList<Adapter>();
            myAdapterList.add(adapter);
        }

        public AdapterChainBuilder(){
            myAdapterList = new ArrayList<Adapter>();
        }

        public <T> AdapterChainBuilder<X,T> attach(Adapter<Y,T> adapter){
            if(adapter == null){
                throw new NullPointerException();
            }
            myAdapterList.add(adapter);
            return (AdapterChainBuilder<X,T>)this;
        }

        public Adapter<X,Y> done(){
            return new AdapterChain<X, Y>(myAdapterList);
        }
    }
}
