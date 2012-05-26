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
package org.jflux.api.data.concurrent;

import org.jflux.api.core.Adapter;
import org.jflux.api.core.Listener;
import org.jflux.api.core.playable.BasicPlayable;
import org.jflux.api.core.playable.PlayableNotifier;

/**
 *
 * @author Matthew Stevenson
 */
public class AsyncAdapter<A,B> implements Adapter<A,PlayableNotifier<B>>{
    private Adapter<A,B> myAdapter;

    public AsyncAdapter(Adapter<A,B> adapter){
        if(adapter == null){
            throw new NullPointerException();
        }
    }
    
    @Override
    public PlayableNotifier<B> adapt(A a) {
        return new FutureAdapt<A, B>(a, myAdapter);
    }
    
    private class FutureAdapt<A,B> extends 
            BasicPlayable implements PlayableNotifier<B> {
        private final A myInput;
        private final Adapter<A,B> myAdapter;
        private Listener<B> myListener;

        public FutureAdapt(A input, Adapter<A, B> adapter) {
            myInput = input;
            myAdapter = adapter;
        }
        
        @Override
        public boolean start() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    B b = myAdapter.adapt(myInput);
                    notifyListeners(b);
                    complete();
                }
            }).start();
            return true;
        }

        @Override
        public synchronized void addListener(Listener<B> listener) {
            if(myListener != null){
                throw new IllegalStateException("Only one Listener allowed.");
            }
            myListener = listener;
        }

        @Override
        public synchronized void removeListener(Listener<B> listener) {
            if(myListener == listener){
                myListener = null;
            }
        }

        @Override
        public void notifyListeners(B e) {
            Listener<B> l = myListener;
            if(l != null){
                l.handleEvent(e);
            }
        }

        @Override
        public boolean pause() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean resume() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean stop() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
        @Override
        protected boolean complete() {
            return super.complete();
        }
        
    }
}
