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
 * Chains a Notifier to one or more Adapters
 * @author Matthew Stevenson
 * @param <A> input data type
 * @param <B> output data type
 */
public class NotifierChain<A,B> extends DefaultNotifier<B> {

    /**
     * Internal notifier
     */
    protected Notifier<A> myInnerNotifier;

    /**
     * ListenerChain containing Adapters
     */
    protected ListenerChain<A,B> myListenerChain;
        
    /**
     * Builds a NotifierChain from a Notifier and an Adapter
     * @param <C> data type
     * @param inputNotifier internal Notifier
     * @param adapter Adapter to process data
     */
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
    
    /**
     * Get the internal Notifier
     * @return the internal Notifier
     */
    public Notifier<A> getInnerNotifier(){
        return myInnerNotifier;
    }
    
    /**
     * Get the internal Adapter
     * @return the internal Adapter
     */
    public Adapter<A,B> getInnerAdapter(){
        return myListenerChain.getInnerAdapter();
    }
    
    class OutputListener implements Listener<B> {
        @Override
        public void handleEvent(B b) {
            notifyListeners(b);
        }
    }

    /**
     * Creates a NotifierChainBuilder from a Notifier
     * @param <T> data type
     * @param notifier base Notifier
     * @return new NotifierChainBuilder
     */
    public static <T> NotifierChainBuilder<T,T> builder(Notifier<T> notifier){
        return new NotifierChainBuilder<T,T>().setNotifier(notifier);
    }

    /**
     * Creates an empty NotifierChainBuilder
     * @param <T> data type
     * @return empty NotifierChainBuilder
     */
    public static <T> NotifierChainBuilder<T,T> builder(){
        return new NotifierChainBuilder<T,T>();
    }
    
    /**
     * Utility class to build a NotifierChain from components
     * @param <X> input data type
     * @param <Y> output data type
     */
    public static class NotifierChainBuilder<X,Y> {
        private Notifier<X> myNotifier;
        private AdapterChainBuilder<X,Y> myAdapters;
        
        /**
         * Creates an empty NotifierChainBuilder
         */
        public NotifierChainBuilder(){
            myAdapters = new AdapterChainBuilder<X, Y>();
        }
        
        /**
         * Set the internal Notifier
         * @param notifier the Internal notifier
         * @return the NotifierChainBuilder itself
         */
        public NotifierChainBuilder<X,Y> setNotifier(Notifier<X> notifier){
            myNotifier = notifier;
            return this;
        }

        /**
         * Add an Adapter
         * @param <T> intermediate data type
         * @param adapter Adapter to add
         * @return the NotifierChainBuilder itself
         */
        public <T> NotifierChainBuilder<X,T> attach(Adapter<Y,T> adapter){
            myAdapters.attach(adapter);
            return (NotifierChainBuilder<X,T>)this;
        }

        /**
         * Generates a new NotifierChain
         * @param <T> data type
         * @return new NotifierChain
         */
        public <T> NotifierChain<T,Y> done(){
            return new NotifierChain<T,Y>(myNotifier, myAdapters.done());
        }
    }
}
