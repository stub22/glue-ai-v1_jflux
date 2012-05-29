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
import org.jflux.api.core.playable.PlayableNotifier;
import org.jflux.api.core.playable.PlayableNotifier.DefaultPlayableNotifier;

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
            DefaultPlayableNotifier<B> implements PlayableNotifier<B> {
        private final A myInput;
        private final Adapter<A,B> myAdapter;
        private final Thread myThread;
        
        //TODO: Replace Thread with ScheduledFuture
        public FutureAdapt(A input, Adapter<A, B> adapter) {
            myInput = input;
            myAdapter = adapter;
            myThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    B b = myAdapter.adapt(myInput);
                    notifyListeners(b);
                    complete();
                }
            });
        }
        
        @Override
        public boolean start() {
            if(Thread.State.NEW != myThread.getState() || !super.start()){
                return false;
            }
            myThread.start();
            return true;
        }

        @Override
        public boolean pause() {
            if(PlayState.PAUSED == getPlayState()){
                return true;
            }else if(PlayState.RUNNING != getPlayState()){
                return false;
            }else if(!super.pause()){
                return false;
            }
            myThread.setPriority(Thread.MIN_PRIORITY);
            return true;
        }

        @Override
        public boolean resume() {
            if(PlayState.PAUSED != getPlayState() || !super.pause()){
                return false;
            }
            myThread.setPriority(Thread.NORM_PRIORITY);
            return true;
        }

        @Override
        public boolean stop() {
            if(Thread.State.TERMINATED == myThread.getState()){
                return true;
            }else if(!super.stop()){
                return false;
            }
            myThread.interrupt();
            return true;
        }
        
        @Override
        protected boolean complete() {
            return super.complete();
        }
        
    }
}
