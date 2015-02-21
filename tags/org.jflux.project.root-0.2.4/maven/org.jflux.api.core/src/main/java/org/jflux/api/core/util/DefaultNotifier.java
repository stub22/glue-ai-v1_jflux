/*
 *  Copyright 2012 by The JFlux Project (www.jflux.org).
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
import org.jflux.api.core.Notifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Basic Notifier implementation
 * @author Matthew Stevenson <www.jflux.org>
 * @param <E> event type
 */
public class DefaultNotifier<E> implements Notifier<E> {

    /**
     * List of managed Listeners
     */
    protected List<Listener<E>> myListeners;
    
    /**
     * Builds an empty DefaultNotifier
     */
    public DefaultNotifier(){
        myListeners = new ArrayList<Listener<E>>();
    }
    
    /**
     * Forwards event to all Listeners
     * @param e event to forward
     */
    @Override
    public void notifyListeners(E e){
        for(Listener<E> l : myListeners){
            l.handleEvent(e);
        }
    }
    
    /**
     * Adds a Listener to the managed group
     * @param listener Listener to add
     */
    @Override
    public void addListener(Listener<E> listener) {
        if(listener == null){
            throw new NullPointerException();
        }
        if(!myListeners.contains(listener)){
            myListeners.add(listener);
        }
    }

    /**
     * Removes a Listener from the managed group
     * @param listener Listener to remove
     */
    @Override
    public void removeListener(Listener<E> listener) {
        if(listener == null){
            throw new NullPointerException();
        }
        myListeners.remove(listener);
    }
    
}
