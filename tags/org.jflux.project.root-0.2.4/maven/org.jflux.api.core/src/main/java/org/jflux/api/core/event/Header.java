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
package org.jflux.api.core.event;

import java.util.Map;
import org.jflux.api.core.Adapter;

/**
 * Interface for one possible kind of Event metadata
 * @author Matthew Stevenson <www.jflux.org>
 * @param <SourceRef> type of source reference
 * @param <Time> time format
 */
public interface Header<SourceRef, Time> {

    /**
     * Get the source reference
     * @return the source reference
     */
    public SourceRef getSourceReference();

    /**
     * Get the current time.
     * @return the current time
     */
    public Time getTimestamp();

    /**
     * Get the event type
     * @return the event type
     */
    public String getEventType();

    /**
     * Get the metadata fields
     * @return Map of metadata fields
     */
    public Map<String,String> getHeaderProperties();
    
    /**
     * Adapter to take a Header and return its event type
     */
    public static class HeaderTypeAdapter implements Adapter<Header,String> {

        /**
         * Takes a Header and returns its event type
         * @param a the Header
         * @return the event type
         */
        @Override 
        public String adapt(Header a) {
            return a == null ? null : a.getEventType();
        }
    }
    
    /**
     * Adapter to take a Header and return one of its property values
     */
    public static class HeaderPropertyAdapter implements 
            Adapter<Header,String> {
        private String myPropertyKey;

        /**
         * Builds a HeaderPropertyAdapter given a property key
         * @param propertyKey
         */
        public HeaderPropertyAdapter(String propertyKey){
            if(propertyKey == null){
                throw new NullPointerException();
            }
            myPropertyKey = propertyKey;
        }
        
        /**
         * Takes a header and return a property value according to the stored
         * property key
         * @param a the Header
         * @return a property value
         */
        @Override 
        public String adapt(Header a) {
            if(a == null || a.getHeaderProperties() == null){
                return null;
            }
            Map<String,String> props = a.getHeaderProperties();
            return props.get(myPropertyKey);
        }
    }
}
