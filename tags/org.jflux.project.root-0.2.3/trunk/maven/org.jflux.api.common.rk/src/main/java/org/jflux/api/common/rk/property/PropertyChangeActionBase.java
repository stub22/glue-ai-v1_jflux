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

import java.beans.PropertyChangeEvent;

/**
 * Base class for PropertyChangeActions to use with a PropertyChangeMonitor
 * @param <T> Type of PropertyChangeEvent accept
 * 
 * @author Matthew Stevenson <www.jflux.org>
 */
public abstract class PropertyChangeActionBase<T extends PropertyChangeEvent> {
    /**
     * Performs the action with the given event only if the event is an instance
     * of T
     * @param event event for the property being changed
     */
    public void performAction(PropertyChangeEvent event){
        if(!(getEventType().isInstance(event))){
            return;
        }
        T t = (T)event;
        run(t);
    }

    /**
     * Action to perform when a PropertyChangeEvent of type T is found
     * @param event
     */
    protected abstract void run(T event);

    /**
     * Returns the PropertyChangeEvent type accepted, T.
     * @return T
     */
    public abstract Class<T> getEventType();
}
