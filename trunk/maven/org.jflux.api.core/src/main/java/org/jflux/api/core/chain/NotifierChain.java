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
public class NotifierChain<A,B> extends DefaultNotifier<B> {
    protected Notifier<A> myInnerNotifier;
    protected ListenerChain<A,B> myListenerChain;
        
    public <C> NotifierChain(Notifier<C> inputNotifier, Adapter<C,B> adapter) {
        if(inputNotifier == null || adapter == null){
            throw new NullPointerException();
        }
        if(inputNotifier instanceof NotifierChain){
            NotifierChain<A,C> notifier = (NotifierChain)inputNotifier;
            Adapter<A,C> innerAdapter = notifier.myListenerChain.myAdapter;
            myListenerChain = new ListenerChain<A,B>(
                    new AdapterChain<A,B>(innerAdapter, adapter), 
                    new OutputListener());
            myInnerNotifier = notifier.myInnerNotifier;
            myInnerNotifier.addListener(myListenerChain);
        }else{
            myInnerNotifier = (Notifier<A>)inputNotifier;
            myListenerChain = new ListenerChain(adapter, new OutputListener());
            myInnerNotifier.addListener(myListenerChain);
        }
    }
    
    class OutputListener implements Listener<B> {
        @Override
        public void handleEvent(B b) {
            notifyListeners(b);
        }
    }

    public static <T> NotifierChainBuilder<T,T> builder(Notifier<T> notifier){
        return new NotifierChainBuilder<T,T>().setNotifier(notifier);
    }

    public static <T> NotifierChainBuilder<T,T> builder(){
        return new NotifierChainBuilder<T,T>();
    }
    
    public static class NotifierChainBuilder<X,Y> {
        private Notifier<X> myNotifier;
        private AdapterChainBuilder<X,Y> myAdapters;
        
        public NotifierChainBuilder(){
            myAdapters = new AdapterChainBuilder<X, Y>();
        }
        
        public NotifierChainBuilder<X,Y> setNotifier(Notifier<X> notifier){
            myNotifier = notifier;
            return this;
        }

        public <T> NotifierChainBuilder<X,T> attach(Adapter<Y,T> adapter){
            myAdapters.attach(adapter);
            return (NotifierChainBuilder<X,T>)this;
        }

        public <T> NotifierChain<T,Y> done(){
            return new NotifierChain<T,Y>(myNotifier, myAdapters.done());
        }
    }
}
