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
package org.jflux.api.core.event;

import org.jflux.api.core.Adapter;
import org.jflux.api.core.Source;
import org.jflux.api.core.util.SourceAdapter;

/**
 * Simple implementation of an Event
 * @author Matthew Stevenson
 * @param <H> type of metadata
 * @param <D> type of data
 */
public class BasicEvent<H,D> implements Event<H,D> {
    private H myHeader;
    private D myData;
    
    /**
     * Builds an Event from metadata and data
     * @param header the metadata
     * @param data the data
     */
    public BasicEvent(H header, D data){
        myHeader = header;
        myData = data;
    }
    
    /**
     * Gets the event's metadata
     * @return the metadata
     */
    @Override
    public H getHeader() {
        return myHeader;
    }

    /**
     * Gets the event's data
     * @return the data
     */
    @Override
    public D getData() {
        return myData;
    }
    
    /**
     * Adapter to generate an Event from data alone
     * @param <H> type of metadata
     * @param <D> type of data
     */
    public static class BasicEventFactory<H,D> implements Adapter<D,Event<H,D>> {
        private Adapter<D,H> myHeaderFactory;
        
        /**
         * Builds a new factory using an Adapter to generate the metadata
         * @param headerFactory
         */
        public BasicEventFactory(Adapter<D,H> headerFactory){
            if(headerFactory == null){
                throw new NullPointerException();
            }
            myHeaderFactory = headerFactory;
        }
        
        /**
         * Builds a new factory using a Source to generate the metadata
         * @param headerSource Source generating metadata
         */
        public BasicEventFactory(Source<H> headerSource){
            this(new SourceAdapter<D, H>(headerSource));
        }
        
        /**
         * Sets the Adapter generating the metadata
         * @param headerFeactory Adapter generating metadata
         */
        public void setHeaderFactory(Adapter<D,H> headerFeactory){
            myHeaderFactory = headerFeactory;
        }
        
        /**
         * Sets the Source generating the metadata
         * @param headerSource Source generating metadata
         */
        public void setHeaderSource(Source<H> headerSource){
            if(headerSource == null){
                myHeaderFactory = null;
            }else{
                myHeaderFactory = new SourceAdapter<D, H>(headerSource);
            }
        }
        
        /**
         * Turns a piece of data into a BasicEvent
         * @param a the data
         * @return the event
         */
        @Override
        public Event<H, D> adapt(D a) {
            if(myHeaderFactory == null){
                return null;
            }
            H header = myHeaderFactory.adapt(a);
            return new BasicEvent<H, D>(header, a);
        }
        
    }
}
