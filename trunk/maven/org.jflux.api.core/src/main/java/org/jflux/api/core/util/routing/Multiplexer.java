/*
 * Copyright 2012 by The JFlux Project (www.jflux.org).
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
package org.jflux.api.core.util.routing;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jflux.api.core.event.Event;
import org.jflux.api.core.util.Adapter;
import org.jflux.api.core.util.Listener;

/**
 *
 * @author Matthew Stevenson <www.robokind.org>
 */
public interface Multiplexer<E> extends Listener<E> {
    final static Logger theLogger = Logger.getLogger(Multiplexer.class.getName());
    
    public static class BasicMultiplexer<E> implements Multiplexer<E> {
        private Adapter<E,Listener<E>> myListenerFinder;
        
        public BasicMultiplexer(Adapter<E,Listener<E>> listenerProc){
            if(listenerProc == null){
                throw new NullPointerException();
            }
            myListenerFinder = listenerProc;
        }
        
        @Override
        public void handleEvent(E event) {
            Listener<E> listener = myListenerFinder.adapt(event);
            if(listener == null){
                return;
            }
            listener.handleEvent(event);
        }
        
    }
    
    public static class EventMultiplexer<H,D,E extends Event<H,D>> 
            implements Multiplexer<E> {
        private Adapter<H,Listener<E>> myListenerFinder;
        
        public EventMultiplexer(Adapter<H,Listener<E>> listenerProc){
            if(listenerProc == null){
                throw new NullPointerException();
            }
            myListenerFinder = listenerProc;
        }
        
        @Override
        public void handleEvent(E event) {
            if(event == null){
                theLogger.warning("Ignoring null event.");
                return;
            }
            H header = event.getHeader();    
            Listener<E> listener = myListenerFinder.adapt(header);
            if(listener == null){
                theLogger.log(Level.WARNING, 
                        "Could not find listener for event: {0}", event);
                return;
            }
            listener.handleEvent(event);
        }
    }
    
    public static class EventDataMultiplexer<H,D,E extends Event<H,D>> 
            implements Multiplexer<E> {
        private Adapter<H,Listener<D>> myListenerFinder;
        
        public EventDataMultiplexer(Adapter<H,Listener<D>> listenerProc){
            if(listenerProc == null){
                throw new NullPointerException();
            }
            myListenerFinder = listenerProc;
        }
        
        @Override
        public void handleEvent(E event) {
            if(event == null){
                theLogger.warning("Ignoring null event.");
                return;
            }
            H header = event.getHeader();
            D data = event.getData();            
            Listener<D> listener = myListenerFinder.adapt(header);
            if(listener == null){
                theLogger.log(Level.WARNING, 
                        "Could not find listener for event: {0}", event);
                return;
            }
            listener.handleEvent(data);
        }
    }
}
