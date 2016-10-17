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

import org.jflux.api.core.Adapter;

/**
 * Interface for an event with metadata
 * @author Matthew Stevenson <www.jflux.org>
 * @param <H> type of metadata
 * @param <D> type of data
 */
public interface Event<H, D> {

    /**
     * Gets the event's metadata
     * @return the metadata
     */
    public H getHeader();
    
    /**
     * Gets the event's data
     * @return the data
     */
    public D getData();
    
    /**
     * Adapter to strip out all but an event's metadata
     * @param <H> type of metadata
     * @param <D> type of data
     */
    public static class EventHeaderAdapter<H,D> implements Adapter<Event<H,D>,H> {

        /**
         * Returns an event's metadata
         * @param a the event
         * @return the metadata
         */
        @Override
        public H adapt(Event<H, D> a) {
            return a.getHeader();
        }
    }
    
    /**
     * Adapter to strip out all but an event's data
     * @param <H> type of metadata
     * @param <D> type of data
     */
    public static class EventDataAdapter<H,D> implements Adapter<Event<H,D>,D> {

        /**
         * Returns an event's data
         * @param a the event
         * @return the data
         */
        @Override
        public D adapt(Event<H, D> a) {
            return a.getData();
        }
    }
}
