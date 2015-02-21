/*
 * Copyright 2014 the JFlux Project.
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
package org.jflux.api.common.rk.utils;

import org.jflux.api.core.Listener;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public class EventRepeater<Msg> implements Listener<Msg> {
    private List<Listener<Msg>> myListeners;
    
    public EventRepeater(){
        myListeners = new ArrayList<Listener<Msg>>();
    }
    
    @Override
    public void handleEvent(Msg event) {
        for(Listener<Msg> l : myListeners){
            l.handleEvent(event);
        }
    }
    
    public void addListener(Listener<Msg> listener){
        if(listener == this){
            return;
        }
        if(!myListeners.contains(listener)){
            myListeners.add(listener);
        }
    }   
    
    public void removeListener(Listener<Msg> listener){
        myListeners.remove(listener);
    }
}
