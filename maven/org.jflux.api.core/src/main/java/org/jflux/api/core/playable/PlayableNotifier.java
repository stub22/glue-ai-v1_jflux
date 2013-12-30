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
package org.jflux.api.core.playable;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jflux.api.core.Listener;
import org.jflux.api.core.Notifier;
import org.jflux.api.core.util.DefaultNotifier;

/**
 *
 * @param <T> 
 * @author Matthew Stevenson
 */
public interface PlayableNotifier<T> extends Notifier<T>, Playable{
        
    /**
     *
     * @param <T>
     */
    public static class DefaultPlayableNotifier<T> extends
            BasicPlayable implements PlayableNotifier<T> {
        private final static Logger theLogger = 
                Logger.getLogger(DefaultPlayableNotifier.class.getName());
        
        private Notifier<T> myNotifier;
        
        /**
         *
         * @param notifier
         */
        public DefaultPlayableNotifier(Notifier<T> notifier){
            if(notifier == null){
                throw new NullPointerException();
            }
            myNotifier = notifier;
        }
        
        /**
         *
         */
        public DefaultPlayableNotifier(){
            myNotifier = new DefaultNotifier<T>();
        }
        
        /**
         *
         * @return
         */
        public Notifier<T> getNotifier(){
            return myNotifier;
        }
        
        /**
         *
         * @param listener
         */
        @Override
        public void addListener(Listener<T> listener) {
            myNotifier.addListener(listener);
        }

        /**
         *
         * @param listener
         */
        @Override
        public void removeListener(Listener<T> listener) {
            myNotifier.removeListener(listener);
        }

        /**
         *
         * @param e
         */
        @Override
        public void notifyListeners(T e) {
            if(getPlayState() == Playable.PlayState.RUNNING){
                myNotifier.notifyListeners(e);
            }else{
                theLogger.log(Level.INFO, 
                        "PlayState is: {0}, ignoring event: {1}", 
                        new Object[]{getPlayState(), e});
            }
            myNotifier.notifyListeners(e);
        }        
    }
}
