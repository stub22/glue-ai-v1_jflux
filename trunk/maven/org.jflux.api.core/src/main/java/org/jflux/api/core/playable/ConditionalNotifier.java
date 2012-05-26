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
package org.jflux.api.core.playable;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jflux.api.core.Listener;
import org.jflux.api.core.Notifier;

/**
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public class ConditionalNotifier<E> implements Notifier<E> {
    private final static Logger theLogger = Logger.getLogger(ConditionalListener.class.getName());
    private Playable myPlayable;
    private Notifier<E> myNotifier;
    
    public ConditionalNotifier(Playable p, Notifier<E> n){
        if(p == null || n == null){
            throw new NullPointerException();
        }
        myPlayable = p;
        myNotifier = n;
    }

    @Override
    public void notifyListeners(E e) {
        if(myPlayable.getPlayState() == Playable.PlayState.RUNNING){
            myNotifier.notifyListeners(e);
        }else{
            theLogger.log(Level.INFO, 
                    "PlayState is: {0}, ignoring event: {1}", 
                    new Object[]{myPlayable.getPlayState(), e});
        }
    }

    @Override
    public void addListener(Listener<E> listener) {
        myNotifier.addListener(listener);
    }

    @Override
    public void removeListener(Listener<E> listener) {
        myNotifier.removeListener(listener);
    }
}
