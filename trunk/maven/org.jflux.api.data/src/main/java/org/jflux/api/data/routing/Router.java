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
package org.jflux.api.data.routing;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jflux.api.core.Adapter;
import org.jflux.api.core.Listener;
import org.jflux.api.core.util.MapAdapter;

/**
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public interface Router<T> extends Listener<T> {
    final static Logger theLogger = Logger.getLogger(Router.class.getName());
    
    public static class DefaultRouter<K,T> implements Router<T> {
        private Adapter<T,K> myKeyAdapter;
        private MapAdapter<K,Listener<T>> myRouteMap;
        
        public DefaultRouter(Adapter<T,K> keyAdapter){
            if(keyAdapter == null){
                throw new NullPointerException();
            }
            myRouteMap = new MapAdapter<K, Listener<T>>();
            myKeyAdapter = keyAdapter;
        }
        
        @Override
        public void handleEvent(T event) {
            //theLogger.log(Level.INFO, "Routing item: {0}", event);
            K key = myKeyAdapter.adapt(event);
            Listener<T> route = myRouteMap.adapt(key);
            route.handleEvent(event);
        }
        
        public void addRoute(K key, Listener<T> route){
            if(myRouteMap.getMap().containsKey(key)){
                theLogger.log(Level.INFO, 
                        "Replacing existing route for key: {0}", key);
            }else{
                theLogger.log(Level.INFO, 
                        "Adding router path for key: {0}", key);
            }
            myRouteMap.getMap().put(key, route);
        }
        
        public void removeRoute(K key){
            theLogger.log(Level.INFO, "Removing router path for key: {0}", key);
            myRouteMap.getMap().remove(key);
        }
    }
}
