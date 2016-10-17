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

import org.jflux.api.core.Listener;

/**
 * Notifier to process a collection of events
 * @author Matthew Stevenson
 * @param <T> event type
 */
public class IteratingNotifier<T> extends 
        DefaultNotifier<T> implements Listener<Iterable<T>>{

    /**
     * Receives a collection of events and notifies for each one
     * @param event collection of events
     */
    @Override
    public void handleEvent(Iterable<T> event) {
        for(T t : event){
            notifyListeners(t);
        }
    }
    
    /**
     * Notifier to process an array of events
     * @param <T> event type
     */
    public static class IteratingArrayNotifier<T> extends 
            DefaultNotifier<T> implements Listener<T[]>{

        /**
         * Receives an array of events and notifies for each one
         * @param event
         */
        @Override
        public void handleEvent(T[] event) {
            for(T t : event){
                notifyListeners(t);
            }
        }
    }
}
