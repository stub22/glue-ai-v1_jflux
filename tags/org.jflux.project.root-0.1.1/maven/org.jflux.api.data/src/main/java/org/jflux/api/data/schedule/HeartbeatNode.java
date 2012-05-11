/*
 * Copyright 2011 Hanson Robokind LLC.
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
package org.jflux.api.data.schedule;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jflux.api.core.node.DefaultProducerNode;
import org.jflux.api.core.playable.Playable.PlayState;
import org.jflux.api.core.util.DefaultNotifier;
import org.jflux.api.core.util.Factory;
import org.jflux.api.core.util.Notifier;

/**
 *
 * @author Matthew Stevenson <www.robokind.org>
 */
public class HeartbeatNode<M> extends 
        DefaultProducerNode<M> implements ScheduleNode<M,TimeUnit> {
    private final static Logger theLogger = Logger.getLogger(HeartbeatNode.class.getName());
    
    private Factory<M> myFactory;
    private ScheduledExecutorService myExecutor;
    private long myInitialDelay;
    private long myPeriod;
    private TimeUnit myTimeUnit;
    private ScheduledFuture<?> myFuture;
    
    public HeartbeatNode(Class<M> msgClass, Factory<M> factory,
            long initialDelay, long period, TimeUnit timeUnit){
        super(msgClass, new DefaultNotifier<M>());
        if(factory == null || timeUnit == null){
            throw new NullPointerException();
        }
        myInitialDelay = initialDelay;
        myPeriod = period;
        myTimeUnit = timeUnit;
        myFactory = factory;
    }
    
    public synchronized void setPeriod(long period){
        myPeriod = period;
        if(PlayState.RUNNING != getPlayState()){
            return;
        }
        long delay = myInitialDelay;
        myInitialDelay = 0L;
        pause();
        start();
        myInitialDelay = delay;
    }

    @Override
    public boolean start() {
        if(myFuture != null){
            if(myFuture.isDone()){
                myFuture = null;
            }else{
                return true;
            }
        }
        if(myExecutor == null){
            myExecutor = new ScheduledThreadPoolExecutor(1);   
        }
        myFuture = myExecutor.scheduleAtFixedRate(
                new MessagePump(), myInitialDelay, myPeriod, myTimeUnit);
        return super.start();
    }

    @Override
    public boolean pause() {
        if(myFuture != null){
            myFuture.cancel(false);
            myFuture = null;
        }
        return super.pause();
    }

    @Override
    public boolean resume() {
        return start();
    }

    @Override
    public boolean stop() {
        if(myFuture != null){
            myFuture.cancel(false);
            myFuture = null;
            myExecutor.shutdown();
            myExecutor = null;
        }
        return super.stop();
    }
    
    class MessagePump implements Runnable {

        @Override
        public void run() {
            if(myFactory == null || getPlayState() != PlayState.RUNNING){
                return;
            }
            M t = myFactory.build();
            Notifier<M> n = getNotifier();
            if(n == null || t == null){
                return;
            }
            try{
                n.notifyListeners(t);
            }catch(RuntimeException ex){
                theLogger.log(Level.WARNING, 
                        "Runtime Exception in Heartbeat Node", ex);
            }
        }
        
    }
}
