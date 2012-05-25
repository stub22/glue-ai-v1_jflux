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
 * @author Matthew Stevenson <www.jflux.org>
 */
public interface Event<H, D> {
    public H getHeader();
    
    public D getData();
    
    public static class EventHeaderAdapter<H> implements Adapter<Event<H,?>,H> {
        @Override
        public H adapt(Event<H, ?> a) {
            return a.getHeader();
        }
    }
    
    public static class EventDataAdapter<D> implements Adapter<Event<?,D>,D> {
        @Override
        public D adapt(Event<?, D> a) {
            return a.getData();
        }
    }
}
