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
import org.jflux.api.core.Notifier;
import org.jflux.api.core.chain.AdapterChain.AdapterChainBuilder;
import org.jflux.api.core.util.DefaultNotifier;

/**
 *
 * @author Matthew Stevenson
 */
public class NotifierChain<T> extends DefaultNotifier<T> {
    protected Notifier myInputNotifier;
    protected ListenerChain<?,T> myListenerChain;
        
    public <A> NotifierChain(Notifier<A> inputNotifier, Adapter<A,T> adapter) {
        if(inputNotifier == null || adapter == null){
            throw new NullPointerException();
        }
        if(inputNotifier instanceof NotifierChain){
            NotifierChain notifier = (NotifierChain)inputNotifier;
            Adapter innerAdapter = notifier.myListenerChain.myAdapter;
            adapter = new AdapterChain(innerAdapter, adapter);
            myInputNotifier = notifier.myInputNotifier;
        }else{
            myInputNotifier = inputNotifier;
        }
        myListenerChain = new ListenerChain<A, T>(adapter, new OutputListener());
        myInputNotifier.addListener(myListenerChain);
    }
    
    class OutputListener implements Listener<T> {
        @Override
        public void handleEvent(T b) {
            notifyListeners(b);
        }
    }

    public static <A> NotifierChainBuilder<A,A> builder(Notifier<A> notifier){
        return new NotifierChainBuilder<A,A>().setNotifier(notifier);
    }

    public static <A> NotifierChainBuilder<A,A> builder(){
        return new NotifierChainBuilder<A,A>();
    }
    
    public static class NotifierChainBuilder<A,B> {
        private Notifier<A> myNotifier;
        private AdapterChainBuilder<A,B> myAdapters;
        
        public NotifierChainBuilder(){
            myAdapters = new AdapterChainBuilder<A, B>();
        }
        
        public NotifierChainBuilder<A,B> setNotifier(Notifier<A> notifier){
            myNotifier = notifier;
            return this;
        }

        public <N> NotifierChainBuilder<A,N> attach(Adapter<B,N> adapter){
            myAdapters.attach(adapter);
            return (NotifierChainBuilder<A,N>)this;
        }

        public NotifierChain<B> done(){
            return new NotifierChain<B>(myNotifier, myAdapters.done());
        }
    }
}
