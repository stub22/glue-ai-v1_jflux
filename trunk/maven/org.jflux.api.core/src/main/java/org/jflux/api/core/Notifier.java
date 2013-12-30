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
package org.jflux.api.core;

/**
 * An interface for managing and coördinating a group of Listeners
 * @param <E> listener input type
 * @author Matthew Stevenson <www.jflux.org>
 */
public interface Notifier<E> {
    /**
     * Add a listener to the managed group.
     * @param listener
     */
    public void addListener(Listener<E> listener);
    /**
     * Remove a listener from the managed group.
     * @param listener
     */
    public void removeListener(Listener<E> listener);
    
    /**
     * Sends an object as input to all listeners in the group.
     * @param e input object
     */
    public void notifyListeners(E e);
}
