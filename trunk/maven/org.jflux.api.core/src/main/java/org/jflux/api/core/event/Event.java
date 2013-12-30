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
 *
 * @param <H> 
 * @param <D> 
 * @author Matthew Stevenson <www.jflux.org>
 */
public interface Event<H, D> {
    /**
     *
     * @return
     */
    public H getHeader();
    
    /**
     *
     * @return
     */
    public D getData();
    
    /**
     *
     * @param <H>
     * @param <D>
     */
    public static class EventHeaderAdapter<H,D> implements Adapter<Event<H,D>,H> {
        /**
         *
         * @param a
         * @return
         */
        @Override
        public H adapt(Event<H, D> a) {
            return a.getHeader();
        }
    }
    
    /**
     *
     * @param <H>
     * @param <D>
     */
    public static class EventDataAdapter<H,D> implements Adapter<Event<H,D>,D> {
        /**
         *
         * @param a
         * @return
         */
        @Override
        public D adapt(Event<H, D> a) {
            return a.getData();
        }
    }
}
