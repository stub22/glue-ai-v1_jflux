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
package org.jflux.api.core.chain;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.jflux.api.core.Adapter;
import org.jflux.api.core.Listener;
import org.jflux.api.core.Notifier;

/**
 * Utility class for chains
 * @author Matthew Stevenson
 */
public class ChainUtils {

    /**
     * Break a Listener into components
     * @param l the Listener
     * @return ChainComponents
     */
    public static ChainComponents explode(Listener l){
        return new ChainComponents(l);
    }

    /**
     * Break a Notifier into components
     * @param n the Notifier
     * @return ChainComponents
     */
    public static ChainComponents explode(Notifier n){
        return new ChainComponents(n);
    }

    /**
     * Break an Adapter into components
     * @param a the Adapter
     * @return ChainComponents
     */
    public static ChainComponents explode(Adapter a){
        return new ChainComponents(a);
    }
    
    /**
     * Represents the components of a chain
     */
    public static class ChainComponents {
        private Listener myListener;
        private Notifier myNotifier;
        private List<Adapter> myAdapters;
        
        /**
         * Converts a Listener into components
         * @param l Listener
         */
        public ChainComponents(Listener l){
            if(l == null){
                myAdapters = Collections.EMPTY_LIST;
            }else if(l instanceof ListenerChain){
                ListenerChain lc = ((ListenerChain)l);
                myListener = lc.getInnerListener();
                setAdapter(lc.getInnerAdapter());
            }else{
                myListener = l;
            }
        }
        
        /**
         * Converts a Notifier into components
         * @param n Notifier
         */
        public ChainComponents(Notifier n){
            if(n == null){
                myAdapters = Collections.EMPTY_LIST;
            }else if(n instanceof NotifierChain){
                NotifierChain nc = ((NotifierChain)n);
                myNotifier = nc.getInnerNotifier();
                setAdapter(nc.getInnerAdapter());
            }else{
                myNotifier = n;
            }
        }
        
        /**
         * Converts an Adapter into components
         * @param a Adapter
         */
        public ChainComponents(Adapter a){
            if(a == null){
                myAdapters = Collections.EMPTY_LIST;
            }else{
                setAdapter(a);
            }
        }
        
        private void setAdapter(Adapter a){
            if(a == null){
                myAdapters = Collections.EMPTY_LIST;
            }else if(a instanceof AdapterChain){
                AdapterChain ac = ((AdapterChain)a);
                myAdapters = ac.getAdapters();
            }else{
                myAdapters = Arrays.asList(a);
            }
        }
        
        /**
         * Gets the Listener component
         * @return Listener component
         */
        public Listener getListener(){
            return myListener;
        }
        
        /**
         * Gets the Notifier component
         * @return Notifier component
         */
        public Notifier getNotifier(){
            return myNotifier;
        }
        
        /**
         * Gets all Adapter components
         * @return List of Adapter components
         */
        public List<Adapter> getAdapterList(){
            return myAdapters;
        }
        
        /**
         * Gets one Adapter component by index
         * @param index Adapter index
         * @return Adapter component
         */
        public Adapter getAdapter(int index){
            return myAdapters.get(index);
        }
        
        /**
         * Gets number of Adapters
         * @return number of Adapters
         */
        public int getAdapterCount(){
            return myAdapters.size();
        }
    }
}
