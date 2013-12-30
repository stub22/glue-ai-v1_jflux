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

import java.util.List;
import org.jflux.api.core.Listener;
import org.jflux.api.core.Notifier;
import org.jflux.api.core.node.ProcessorNode;
import org.jflux.api.core.playable.BasicPlayable;
import org.jflux.api.core.playable.ConditionalListener;
import org.jflux.api.core.playable.ConditionalNotifier;

/**
 *
 * @param <T> 
 * @author Matthew Stevenson
 */
public class IteratorNode<T> extends 
        BasicPlayable implements ProcessorNode<List<T>, T> {
    private Listener<List<T>> myListener;
    private Notifier<T> myNotifier;
    
    /**
     *
     */
    public IteratorNode(){
        IteratingNotifier<T> in = new IteratingNotifier<T>();
        myListener = new ConditionalListener<List<T>>(this, in);
        myNotifier = new ConditionalNotifier<T>(this, in);
    }    
    
    /**
     *
     * @return
     */
    @Override
    public Listener<List<T>> getListener() {
        return myListener;
    }

    /**
     *
     * @return
     */
    @Override
    public Notifier<T> getNotifier() {
        return myNotifier;
    }
    
    /**
     *
     * @param <T>
     */
    public static class IteratingNotifier<T> extends 
            DefaultNotifier<T> implements Listener<List<T>>{

        /**
         *
         * @param event
         */
        @Override
        public void handleEvent(List<T> event) {
            for(T t : event){
                notifyListeners(t);
            }
        }
    }
}

