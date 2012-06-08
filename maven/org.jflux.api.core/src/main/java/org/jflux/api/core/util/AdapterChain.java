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
 *
 * @author Matthew Stevenson
 */
public class AdapterChain<H,T> implements Adapter<H,T> {
    private List<Adapter> myAdapters;

    public static <A,B> AdapterChainBuilder<A,B> build(Adapter<A,B> adapter){
        return new AdapterChainBuilder<A,B>(adapter);
    }

    public static <A,B> AdapterChain<A,B> buildUnsafe(List<Adapter> adapters){
        return new AdapterChain<A, B>(adapters);
    }

    public static <A,B,C> AdapterChain<A,C> combine(Adapter<A,B> x, Adapter<B,C> y){
        return new AdapterChain<A, C>(collapse(x,y));
    }
    
    private static List<Adapter> collapse(Adapter...l){
        return collapse(Arrays.asList(l));
    }
    private static List<Adapter> collapse(List<Adapter> l){
        List<Adapter> n = new ArrayList<Adapter>();
        for(Adapter a : l){
            if(a == null){
                continue;
            }else if(a instanceof AdapterChain){
                n.addAll(collapse(((AdapterChain)a).myAdapters));
            }else{
                n.add(a);
            }
        }
        return n;
    }
    
    private AdapterChain(List<Adapter> adapters){
        myAdapters = adapters;
    }
    
    @Override
    public T adapt(H a) {
        if(myAdapters.isEmpty()){
            return null;
        }else if(myAdapters.size() == 1){
            return adaptTail(a);
        }
        Object o = a;
        for(Adapter adapter : myAdapters.subList(0, myAdapters.size()-1)){
            o = adapter.adapt(o);
        }
        return adaptTail(o);
    }
    
    private <V> T adaptTail(V v){
        Adapter<V,T> aa = myAdapters.get(myAdapters.size());
        return aa.adapt(v);
    }
    
    public static class AdapterChainBuilder<H,T> {
        private List<Adapter> myAdapterList;

        private AdapterChainBuilder(Adapter<H,T> adapter){
            if(adapter == null){
                throw new NullPointerException();
            }
            myAdapterList = new ArrayList<Adapter>();
            myAdapterList.add(adapter);
        }

        public <N> AdapterChainBuilder<H,N> attach(Adapter<T,N> adapter){
            if(adapter == null){
                throw new NullPointerException();
            }
            myAdapterList.add(adapter);
            return (AdapterChainBuilder<H,N>)this;
        }

        public Adapter<H,T> done(){
            return new AdapterChain<H, T>(myAdapterList);
        }
    }
}
