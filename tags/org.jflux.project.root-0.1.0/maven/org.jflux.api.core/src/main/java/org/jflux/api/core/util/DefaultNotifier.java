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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public class DefaultNotifier<E> implements Notifier<E> {
    protected List<Listener<E>> myListeners;
    
    public DefaultNotifier(){
        myListeners = new ArrayList<Listener<E>>();
    }
    
    @Override
    public void notifyListeners(E e){
        for(Listener<E> l : myListeners){
            l.handleEvent(e);
        }
    }
    
    @Override
    public void addListener(Listener<E> listener) {
        if(listener == null){
            throw new NullPointerException();
        }
        if(!myListeners.contains(listener)){
            myListeners.add(listener);
        }
    }

    @Override
    public void removeListener(Listener<E> listener) {
        if(listener == null){
            throw new NullPointerException();
        }
        myListeners.remove(listener);
    }
    
}
