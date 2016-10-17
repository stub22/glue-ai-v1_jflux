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


package org.jflux.api.common.rk.property;

import java.beans.PropertyChangeListener;

/**
 * Interface defining methods for Classes which fire property change events.
 * 
 * @author Matthew Stevenson <www.jflux.org>
 */
public interface PropertyChangeSource {
    /**
     * Adds a listener to be notified for all property changes.
     * @param listener PropertyChangeListener to add
     */
    public void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Removes a listener
     * @param listener PropertyChangeListener to remove
     */
    public void removePropertyChangeListener(PropertyChangeListener listener);

    /**
     * Adds a listener for a specific property change event.
     * @param propertyName name of the event to listen for
     * @param listener the PropertyChangeListener to notify
     */
    public void addPropertyChangeListener( String propertyName, PropertyChangeListener listener);

    /**
     * Removes a listener from listening to a specific property change event.
     * @param propertyName name of the event
     * @param listener the PropertyChangeListener to remove
     */
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);
}
